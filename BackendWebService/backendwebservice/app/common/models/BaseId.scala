package common.models

trait BaseId[U] extends Any {
  def value: U
}
