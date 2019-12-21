package university.repository

trait Universities {
  import scalaz.Reader

  def getAll = Reader((universityRepo: UniversityRepo) =>
    universityRepo.getAll

  def getById(id: Int) = Reader((universityRepo: UniversityRepo) =>
    universityRepo.getById(id))
}
