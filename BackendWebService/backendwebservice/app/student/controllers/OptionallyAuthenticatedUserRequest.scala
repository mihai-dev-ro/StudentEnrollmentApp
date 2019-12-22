package student.controllers

import play.api.mvc.Request
import student.authentication.AuthenticatedStudent

trait OptionallyAuthenticatedUserRequest[+BodyContentType] extends Request[BodyContentType] {
  def authenticatedUserOption: Option[AuthenticatedStudent]
}
