package student.controllers

import play.api.mvc.Request
import play.api.mvc.Security.AuthenticatedRequest
import student.authentication.AuthenticatedStudent

class AuthenticatedStudentRequest[+A](authenticatedStudent: AuthenticatedStudent,
                                      request: Request[A])
  extends AuthenticatedRequest[A, AuthenticatedStudent](authenticatedStudent, request)
  with OptionallyAuthenticatedUserRequest[A] {

  override val authenticatedUserOption: Option[AuthenticatedStudent] = Some(authenticatedStudent)
}
