package student.controllers.authentication_middleware

import play.api.mvc.Request
import play.api.mvc.Security.AuthenticatedRequest
import student.models.AuthenticatedStudent

class AuthenticatedStudentRequest[+A](authenticatedStudent: AuthenticatedStudent,
                                      request: Request[A])
  extends AuthenticatedRequest[A, AuthenticatedStudent](authenticatedStudent, request)
