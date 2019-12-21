package common.validations.constraints

object NotNullViolation extends Violation {
  override def message: String = "nu poate sa fie lipsa"
}
