package common

import com.softwaremill.macwire.wire
import common.repositories.{DateTimeProvider, DbConfigHelper}
import common.services.{DbActionRunner, InstantProvider}
import play.api.db.slick.DatabaseConfigProvider

trait CommonComponents {

  lazy val actionRunner: DbActionRunner = wire[DbActionRunner]
  lazy val dbConfigHelp: DbConfigHelper = wire[DbConfigHelper]

  def databaseConfigProvider: DatabaseConfigProvider

  lazy val dateTimeProvider: DateTimeProvider = wire[InstantProvider]
}
