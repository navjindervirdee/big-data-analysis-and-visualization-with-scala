package observatory

import GridUtility._
import scala.collection._

/**
  * 4th milestone: value-added information
  */
object Manipulation {
  
  /**
    * get the grid locations 
    */
  val generateGridLocations : Seq[GridLocation] = {
    for {
      y <- (90 until -90 by -1)
      x <- (-180 until 180)
    } yield GridLocation(y, x)
  }
  
  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature = {
    val locations = generateGridLocations
    getGridTemperature(temperatures, locations)
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature = {
    val gridLocations = generateGridLocations
    averageTemperatures(temperaturess, gridLocations)
  }

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)], normals: GridLocation => Temperature): GridLocation => Temperature = {
    val gridLocations = generateGridLocations
    val map = getGridTemperature(temperatures, gridLocations)
    def getDeviation(gridLocation : GridLocation) : Temperature = {
      map(gridLocation) - normals(gridLocation) 
    }
    getDeviation
  }
}