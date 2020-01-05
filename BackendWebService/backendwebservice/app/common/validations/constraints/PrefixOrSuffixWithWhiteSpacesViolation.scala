package common.validations.constraints

case object PrefixOrSuffixWithWhiteSpacesViolation extends Violation {
  override def message: String = "sterge spatiile libere repetitive"
}
