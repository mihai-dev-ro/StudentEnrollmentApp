package student.exceptions

case class FileValidationException(message: String)
  extends RuntimeException(message)
