package smartcampus

// Representa una estación con sus propiedades explícitas.
case class Station(
  id: String,
  name: String,
  lat: Double,
  lon: Double,
  zone: String
)

// Representa un viaje. Sustituye a la lista de Strings (List[String]).
case class Trip(
  id: String,
  startTime: String,
  origin: String,
  destination: String,
  durationMin: Int,
  distanceRaw: String, 
  userType: String,
  vehicleType: String,
  costEur: Double,
  status: String,
  dataQuality: String
)
