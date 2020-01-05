package common.validations.constraints

case class MaxLengthViolation(maxLength: Int) extends Violation {
  override val message: String = s"este prea mare (maximum este $maxLength literes)"
}
