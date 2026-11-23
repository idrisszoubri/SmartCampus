package smartcampus

object Queries {

  // Nos quedamos con los viajes completados.
  def completed(trips: List[Trip]): List[Trip] = {
    trips.filter(_.status == "completed")
  }

  // Contamos los viajes completados por tipo de vehiculo.
  def tripsByVehicle(trips: List[Trip]): Map[String, Int] = {
    GenericQueries.frequencyBy(completed(trips))(_.vehicleType)
  }

  // Calculamos la duracion media por tipo de usuario.
  def averageDurationByUser(trips: List[Trip]): Map[String, Double] = {

    completed(trips).groupBy(_.userType).map {
      case (userType, userTrips) =>
        val durations = userTrips.map(_.durationMin)
        userType -> (durations.sum.toDouble / durations.size)
    }
  }

  // Reutilizamos el ranking generico indicando la propiedad que queremos usar.
  private def topBy(
      trips: List[Trip],
      n: Int
  )(key: Trip => String): List[(String, Int)] = {

    GenericQueries.topNBy(trips, n)(key)
  }

  // Obtenemos los origenes mas frecuentes.
  def topOrigins(trips: List[Trip], n: Int): List[(String, Int)] = {
    topBy(trips, n)(_.origin)
  }

  // Obtenemos los destinos mas frecuentes.
  def topDestinations(trips: List[Trip], n: Int): List[(String, Int)] = {
    topBy(trips, n)(_.destination)
  }

  // Calculamos los ingresos de los viajes completados por dia.
  def revenueByDay(trips: List[Trip]): Map[String, Double] = {

    completed(trips).groupBy(_.date).map {
      case (date, dayTrips) =>
        date -> dayTrips.map(_.costEur).sum
    }
  }

  // Buscamos la ruta mas frecuente.
  def mostFrequentRoute(
      trips: List[Trip]
  ): Option[((String, String), Int)] = {

    GenericQueries
      .topNBy(completed(trips), 1)(t => (t.origin, t.destination))
      .headOption
  }

  // Clasificamos una hora en su franja horaria.
  def timeBand(startTime: String): String = {

    val hour = startTime.slice(11, 13).toInt

    if (hour >= 6 && hour < 12) "morning"
    else if (hour >= 12 && hour < 18) "afternoon"
    else if (hour >= 18 && hour < 22) "evening"
    else "night"
  }

  // Buscamos la franja horaria con mas viajes.
  def busiestTimeBand(trips: List[Trip]): Option[(String, Int)] = {

    GenericQueries
      .topNBy(completed(trips), 1)(t => timeBand(t.startTime))
      .headOption
  }

  // Calculamos ingresos y numero de viajes completados con foldLeft.
  def revenueAndCount(trips: List[Trip]): (Double, Int) = {

    completed(trips).foldLeft((0.0, 0)) {
      case ((revenue, count), trip) =>
        (revenue + trip.costEur, count + 1)
    }
  }

  // Calculamos la media usando solamente las distancias conocidas.
  def averageKnownDistance(trips: List[Trip]): Option[Double] = {

    val distances = completed(trips).flatMap(_.distanceKm)

    if (distances.isEmpty) None
    else Some(distances.sum / distances.size)
  }

  // Contamos los viajes completados de cada fecha.
  def completedTripsByDay(trips: List[Trip]): Map[String, Int] = {
    GenericQueries.frequencyBy(completed(trips))(_.date)
  }

  // Calculamos la media de viajes de los dias lluviosos y secos.
  def averageTripsByRainClass(
      trips: List[Trip],
      weather: List[Weather]
  ): Map[String, Double] = {

    val tripsByDay = completedTripsByDay(trips)

    weather
      .map { day =>
        val rainClass = if (day.rainMm >= 1.0) "rainy" else "dry"
        val count = tripsByDay.getOrElse(day.date, 0)
        (rainClass, count)
      }
      .groupBy(_._1)
      .map {
        case (rainClass, days) =>
          rainClass -> (days.map(_._2).sum.toDouble / days.size)
      }
  }

  // Contamos los vehiculos utilizados en dias lluviosos.
  def vehiclesOnRainyDays(
      trips: List[Trip],
      weather: List[Weather]
  ): Map[String, Int] = {

    val rainyDates =
      weather.filter(_.rainMm >= 1.0).map(_.date).toSet

    val rainyTrips =
      completed(trips).filter(t => rainyDates.contains(t.date))

    GenericQueries.frequencyBy(rainyTrips)(_.vehicleType)
  }

  // Obtenemos los dias con mas lluvia junto con sus viajes completados.
  def rainiestDaysWithTrips(
      trips: List[Trip],
      weather: List[Weather],
      n: Int
  ): List[(Weather, Int)] = {

    val tripsByDay = completedTripsByDay(trips)

    weather
      .sortBy(day => -day.rainMm)
      .take(n)
      .map(day => day -> tripsByDay.getOrElse(day.date, 0))
  }
}