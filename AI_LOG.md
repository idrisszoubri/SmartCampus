# Registro de IAG utilizada para consultar dudas.


## Consulta 1

Fecha: 21/09/2026

Herramienta: ChatGPT

Uso: Consultamos cómo organizar la estructura del proyecto de la Fase 1 y dónde debían estar `Csv.scala` y `Main.scala`.

Código afectado: Estructura del proyecto, `Csv.scala` y `Main.scala`.

Verificación: Comprobamos que la estructura coincidía con la indicada en el enunciado y que `sbt compile` terminaba correctamente.


## Consulta 2

Fecha: 21/09/2026

Herramienta: ChatGPT

Uso: Consultamos por qué era necesario convertir el `Iterator` de `getLines()` en una `List` antes de cerrar el `Source`.

Código afectado: `Csv.scala`.

Verificación: Comprobamos que el fichero se leía correctamente y que el `Source` se cerraba después de materializar las líneas.


## Consulta 3

Fecha: 23/09/2026

Herramienta: ChatGPT

Uso: Consultamos cómo tratar el BOM de los ficheros CSV y por qué utilizábamos `split(";", -1)`.

Código afectado: `Main.scala` y `Csv.scala`.

Verificación: Ejecutamos el programa y comprobamos que la cabecera se mostraba correctamente, que las filas tenían el número de campos esperado y que no había filas con estructura incorrecta.


## Consulta 4

Fecha: 06/10/2026  

Herramienta: ChatGPT  

Uso: Diferencia entre `filter` y `map` y tipos intermedios de la consulta de S02.  

Código afectado: `Main.scala`.  

Verificación: Comprobamos el resultado con `sbt compile` y `sbt run`.


## Consulta 5

Fecha: 09/10/2026  

Herramienta: ChatGPT  

Uso: Cómo crear y utilizar `stationById` mediante un `Map`.  

Código afectado: `Main.scala`.  

Verificación: Comprobamos que S01 mostraba correctamente su nombre y número de viajes.


## Consulta 6

Fecha: 24/10/2026

Herramienta: ChatGPT

Uso: Cómo utilizar `groupBy` para agrupar datos y contar los elementos de cada grupo.

Código afectado: `Queries.scala`.

Verificación: Comprobamos que los viajes se agrupaban y contaban correctamente.


## Consulta 7

Fecha: 25/10/2026

Herramienta: ChatGPT

Uso: Cómo utilizar `Option` y `flatMap` para descartar conversiones numéricas incorrectas.

Código afectado: `SafeParse.scala` y `Queries.scala`.

Verificación: Comprobamos que solo se utilizaban las duraciones convertidas correctamente.

## Consulta 8

Fecha: 8/11/2026

Herramienta: Claude

Uso: Duda sobre cómo aplicar la política de ingresos de la Fase 3 dentro del nuevo parser, asegurando que si costEur no es interpretable, se asigne 0.0 en lugar de descartar el viaje.

Código afectado: `Parsers.scala`.

Verificación: Se validó que el método getOrElse(0.0) permitía construir el Trip correctamente manteniendo el número total de registros completados.


## Consulta 9

Fecha: 6/11/2026

Herramienta: Claude

Uso: Comprensión de la signatura paramétrica [A, K] y el uso de funciones de orden superior (key: A => K) para generalizar los algoritmos de recuento y ranking.

Código afectado: `Queries.scala` y `GenericQueries.scala`.

Verificación: Comprobamos que el algoritmo de topNBy servía tanto para tipados complejos (Trip) como para colecciones simples (String), reutilizando frequencyBy sin duplicar código.