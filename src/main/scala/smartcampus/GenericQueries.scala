package smartcampus

object GenericQueries {

  // Cuenta las frecuencias de cualquier colección A agrupando por la clave K generada por la función key.
  def frequencyBy[A, K](xs: List[A])(key: A => K): Map[K, Int] = {
    xs.groupBy(key).map {
      case (k, group) => k -> group.size
    }
  }

  // Obtiene un ranking de los N elementos más frecuentes de cualquier tipo A. Reutiliza frequencyBy.
  def topNBy[A, K](xs: List[A], n: Int)(key: A => K): List[(K, Int)] = {
    val counts = frequencyBy(xs)(key)
    counts.toList.sortBy {
      case (_, count) => -count
    }.take(n)
  }
}