package university.controllers

import common.controllers.AbstractBaseController
import common.services.DbActionRunner
import javax.inject.Inject
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import play.api.{Configuration, Logger}
import student.controllers.authentication_middleware.AuthenticatedActionBuilder
import university.models.UniversityWithCompleteInfo
import university.services.{UniversityQueryExternalProvider, UniversityQueryProvider}

import scala.concurrent.ExecutionContext

case class PostFormInput(title: String, body: String)

/**
 * Takes HTTP requests and produces JSON.
 */
class UniversityController (cc: ControllerComponents,
  universityQueryProvider: UniversityQueryProvider,
  universityQueryExternalProvider: UniversityQueryExternalProvider,
  authenticatedAction: AuthenticatedActionBuilder,
  actionRunner: DbActionRunner)(
  implicit ec: ExecutionContext)
  extends AbstractBaseController(cc) {

  private val logger = Logger(getClass)

  def getAll = authenticatedAction.async {request =>
    actionRunner.run(universityQueryProvider.getAllUniversitiesWithCompleteInfo)
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def getAllFromPublicAPI = authenticatedAction.async {request =>
    universityQueryExternalProvider.getAllUniversitiesWithCompleteInfo()
      .map({
        case Left(value) => {
          val violation = JsObject(Map("timeout exception" -> Json.toJson(value.toString())))
          val response = JsObject(Map("errors" -> violation))
          UnprocessableEntity(response)
        }
        case Right(value) => {
          Ok(Json.toJson(value))
        }
      })
  }

//  private val form: Form[PostFormInput] = {
//    import play.api.data.Forms._
//
//    Form(
//      mapping(
//        "title" -> nonEmptyText,
//        "body" -> text
//      )(PostFormInput.apply)(PostFormInput.unapply)
//    )
//  }
//
//  def index: Action[AnyContent] = PostAction.async { implicit request =>
//    logger.trace("index: ")
//    postResourceHandler.find.map { posts =>
//      Ok(Json.toJson(posts))
//    }
//  }
//
//  def process: Action[AnyContent] = PostAction.async { implicit request =>
//    logger.trace("process: ")
//    processJsonPost()
//  }
//
//  def show(id: String): Action[AnyContent] = PostAction.async {
//    implicit request =>
//      logger.trace(s"show: id = $id")
//      postResourceHandler.lookup(id).map { post =>
//        Ok(Json.toJson(post))
//      }
//  }
//
//  private def processJsonPost[A]()(
//    implicit request: PostRequest[A]): Future[Result] = {
//    def failure(badForm: Form[PostFormInput]) = {
//      Future.successful(BadRequest(badForm.errorsAsJson))
//    }
//
//    def success(input: PostFormInput) = {
//      postResourceHandler.create(input).map { post =>
//        Created(Json.toJson(post)).withHeaders(LOCATION -> post.link)
//      }
//    }
//
//    form.bindFromRequest().fold(failure, success)
//  }
}
