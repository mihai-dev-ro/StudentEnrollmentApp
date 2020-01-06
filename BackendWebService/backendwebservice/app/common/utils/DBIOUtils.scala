package common.utils

import slick.dbio.DBIO

object DBIOUtils {

  def optionToDBIO[A](someVal: Option[A],
                      failureReason: Throwable = new NoSuchElementException): DBIO[A] = {
    someVal match {
      case Some(v) => DBIO.successful(v)
      case None => DBIO.failed(failureReason)
    }
  }

  def eitherToDBIO[A](someVal: Either[Throwable, A]): DBIO[A] = {
    someVal match {
      case Right(v) => DBIO.successful(v)
      case Left(e) => DBIO.failed(e)
    }
  }

  def fail(predicate: => Boolean, e: Exception): DBIO[Unit] = {
    if (predicate) DBIO.successful(())
    else DBIO.failed(e)
  }

}
