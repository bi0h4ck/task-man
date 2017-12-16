package us.diempham.taskman.database

trait DatabaseInterface[K, V] {
  def delete(k: K): Option[V]
  def update(k: K)(f: V => V): Option[V]
  def create(k: K, v: V): V
}
