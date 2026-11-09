package smartcampus

object Main {

  def main(args: Array[String]): Unit = {

    // --- PRUEBAS CON ASSERT (Paso 9) ---

    // 1. Asserts de Parsers
    val validRow = List("T1", "2026-10-01T08:00:00", "S01", "S02", "10", "1.0", "student", "bike", "2.0", "completed", "ok")
    val invalidLengthRow = List("T1", "2026-10-01T08:00:00")
    val invalidDurationRow = List("T1", "2026-10-01T08:00:00", "S01", "S02", "invalido", "1.0", "student", "bike", "2.0", "completed", "ok")

    assert(Parsers.trip(validRow).isDefined)
    assert(Parsers.trip(invalidLengthRow).isEmpty)
    assert(Parsers.trip(invalidDurationRow).isEmpty)

    // 2. Asserts de GenericQueries sobre colecciones genéricas (ejemplo con Strings)
    val testWords = List("sol", "luna", "casa", "mar")
    assert(GenericQueries.frequencyBy(testWords)(_.length) == Map(3 -> 2, 4 -> 2))
    assert(GenericQueries.topNBy(testWords, 1)(_.length) == List((3, 2)))

    // 3. Colección pequeña de objetos Trip de prueba (2 completados, 1 cancelado)
    val testTrips: List[Trip] = List(
      Trip("T1", "2026-10-01T08:00:00", "S01", "S02", 10, "1.0", "student", "bike", 2.0, "completed", "ok"),
      Trip("T2", "2026-10-01T14:00:00", "S01", "S03", 20, "2.0", "student", "bike", 3.0, "completed", "ok"),
      Trip("T3", "2026-10-01T19:00:00", "S03", "S01", 15, "1.5", "subscriber", "scooter", 1.5, "cancelled", "ok")
    )

    assert(Queries.completed(testTrips).size == 2)
    assert(Queries.tripsByVehicle(testTrips) == Map("bike" -> 2))
    assert(Queries.topOrigins(testTrips, 1) == List(("S01", 2)))
    assert(Queries.revenueAndCount(testTrips) == (5.0, 2))

    // --- CARGA DE DATOS REALES (Pasos 4 y 8) ---

    val trips: List[Trip] = Csv.readRows("datos/trips.csv").flatMap(Parsers.trip)
    val stations: List[Station] = Csv.readRows("datos/stations.csv").flatMap(Parsers.station)
    val stationById: Map[String, Station] = stations.map(s => s.id -> s).toMap

    // Llamamos a las consultas con la lista de objetos Trip
    val completedTrips = Queries.completed(trips)
    val tripsByVehicle = Queries.tripsByVehicle(trips)
    val averageDurationByUser = Queries.averageDurationByUser(trips)
    val topOrigins = Queries.topOrigins(trips, 3)
    val topDestinations = Queries.topDestinations(trips, 3)
    val revenueByDay = Queries.revenueByDay(trips)
    val mostFrequentRoute = Queries.mostFrequentRoute(trips)
    val busiestTimeBand = Queries.busiestTimeBand(trips)
    val revenueAndCount = Queries.revenueAndCount(trips)

    // --- PRESENTACIÓN DE RESULTADOS ---

    println("Numero de viajes completados: " + completedTrips.size)

    println("\nViajes completados por tipo de vehiculo:")
    tripsByVehicle.toList.sortBy(_._1).foreach {
      case (vehicleType, count) =>
        println(vehicleType + ": " + count)
    }

    println("\nDuracion media por tipo de usuario:")
    averageDurationByUser.toList.sortBy(_._1).foreach {
      case (userType, average) =>
        println(userType + ": " + average)
    }

    // Mostramos ranking de origen indicando el nombre traducido mediante stationById (Paso 8)
    println("\nTop 3 estaciones de origen:")
    topOrigins.foreach {
      case (stationId, count) =>
        val name = stationById.get(stationId).map(_.name).getOrElse("desconocida")
        println(s"$stationId ($name): $count")
    }

    // Mostramos ranking de destino indicando el nombre traducido mediante stationById (Paso 8)
    println("\nTop 3 estaciones de destino:")
    topDestinations.foreach {
      case (stationId, count) =>
        val name = stationById.get(stationId).map(_.name).getOrElse("desconocida")
        println(s"$stationId ($name): $count")
    }

    println("\nIngresos por dia:")
    revenueByDay.toList.sortBy(_._1).foreach {
      case (date, revenue) =>
        println(date + ": " + revenue)
    }

    mostFrequentRoute match {
      case Some(((origin, destination), count)) =>
        val originName = stationById.get(origin).map(_.name).getOrElse(origin)
        val destName = stationById.get(destination).map(_.name).getOrElse(destination)
        println(s"\nRuta mas frecuente: $origin ($originName) -> $destination ($destName) ($count viajes)")
      case None =>
        println("\nNo hay una ruta mas frecuente.")
    }

    busiestTimeBand match {
      case Some((band, count)) =>
        println(s"Franja horaria con mas viajes: $band ($count viajes)")
      case None =>
        println("No hay una franja horaria con viajes.")
    }

    println("Ingresos totales de viajes completados: " + revenueAndCount._1)
    println("Numero de viajes completados contado con foldLeft: " + revenueAndCount._2)

    println("\nTodos los assert se han superado correctamente.")
  }
}