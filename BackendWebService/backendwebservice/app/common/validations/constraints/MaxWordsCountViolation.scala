package common.validations.constraints

case class MaxWordsCountViolation(maxWordsCount: Int) extends Violation {
  override val message: String = s"este prea lung (maximum est $maxWordsCount cuvinte)"
}
