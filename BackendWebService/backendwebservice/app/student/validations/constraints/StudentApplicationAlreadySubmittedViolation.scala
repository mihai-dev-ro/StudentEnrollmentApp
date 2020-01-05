package student.validations.constraints

import common.validations.constraints.Violation

object StudentApplicationAlreadySubmittedViolation extends Violation {
  override def message: String = "Aplicatia a fost deja inregsitrata. Nu mai poate fi actualizata."
}
