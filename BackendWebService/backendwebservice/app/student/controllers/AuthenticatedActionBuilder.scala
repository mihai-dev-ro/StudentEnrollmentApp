package student.controllers

import play.api.mvc.{ActionBuilder, AnyContent}

trait AuthenticatedActionBuilder extends ActionBuilder[AuthenticatedStudentRequest, AnyContent]
