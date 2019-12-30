package student.controllers.authentication_middleware

import play.api.mvc.{ActionBuilder, AnyContent}

trait AuthenticatedActionBuilder extends ActionBuilder[AuthenticatedStudentRequest, AnyContent]
