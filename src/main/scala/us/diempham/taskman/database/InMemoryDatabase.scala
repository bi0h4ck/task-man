package us.diempham.taskman.database

import scala.collection.mutable;

class InMemoryDatabase[K, V] extends DatabaseInterface[K, V] {
  protected val database: mutable.Map[K, V] = mutable.Map.empty[K, V]

  override def get(k: K): Option[V] = database.get(k)

  override def create(k: K, v: V): V = {
    database += (k -> v)
    v
  }

  override def delete(k: K): Option[V] = {
    val result = database.get(k)
    database -= k
    result
  }

  override def update(k: K)(f: V => V): Option[V] = {
    val result = database.get(k).map(f)
    result.map(create(k, _))
    result
    }



}
