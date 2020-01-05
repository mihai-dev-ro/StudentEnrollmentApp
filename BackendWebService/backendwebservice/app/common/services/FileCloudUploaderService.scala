package common.services

import play.api.Configuration
import akka.NotUsed
import akka.stream.scaladsl.FileIO
import akka.stream.{IOResult, Materializer}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.zengularity.benji._
import student.models.StudentId
import play.api.libs.ws.DefaultBodyWritables._

import scalaz.Applicative

import scala.concurrent.{ExecutionContext, Future}

class FileCloudUploaderService(
  benji: ObjectStorage,
  config: Configuration
)(implicit ex: ExecutionContext,
  materializer: Materializer
) {

  def getBucketNameForStudent(studentId: StudentId) = s"student-${studentId.value}"

  def getBucket(studentId: StudentId) = {
    val bucketName = getBucketNameForStudent(studentId)
    val bucket = benji.bucket(bucketName)

    Future.successful(bucket)
      .flatMap(_.exists)
      .flatMap(result => {
        if (result) Future.successful(bucket)
        else bucket.create(failsIfExists = false).map(_ => bucket)
      })
  }

  def getAllBuckets() = {
    benji.buckets.collect[List]()
  }

  def getBucketObjects(studentId: StudentId, maybeBatchSize: Option[Long] ) = {
    val objects = benji.bucket(getBucketNameForStudent(studentId)).objects
    val withBatchSize = maybeBatchSize.fold(objects)(batchSize => {
      objects.withBatchSize(batchSize)
    })
    withBatchSize.collect[List]().map(_.map(_.name))
  }

  def createObject(
    bucket: BucketRef,
    sourceFile: Source[ByteString, Future[IOResult]],
    sourceFileName: String) = {

    sourceFile runWith bucket.obj(sourceFileName.toString).put[ByteString]
  }

  def getObject(bucket: BucketRef, objectName: String) = {
    bucket.obj(objectName).get()
  }

  def getObjectMetadata(bucket: BucketRef, objectName: String) = {
    bucket.obj(objectName).metadata()
  }
}
