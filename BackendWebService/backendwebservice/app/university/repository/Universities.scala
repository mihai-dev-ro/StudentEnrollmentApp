/* Work in progress to solve the DI by using the Reader monad */

package university.repository

import university.models.University

trait Universities {
  import scalaz.Reader

  def getAll = Reader((universityRepo: UniversityRepoForReaderDI) =>
    universityRepo.getAll)

  def getById(id: Int) = Reader((universityRepo: UniversityRepoForReaderDI) =>
    universityRepo.getById(id))
}

// repo
trait UniversityRepoForReaderDI {
  def getAll: Seq[University]
  def getById(id: Int): University
}

// implementation of the Repo by using the Slick
class UniversityRepoH2Impl extends UniversityRepoForReaderDI {
  override def getAll: Seq[University] = ???

  override def getById(id: Int): University = ???
}
