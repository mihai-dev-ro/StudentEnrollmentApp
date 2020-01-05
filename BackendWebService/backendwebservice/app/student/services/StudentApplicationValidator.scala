package student.services

import common.utils.AppStringUtils
import common.validations.PropertyViolation
import common.validations.constraints.{MaxLengthViolation, MaxWordsCountViolation, MinLengthViolation, NotNullViolation, PrefixOrSuffixWithWhiteSpacesViolation, Violation}
import org.apache.commons.lang3.StringUtils
import student.models.StudentApplicationForUpdating

import scala.concurrent.ExecutionContext

class StudentApplicationValidator(implicit private val ec: ExecutionContext) {

  def validateStudentApplicationForUpdating(
    studentApplicationForUpdating: StudentApplicationForUpdating): Seq[PropertyViolation] = {
    val academicRecordsViolations = studentApplicationForUpdating.academicRecords
      .map(validateShortText("Informatii matricole", _)).getOrElse(Seq.empty)

    val honorsAndDistinctionsViolations = studentApplicationForUpdating.honorsAndDistinctions
      .map(validateShortText("Premii si distinctii", _)).getOrElse(Seq.empty)

    val volunteerActivitiesViolations = studentApplicationForUpdating.volunteerActivities
      .map(validateShortText("Activitati de viluntariat", _)).getOrElse(Seq.empty)

    val otherInterestsViolations = studentApplicationForUpdating.otherInterests
      .map(validateShortText("Alte activitati", _)).getOrElse(Seq.empty)

    val coverLetterViolations = studentApplicationForUpdating.coverLetter
      .map(validateEssay("Scrisoare de intentie", _)).getOrElse(Seq.empty)


    academicRecordsViolations ++
      honorsAndDistinctionsViolations ++
      volunteerActivitiesViolations ++
      otherInterestsViolations ++
      coverLetterViolations
  }

  private def validateShortText(fieldName: String, field: String) =
    validateText(fieldName, field, 200)

  private def validateEssay(fieldName: String, field: String) =
    validateText(fieldName, field, 1000)

  private def validateText(fieldName: String, fieldText: String, fieldMaxWordsCount: Int) = {
    val stringValidator = new TextValidator(maxWordsCount = fieldMaxWordsCount)

    stringValidator.validate(fieldText)
      .map(PropertyViolation(fieldName, _))
  }

  private class TextValidator(minLength: Int = 0, maxWordsCount: Int = Int.MaxValue) {

    def validate(str: String): Seq[Violation] = {
      if (StringUtils.isBlank(str)) Seq(NotNullViolation)
      else if (str.length < minLength) Seq(MinLengthViolation(minLength))
      else if (AppStringUtils.countWords(str) > maxWordsCount) Seq(MaxWordsCountViolation(maxWordsCount))
      else if (AppStringUtils.startsWithWhiteSpace(str)
        || AppStringUtils.endsWithWhiteSpace(str)) Seq(PrefixOrSuffixWithWhiteSpacesViolation)
      else Nil
    }
  }
}