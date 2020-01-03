package common.models

trait IdMetaModel {
  type ModelId <: BaseId[_]

  val id: Property[Option[ModelId]] = Property("Id")
}
