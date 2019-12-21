package common.services

import scala.concurrent.Future
import common.repositories.DbConfigHelper

class DbActionRunner(dbConfigHelper: DbConfigHelper) {
  import dbConfigHelper.driver.api._

  def run[A](action: DBIO[A]): Future[A] = dbConfigHelper.db.run(action)

  def runTransactionally[A](action: DBIO[A]): Future[A] = run(action.transactionally)
}
