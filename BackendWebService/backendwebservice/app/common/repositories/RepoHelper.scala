package common.repositories

import slick.lifted._
import slick.ast.{Ordering => SlickOrdering}
import common.models._

object RepoHelper {
  def createSlickColumnOrdered(column: Rep[_])(
    implicit direction: Direction): ColumnOrdered[_] = ColumnOrdered(column, direction)

  // use the implicit conversion mechanism
  // the compiler will discover this conversion method when trying to resolve the
  // ColumnOrdered constructor
  implicit def directionToSlickOrdering(direction: Direction): SlickOrdering = {
    direction match {
      case Ascending => SlickOrdering(SlickOrdering.Asc)
      case Descending => SlickOrdering(SlickOrdering.Desc)
    }
  }
}
