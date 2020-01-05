package config

import authentication.AuthenticationComponents
import controllers.AssetsComponents
import play.api.cache.ehcache.EhCacheComponents
import play.api.db.DBApi
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.db.slick.{DatabaseConfigProvider, DbName, DefaultSlickApi, SlickApi, SlickComponents}
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.{AhcWSComponents, StandaloneAhcWSClient}
import play.api.mvc.{EssentialFilter, Handler, RequestHeader}
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import play.filters.cors.{CORSConfig, CORSFilter}
import play.modules.benji.{BenjiComponents, BenjiFromContext}
import slick.basic.{BasicProfile, DatabaseConfig}
import student.StudentComponents
import student.services.EmailValidator
import university.UniversityComponents

class StudentEnrollmentAppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application =
    new StudentEnrollmentAppComponents(context).application
}

class StudentEnrollmentAppComponents(context: ApplicationLoader.Context)
  extends BenjiFromContext(context, "default")
  with SlickComponents
  with SlickEvolutionsComponents
  with EvolutionsComponents
  with AuthenticationComponents
  with StudentComponents
  with UniversityComponents
  with AhcWSComponents
  with EhCacheComponents
  with HttpFiltersComponents
  with AssetsComponents
{

  // override and associate instances to definitions defined in components registry traits
  // 1. CommonComponents via Authentication or any other
  override def databaseConfigProvider: DatabaseConfigProvider = new DatabaseConfigProvider {
    def get[P <: BasicProfile]: DatabaseConfig[P] = slickApi.dbConfig[P](DbName("default"))
  }

  // 2. SlickComponents
  override lazy val slickApi: SlickApi =
    new DefaultSlickApi(environment, configuration, applicationLifecycle)(executionContext)

  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  // apply evolutions
  def onStart(): Unit = {
    // applicationEvolutions is a val and requires evaluation
    applicationEvolutions
  }
  onStart()

  // http filters
  private lazy val corsFilter: CORSFilter = {
    val corsConfig = CORSConfig.fromConfiguration(configuration)

    CORSFilter(corsConfig)
  }
  override def httpFilters: Seq[EssentialFilter] = List(corsFilter)

  // router
  lazy val routes: PartialFunction[RequestHeader, Handler] =
    studentRoutes
      .orElse(universityRoutes)
  override def router: Router = Router.from(routes)
}
