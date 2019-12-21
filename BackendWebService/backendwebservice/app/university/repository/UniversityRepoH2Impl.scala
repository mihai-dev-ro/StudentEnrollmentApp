package university.repository
import university.models.University

class UniversityRepoH2Impl extends UniversityRepo {
  override def getAll: Seq[University] = ???

  override def getById(id: Int): University = ???
}
