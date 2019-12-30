package config

import authentication.AuthenticationComponents
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}
import student.StudentComponents

class StudentEnrollmentAppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application =
    new StudentEnrollmentAppComponents(context).application
}

class StudentEnrollmentAppComponents(context: ApplicationLoader.Context)
  extends BuiltInComponentsFromContext(context)
  with AuthenticationComponents
  with StudentComponents {

  override def router: Router = ???

  override def httpFilters: Seq[EssentialFilter] = ???

  override def databaseConfigProvider: DatabaseConfigProvider = ???
}
