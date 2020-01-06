package student.controllers

import akka.stream.scaladsl.FileIO
import com.zengularity.benji.ObjectStorage
import com.zengularity.benji.exception.ObjectNotFoundException
import common.controllers.AbstractBaseController
import common.exceptions.MissingModelException
import common.services.{DbActionRunner, FileCloudUploaderService}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, MultipartFormData}
import student.controllers.authentication_middleware.AuthenticatedActionBuilder
import student.exceptions.FileValidationException
import student.models.{NewStudentApplication, StudentApplicationFile, StudentApplicationForUpdating, StudentApplicationId, StudentApplicationStartWrapper}
import student.services.StudentApplicationService
import university.models.UniversityId
import university.services.{UniversityQueryExternalProvider, UniversityQueryProvider, UniversityUpdateService}

import scala.concurrent.{ExecutionContext, Future}

class StudentApplicationController(
  controllerComponents: ControllerComponents,
  studentApplicationService: StudentApplicationService,
  universityQueryProvider: UniversityQueryProvider,
  universityQueryExternalProvider: UniversityQueryExternalProvider,
  universityUpdateService: UniversityUpdateService,
  authenticatedAction: AuthenticatedActionBuilder,
  dbActionRunner: DbActionRunner,
  fileCloudUploaderService: FileCloudUploaderService)(
  implicit ec: ExecutionContext
) extends AbstractBaseController(controllerComponents) {

  def startApplication: Action[StudentApplicationStartWrapper] =
    authenticatedAction.async(validateJson[StudentApplicationStartWrapper]) {
      request => {

        val studentId = request.user.studentId
        val universityName = request.body.universityName
        val universityCountryCode = request.body.universityCountryCode

        def loadAndUpdateFromPublicAPI(universityName: String, universityCountryCode: String) = {
          for {
            universityFromPublicAPI <- universityQueryExternalProvider
              .getUniversityWithCompleteInfo(universityName, universityCountryCode)
            universityAdded <- dbActionRunner.runTransactionally(
              universityUpdateService.addUniversity(universityFromPublicAPI))
          } yield universityAdded
        }

        def getUniversity = {
          dbActionRunner.runTransactionally(
            universityQueryProvider.getUniversityWithCompleteInfoOption(
              universityName, universityCountryCode))
            .flatMap(maybeUniversity => maybeUniversity match {
              case Some(university) => Future.successful(university)
              case None => loadAndUpdateFromPublicAPI(universityName, universityCountryCode)
            })
        }

        val action = for {
          university <- getUniversity
          studentApplication <- dbActionRunner.runTransactionally(
            studentApplicationService.startNewApplication(studentId, university.id))
        } yield studentApplication

        action
          .map(NewStudentApplication(_))
          .map(Json.toJson(_))
          .map(Ok(_))
          .recover(handleFailedValidation)
      }
    }

  def showAllApplications: Action[AnyContent] = authenticatedAction.async {
    request => {
      val studentId = request.user.studentId
      dbActionRunner
        .runTransactionally(studentApplicationService.loadApplicationsForStudent(studentId))
        .map(Json.toJson(_))
        .map(Ok(_))
    }
  }

  def showApplication(id: StudentApplicationId): Action[AnyContent] = authenticatedAction.async {
    request => {
      val studentId = request.user.studentId
      dbActionRunner
        .runTransactionally(studentApplicationService.loadApplication(id))
        .map(Json.toJson(_))
        .map(Ok(_))
        .recover(handleFailedValidation.orElse({
          case _: MissingModelException => NotFound
        }))
    }
  }

  def updateApplication(id: StudentApplicationId): Action[StudentApplicationForUpdating] =
    authenticatedAction.async(validateJson[StudentApplicationForUpdating]) {
      request =>
        val studentApp = request.body
        dbActionRunner
          .runTransactionally(studentApplicationService.updateApplication(id, studentApp))
          .map(Json.toJson(_))
          .map(Ok(_))
          .recover(handleFailedValidation.orElse({
            case _: MissingModelException => NotFound
          }))
    }

  def uploadFile = authenticatedAction.async(parse.multipartFormData) {
    request =>
      val studentId = request.user.studentId

      fileCloudUploaderService.getBucket(studentId)
        .map(bucket => request.body.files.map(file =>
          fileCloudUploaderService.createObject(bucket, FileIO.fromPath(file.ref.path),
            file.filename)
        ))
        .flatMap(files => {
          if (files.isEmpty)
            Future.successful(BadRequest("Fisier lipsa"))
          else
            Future.sequence(files).map(_ => {
              Ok(s"Documentele ${request.body.files.map(_.filename).reduce(_ ++ "," ++ _)} " +
                s"au fost salvate")
            })
        })
  }

  def uploadFileForApplication(studentApplicationId: StudentApplicationId) =
    authenticatedAction.async(parse.multipartFormData) { request =>
      val studentId = request.user.studentId

      def validateFiles() = {
        if (request.body.files.size != 1)
          Future.failed(FileValidationException("Maxim un singur document acceptat"))
        else {
          request.body.files.headOption match {
            case Some(file) => Future.successful(file)
            case None => Future.failed(FileValidationException("Fisier lipsa"))
          }
        }
      }

      val action = for {
        bucket <- fileCloudUploaderService.getBucket(studentId)
        file <- validateFiles
        _ <- fileCloudUploaderService.createObject(
          bucket, FileIO.fromPath(file.ref.path), file.filename)
        studentApplicationUpdated <- dbActionRunner.runTransactionally(
          studentApplicationService.updateApplicationFile(studentApplicationId,
            StudentApplicationFile(Some(file.filename)))
        )
      } yield studentApplicationUpdated

      action
        .map(Json.toJson(_)).map(Ok(_))
        .recover({
          case e: FileValidationException => BadRequest(e.message)
        })

  }

  def getFile(fileName: Option[String]) = authenticatedAction.async {
    request =>

      val studentId = request.user.studentId
      fileCloudUploaderService.getBucket(studentId)
        .map(bucket => fileCloudUploaderService.getObject(bucket, fileName.getOrElse("__none__")))
        .map(Ok.chunked(_))
        .recover({
          case _: ObjectNotFoundException => NotFound
        })
  }

  def getFileMetadata(fileName: Option[String]) = authenticatedAction.async {
    request =>
      val studentId = request.user.studentId

      fileCloudUploaderService.getBucket(studentId)
        .flatMap(bucket =>
          fileCloudUploaderService.getObjectMetadata(bucket, fileName.getOrElse("__none__")))
        .map(meta =>
          NoContent.withHeaders(meta.toSeq.flatMap {
            case (name, values) => values.map(name -> _)
          }: _*)
        )
        .recover({
          case _: ObjectNotFoundException => NotFound
        })
  }

  def submitApplication(id: StudentApplicationId): Action[AnyContent] = {
    authenticatedAction.async {
      dbActionRunner
        .runTransactionally(studentApplicationService.submitApplication(id))
        .map(Json.toJson(_))
        .map(Ok(_))
        .recover({
          case _: MissingModelException => NotFound
        })
    }
  }
}
