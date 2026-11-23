package smartcampus

object Main {

  def main(args: Array[String]): Unit = {

    // Creamos filas pequenas para comprobar los nuevos casos del parser.
    val rowDistance =
      List("T1", "2026-10-01T08:00:00", "S01", "S02", "10", "1.5", "student", "bike", "2.0", "completed", "ok")

    val rowWithoutDistance =
      List("T2", "2026-10-01T09:00:00", "S01", "S03", "12", "", "student", "bike", "3.0", "completed", "ok")

    val rowInvalidDistance =
      List("T3", "2026-10-01T10:00:00", "S02", "S03", "15", "abc", "student", "bike", "3.0", "completed", "ok")

    // Comprobamos los tres casos de distance_km.
    assert(Parsers.trip(rowDistance).exists(_.distanceKm.contains(1.5)))
    assert(Parsers.trip(rowWithoutDistance).exists(_.distanceKm.isEmpty))
    assert(Parsers.trip(rowInvalidDistance).isEmpty)

    // Comprobamos que el parser meteorologico tambien funciona.
    val weatherRow = List("2026-10-01", "15.0", "2.0", "10.0")
    assert(Parsers.weather(weatherRow).isDefined)

    // Creamos viajes pequenos con distancia conocida y desconocida.
    val testTrips = List(
      Trip("T1", "2026-10-01T08:00:00", "S01", "S02", 10, Some(1.0), "student", "bike", 2.0, "completed", "ok"),
      Trip("T2", "2026-10-01T14:00:00", "S01", "S03", 20, None, "student", "bike", 3.0, "completed", "ok"),
      Trip("T3", "2026-10-02T18:00:00", "S02", "S01", 30, Some(3.0), "subscriber", "ebike", 4.0, "completed", "ok"),
      Trip("T4", "2026-10-02T19:00:00", "S03", "S01", 15, Some(10.0), "subscriber", "scooter", 1.5, "cancelled", "ok")
    )

    // Comprobamos que una distancia ausente no elimina el viaje ni entra en la media.
    assert(Queries.completed(testTrips).size == 3)
    assert(Queries.averageKnownDistance(testTrips).contains(2.0))

    // Mantenemos una consulta de F4 para comprobar continuidad.
    assert(
      Queries.tripsByVehicle(testTrips) ==
        Map("bike" -> 2, "ebike" -> 1)
    )

    // Creamos datos meteorologicos pequenos para probar la relacion por fecha.
    val testWeather = List(
      Weather("2026-10-01", 15.0, 2.0, 10.0),
      Weather("2026-10-02", 16.0, 0.0, 8.0),
      Weather("2026-10-03", 14.0, 5.0, 12.0),
      Weather("2026-10-04", 13.0, 0.0, 9.0)
    )

    assert(
      Queries.averageTripsByRainClass(testTrips, testWeather) ==
        Map("rainy" -> 1.0, "dry" -> 0.5)
    )

    assert(
      Queries.vehiclesOnRainyDays(testTrips, testWeather) ==
        Map("bike" -> 2)
    )

    // Mantenemos tambien una prueba de GenericQueries de F4.
    val words = List("sol", "mar", "casa", "perro")

    assert(
      GenericQueries.frequencyBy(words)(_.length) ==
        Map(3 -> 2, 4 -> 1, 5 -> 1)
    )

    // Cargamos los datos reales.
    val trips =
      Csv.readRows("datos/trips.csv").flatMap(Parsers.trip)

    val stations =
      Csv.readRows("datos/stations.csv").flatMap(Parsers.station)

    val weather =
      Csv.readRows("datos/weather.csv").flatMap(Parsers.weather)

    // Mantenemos stationById de la fase anterior.
    val stationById =
      stations.map(s => s.id -> s).toMap

    // Mostramos una consulta heredada de F4.
    println("Numero de viajes completados: " + Queries.completed(trips).size)

    println("\nTop 3 estaciones de origen:")

// Mostramos las tres estaciones de origen con mas viajes completados.
    Queries.topOrigins(trips, 3).foreach {
      case (id, count) =>
        val name = stationById.get(id).map(_.name).getOrElse("desconocida")
        println(s"$id ($name): $count")
    }

    // Mostramos la distancia media conocida de los viajes.
    Queries.averageKnownDistance(trips) match {
      case Some(distance) =>
        println("\nDistancia media conocida: " + distance + " km")

      case None =>
        println("\nNo hay distancias conocidas.")
    }

    println("\nMedia de viajes por clase de lluvia:")

    // Mostramos la media de viajes por clase de lluvia ordenada alfabeticamente.
    Queries
      .averageTripsByRainClass(trips, weather)
      .toList
      .sortBy(_._1)
      .foreach {
        case (rainClass, average) =>
          println(rainClass + ": " + average)
      }

    println("\nVehiculos en dias lluviosos:")

    // Mostramos los vehiculos en dias lluviosos ordenados alfabeticamente.
    Queries
      .vehiclesOnRainyDays(trips, weather)
      .toList
      .sortBy(_._1)
      .foreach {
        case (vehicle, count) =>
          println(vehicle + ": " + count)
      }

    println("\nTop 3 dias con mas lluvia:")

    // Mostramos los dias con mas lluvia junto con sus viajes completados.
    Queries
      .rainiestDaysWithTrips(trips, weather, 3)
      .foreach {
        case (day, count) =>
          println(day.date + ": " + day.rainMm + " mm -> " + count + " viajes")
      }

    println("\nTodos los assert se han superado correctamente.")
  }
}