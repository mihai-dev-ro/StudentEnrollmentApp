package common.validations.constraints

case class MinLengthViolation(minLength: Int) extends Violation {
  override val message: String = s"este prea scurt (minimum este $minLength litere)"
}