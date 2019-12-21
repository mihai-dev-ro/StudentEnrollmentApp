package common.validations

import common.validations.constraints._

case class PropertyViolation(property: String, violation: Violation)

