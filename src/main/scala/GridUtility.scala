package observatory

import scala.collection._
import Visualization._

object GridUtility {
  var map = mutable.Map[GridLocation, (Temperature, Int)]()
  
  val iter = (1975 until 2015).toSeq.iterator
  
  /**
    *  add temperatures of single year location data
    */
  def getGridTemperature(
    temperatures:  Iterable[(Location, Temperature)],
    gridLocations: Seq[GridLocation]): GridLocation => Temperature = {
    val map = predictTemperatures(temperatures, gridLocations)
    def getTemperature(gridLocation: GridLocation) = {
      map(gridLocation)
    }
    println(iter.next() + " done grid prediction ....")
    getTemperature
  }
  
  /**
    * average temperatures of given number of years data
    * at the grid points 
    */
  def averageTemperatures(
      temperaturess : Iterable[Iterable[(Location, Temperature)]],
      gridLocations : Seq[GridLocation]): GridLocation => Temperature = {
        
    println("num of years = " + temperaturess.size)    
    val gridTemperaturess = for (temperatures <- temperaturess) yield {
      getGridTemperature(temperatures, gridLocations)
    }
    
    println("all grid prediction done ...")
    val avgGridTemperatures = computeAvgTemperatures(gridTemperaturess, gridLocations)
    
    println("average done ....")
    def getAvgTemperature(gridLocation : GridLocation) = {
      avgGridTemperatures(gridLocation)
    }
    
    getAvgTemperature
  }
  
  /**
    * compute the average temperatures of the grid points      
    */
  def computeAvgTemperatures(
      gridTemperaturess : Iterable[GridLocation => Temperature],
      gridLocations : Seq[GridLocation]) : Map[GridLocation, Temperature] = {
    
    val avgTemperatures = for (gloc <- gridLocations.par) yield (gloc, average(gridTemperaturess, gloc))
    avgTemperatures.seq.toMap
  }
  
  /**
    * average the temperature at a given grid location
    */
  def average(gridTemperaturess : Iterable[GridLocation => Temperature], gridLocation : GridLocation) : Double = {
    val temperatures = gridTemperaturess.map(grid => grid(gridLocation))
    val len = temperatures.size
    val temp = temperatures.reduce(_+_)/len
    temp
  }

  /**
    * predict the grid points temperatures
    * of a particular year
    */
  def predictTemperatures(
    temperatures:  Iterable[(Location, Temperature)],
    gridLocations: Seq[GridLocation]): Map[GridLocation, Temperature] = {
    val gridTemperatures = gridLocations.par.map(loc => (loc, predictTemperature(temperatures, Location(loc.lat, loc.lon)))).seq.toMap
    gridTemperatures
  }
}
