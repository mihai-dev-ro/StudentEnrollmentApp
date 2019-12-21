package university.repository

import university.models.University

trait UniversityRepo {
  def getAll: Seq[University]
  def getById(id: Int): University
}
