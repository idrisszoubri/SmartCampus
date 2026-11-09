# Decisiones Multiparadigma - SmartCampus

## 1. Uso de Case Class y Parsers frente a colecciones de Strings posicionales
* **Problema que aparece:** En las primeras fases, los datos se representaban como `List[String]`. Las consultas dependían de índices numéricos (ej. `row(7)`) para acceder a las columnas. Esto acoplaba toda la lógica del programa al formato exacto del CSV, haciendo el código frágil ante cambios y difícil de leer.
* **Alternativas consideradas:** Mantener las listas de texto y crear un diccionario global de índices o usar constantes en cada consulta para darles un nombre semántico (como en la Fase 3), pero los datos seguirían sin estar tipados (ej. tratar las duraciones como texto hasta el último momento).
* **Decisión adoptada:** Implementar un modelo de dominio usando `case class` (`Trip`, `Station`) y establecer una frontera de seguridad mediante funciones de parsing (`Parsers.trip`, `Parsers.station`) que devuelven `Option[T]`.
* **Por qué encaja con el paradigma:** Aprovecha la Programación Orientada a Objetos para agrupar datos relacionados bajo entidades con significado (dominio) y la Programación Funcional (tipos `Option` y bloques `for/yield`) para manejar la ausencia de datos o fallos de conversión de forma inmutable y segura.
* **Consecuencias:** 
  * *Facilita:* El código de las consultas es mucho más legible (`trip.vehicleType` en lugar de `row(7)`) y fuertemente tipado. El conocimiento del esquema del CSV queda aislado en una única capa (el parser).
  * *Coste:* Introduce la necesidad de procesar y transformar la colección inicial antes de operar con ella, obligando a gestionar los casos `None` mediante operaciones funcionales como `flatMap`.

## 2. Reutilización mediante algoritmos genéricos y funciones selectoras
* **Problema que aparece:** Las consultas de la Fase 3 que calculaban rankings (orígenes, destinos, vehículos) duplicaban el mismo algoritmo de agrupación, conteo y ordenación, cambiando únicamente el campo utilizado como clave.
* **Alternativas consideradas:** Mantener funciones separadas para cada caso, o bien pasar un índice numérico por parámetro a una función privada (como `topBy` en F3), lo cual mantenía la dependencia estructural de los datos.
* **Decisión adoptada:** Extraer la lógica a un módulo `GenericQueries` implementando funciones paramétricas (`frequencyBy[A, K]` y `topNBy[A, K]`). Estas funciones reciben la colección y una función de orden superior (`key: A => K`) que actúa como selectora de la clave.
* **Por qué encaja con el paradigma:** Es una aplicación directa de la Programación Funcional Genérica. Trata a las funciones como ciudadanos de primera clase (pasando la función selectora como argumento) y abstrae el tipo de dato, separando el "qué hacer" (algoritmo genérico) del "con qué hacerlo" (función selectora específica del dominio).
* **Consecuencias:** 
  * *Facilita:* Reutilización masiva del código. El algoritmo genérico se puede testear independientemente del dominio (ej. usando `String`) y el código de negocio queda muy declarativo.
  * *Coste:* Requiere un mayor nivel de abstracción mental por parte del desarrollador para comprender la firma paramétrica `[A, K]` y el uso de múltiples listas de argumentos para aplicar currificación parcial.
