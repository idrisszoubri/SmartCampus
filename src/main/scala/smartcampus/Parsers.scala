package smartcampus

object Parsers {

  // Centralizamos los índices de stations.csv
  private val StationId = 0
  private val StationName = 1
  private val StationLat = 2
  private val StationLon = 3
  private val StationZone = 4

  // Centralizamos los índices de trips.csv
  private val TripId = 0
  private val StartTime = 1
  private val Origin = 2
  private val Destination = 3
  private val DurationMin = 4
  private val DistanceRaw = 5
  private val UserType = 6
  private val VehicleType = 7
  private val CostEur = 8
  private val Status = 9
  private val DataQuality = 10

  // Convierte una fila de stations.csv en un objeto Station
  def station(row: List[String]): Option[Station] = {
    // Comprueba primero el número de campos
    if (row.length < 5) None
    else {
      // Usamos for/yield para encadenar conversiones obligatorias
      for {
        lat <- SafeParse.toDoubleOption(row(StationLat))
        lon <- SafeParse.toDoubleOption(row(StationLon))
      } yield Station(
        id = row(StationId),
        name = row(StationName),
        lat = lat,
        lon = lon,
        zone = row(StationZone)
      )
    }
  }

  // Conoce las posiciones de trips.csv y construye un Trip
  def trip(row: List[String]): Option[Trip] = {
    // Comprueba primero el número de campos
    if (row.length < 11) None
    else {
      // durationMin es una conversión obligatoria
      for {
        duration <- SafeParse.toIntOption(row(DurationMin))
      } yield {
        // Si costEur no puede convertirse, utiliza 0.0 y no descartamos el viaje
        val cost = SafeParse.toDoubleOption(row(CostEur)).getOrElse(0.0)
        
        Trip(
          id = row(TripId),
          startTime = row(StartTime),
          origin = row(Origin),
          destination = row(Destination),
          durationMin = duration,
          distanceRaw = row(DistanceRaw),
          userType = row(UserType),
          vehicleType = row(VehicleType),
          costEur = cost,
          status = row(Status),
          dataQuality = row(DataQuality)
        )
      }
    }
  }
}