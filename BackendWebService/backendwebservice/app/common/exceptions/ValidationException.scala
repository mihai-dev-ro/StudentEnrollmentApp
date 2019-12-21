package common.exceptions

import common.validations._

class ValidationException(val violations: Seq[PropertyViolation])
  extends RuntimeException(violations.toString())

