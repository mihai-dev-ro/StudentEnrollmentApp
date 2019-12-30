package authentication.exceptions

class GenericException(val exception: AuthenticationExceptionCode)
  extends RuntimeException(exception.toString)
