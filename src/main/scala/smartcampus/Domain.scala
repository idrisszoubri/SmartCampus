package smartcampus

// Representamos una estacion con sus datos.
case class Station(
  id: String,
  name: String,
  lat: Double,
  lon: Double,
  zone: String
)

// Representamos un viaje con sus datos ya interpretados.
case class Trip(
  id: String,
  startTime: String,
  origin: String,
  destination: String,
  durationMin: Int,
  distanceKm: Option[Double],
  userType: String,
  vehicleType: String,
  costEur: Double,
  status: String,
  dataQuality: String
) {

  // Obtenemos la fecha del viaje para poder relacionarla con la meteorologia.
  def date: String = startTime.take(10)
}

// Representamos los datos meteorologicos de un dia.
case class Weather(
  date: String,
  meanTempC: Double,
  rainMm: Double,
  windKmh: Double
)