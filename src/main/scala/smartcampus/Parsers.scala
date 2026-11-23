package smartcampus

object Parsers {

  // Guardamos los indices de stations.csv.
  private val StationId = 0
  private val StationName = 1
  private val StationLat = 2
  private val StationLon = 3
  private val StationZone = 4

  // Guardamos los indices de trips.csv.
  private val TripId = 0
  private val StartTime = 1
  private val Origin = 2
  private val Destination = 3
  private val DurationMin = 4
  private val DistanceKm = 5
  private val UserType = 6
  private val VehicleType = 7
  private val CostEur = 8
  private val Status = 9
  private val DataQuality = 10

  // Guardamos los indices de weather.csv.
  private val WeatherDate = 0
  private val MeanTempC = 1
  private val RainMm = 2
  private val WindKmh = 3

  // Distinguimos una distancia vacia, una correcta y una incorrecta.
  private def optionalDistance(text: String): Option[Option[Double]] = {

    // Si esta vacio el Trip sigue siendo valido, pero no conocemos la distancia.
    if (text.isEmpty) {
      Some(None)
    } else {
      // Si hay contenido intentamos convertirlo a Double.
      SafeParse.toDoubleOption(text) match {
        case Some(distance) => Some(Some(distance))
        case None => None
      }
    }
  }

  // Convertimos una fila de estaciones en un objeto Station.
  def station(row: List[String]): Option[Station] = {

    // Comprobamos primero el numero exacto de campos.
    if (row.length != 5) None
    else {
      for {
        lat <- SafeParse.toDoubleOption(row(StationLat))
        lon <- SafeParse.toDoubleOption(row(StationLon))
      } yield Station(
        row(StationId),
        row(StationName),
        lat,
        lon,
        row(StationZone)
      )
    }
  }

  // Convertimos una fila de viajes en un objeto Trip.
  def trip(row: List[String]): Option[Trip] = {

    if (row.length != 11) None
    else {
      for {
        duration <- SafeParse.toIntOption(row(DurationMin))
        distance <- optionalDistance(row(DistanceKm))
      } yield {

        // Si el coste no se puede convertir mantenemos la politica anterior y usamos 0.0.
        val cost = SafeParse.toDoubleOption(row(CostEur)).getOrElse(0.0)

        Trip(
          row(TripId),
          row(StartTime),
          row(Origin),
          row(Destination),
          duration,
          distance,
          row(UserType),
          row(VehicleType),
          cost,
          row(Status),
          row(DataQuality)
        )
      }
    }
  }

  // Convertimos una fila meteorologica en un objeto Weather.
  def weather(row: List[String]): Option[Weather] = {

    if (row.length != 4) None
    else {
      for {
        meanTemp <- SafeParse.toDoubleOption(row(MeanTempC))
        rain <- SafeParse.toDoubleOption(row(RainMm))
        wind <- SafeParse.toDoubleOption(row(WindKmh))
      } yield Weather(
        row(WeatherDate),
        meanTemp,
        rain,
        wind
      )
    }
  }
}