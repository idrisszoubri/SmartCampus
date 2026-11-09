package smartcampus

object Queries {

  // Nos quedamos solamente con los viajes que tienen estado completed.
  def completed(trips: List[Trip]): List[Trip] = {
    trips.filter(t => t.status == "completed")
  }

  // Agrupamos los viajes completados por tipo de vehiculo reutilizando GenericQueries.
  def tripsByVehicle(trips: List[Trip]): Map[String, Int] = {
    GenericQueries.frequencyBy(completed(trips))(_.vehicleType)
  }

  // Agrupamos los viajes completados por tipo de usuario y calculamos su duracion media.
  def averageDurationByUser(trips: List[Trip]): Map[String, Double] = {
    val completedTrips = completed(trips)

    val grouped: Map[String, List[Trip]] = completedTrips.groupBy(_.userType)

    grouped.map {
      case (userType, userTrips) =>
        // durationMin ya es Int en Trip, no necesitamos SafeParse
        val durations: List[Int] = userTrips.map(_.durationMin)

        val average =
          if (durations.nonEmpty) durations.sum.toDouble / durations.size
          else 0.0

        userType -> average
    }
  }

  // Reutiliza el algoritmo genérico de topNBy pasándole la función selectora key: Trip => String.
  private def topBy(trips: List[Trip], n: Int)(key: Trip => String): List[(String, Int)] = {
    GenericQueries.topNBy(trips, n)(key)
  }

  // Obtenemos las estaciones de origen mas frecuentes usando la función selectora _.origin.
  def topOrigins(trips: List[Trip], n: Int): List[(String, Int)] = {
    topBy(trips, n)(_.origin)
  }

  // Obtenemos las estaciones de destino mas frecuentes usando la función selectora _.destination.
  def topDestinations(trips: List[Trip], n: Int): List[(String, Int)] = {
    topBy(trips, n)(_.destination)
  }

  // Agrupamos los viajes completados por dia y sumamos sus ingresos.
  def revenueByDay(trips: List[Trip]): Map[String, Double] = {
    val completedTrips = completed(trips)

    val grouped: Map[String, List[Trip]] = completedTrips.groupBy { t =>
      t.startTime.take(10)
    }

    grouped.map {
      case (date, dayTrips) =>
        // costEur ya es Double en Trip
        val revenue = dayTrips.map(_.costEur).sum
        date -> revenue
    }
  }

  // Buscamos la ruta origen-destino que mas veces aparece entre los viajes completados.
  def mostFrequentRoute(trips: List[Trip]): Option[((String, String), Int)] = {
    GenericQueries.topNBy(completed(trips), 1)(t => (t.origin, t.destination)).headOption
  }

  // Clasificamos la hora de inicio de un viaje en una franja horaria.
  def timeBand(startTime: String): String = {
    val hour = startTime.slice(11, 13).toInt

    if (hour >= 6 && hour < 12) "morning"
    else if (hour >= 12 && hour < 18) "afternoon"
    else if (hour >= 18 && hour < 22) "evening"
    else "night"
  }

  // Buscamos la franja horaria con mas viajes completados.
  def busiestTimeBand(trips: List[Trip]): Option[(String, Int)] = {
    GenericQueries.topNBy(completed(trips), 1)(t => timeBand(t.startTime)).headOption
  }

  // Recorremos los viajes completados acumulando a la vez los ingresos y el numero de viajes.
  def revenueAndCount(trips: List[Trip]): (Double, Int) = {
    completed(trips).foldLeft((0.0, 0)) {
      case ((revenue, count), trip) =>
        (revenue + trip.costEur, count + 1)
    }
  }
}