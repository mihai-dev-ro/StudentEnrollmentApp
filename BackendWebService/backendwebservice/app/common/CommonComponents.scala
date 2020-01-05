package common

import akka.stream.Materializer
import com.softwaremill.macwire.wire
import com.zengularity.benji.ObjectStorage
import common.config.WithExecutionContextComponents
import common.repositories.{DateTimeProvider, DbConfigHelper}
import common.services.{DbActionRunner, FileCloudUploaderService, InstantProvider}
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider

trait CommonComponents
 extends WithExecutionContextComponents{

  lazy val actionRunner: DbActionRunner = wire[DbActionRunner]
  lazy val dbConfigHelp: DbConfigHelper = wire[DbConfigHelper]

  def databaseConfigProvider: DatabaseConfigProvider

  def configuration: Configuration
  implicit def materializer: Materializer
  def benji: ObjectStorage

  lazy val fileCloudUploaderService: FileCloudUploaderService = wire[FileCloudUploaderService]
  lazy val dateTimeProvider: DateTimeProvider = wire[InstantProvider]
}
