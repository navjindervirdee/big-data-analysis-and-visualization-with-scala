package observatory

import java.time.LocalDate
import scala.io.Source
import scala.collection._

/**
  * 1st milestone: data extraction
  */
object Extraction {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate,Location, Temperature)] = {
    //get files as streams
    val stationsFileStream = getClass().getResourceAsStream(stationsFile)
    val temperaturesFileStream = getClass().getResourceAsStream(temperaturesFile)
    
    //get the lines from input streams
    val stationsIter : Seq[String] = Source.fromInputStream(stationsFileStream).getLines.toSeq
    val temperaturesIter : Seq[String] = Source.fromInputStream(temperaturesFileStream).getLines.toSeq
    
    //ignore station rows with missing lat and long 
    val stationsFiltered : Seq[String] = stationsIter.filter(validStationRow(_))
    
    //convert stations to Map[Identifier, Location[Lat, Long]]
    val stationsMap : Map[(String, String), Location] = stationsFiltered.map(station2Location(_)).toMap
    
    /*
     * ignore 1. temperature rows with temp = 9999.9
     * ignore 2. station without lat and long
     */
    val temperaturesFiltered : Seq[String] = temperaturesIter.filter(validTemperatureRow(_ , stationsMap))
    
    /*
     * convert temperature rows to (LocaleDate(year,mon,day), Location, temperature) 
     */
    val records = temperaturesFiltered.map(convertTemperatureRows(_, year, stationsMap))
    //records.foreach(value => if(value._2.lat.isNaN || value._2.lon.isNaN) println(value._2 + " " + value._1))
    records
  }
  
  /**
   * check valid station row or not
   * valid row should not contain missing lat and long
   */
  def validStationRow(row : String) : Boolean = {
    val array = row.split(",")
    array.length == 4 && array(2) != "" && array(3) != ""
  }
  
  /**
   * converts station string to tuple (identifier, Location)
   * identifier = (stn, wban)
   * Location = Location(lat, long)
   */
  def station2Location(station : String) : ((String, String), Location) = {
    val values = station.split(",")
    val stn = values(0)
    val wban = values(1)
    val lat = values(2).toDouble
    val lon = values(3).toDouble
    ((stn, wban) , Location(lat, lon))
  }
  
  /**
   * checks valid temperature row or not
   * 1. valid row should not contain temp = 9999.9
   * 2. valid row should not contain station without lat and lon
   */
  def validTemperatureRow(row : String, validStations : GenMap[(String, String), Location]) : Boolean = {
    val values = row.split(",")
    val stn = values(0)
    val wban = values(1)
    val temp = values(4).toDouble
    
    temp != 9999.9 && validStations.contains((stn,wban))
  }
  
  /**
   * converts temperature rows to (LocalDate(year, mon, day), Location(lat, lon), Temperature) 
   */
  def convertTemperatureRows(row : String, year : Year, stationsMap : GenMap[(String,String), Location]) = {
    val values = row.split(",")
    val stn = values(0)
    val wban = values(1)
    val month = values(2).toInt
    val day = values(3).toInt
    val temp = celcius(values(4).toDouble)
    val location = stationsMap((stn,wban)) 
    val date =  LocalDate.of(year, month, day)
    
    (date, location, temp)
  }
  
  /**
    * converts farenheit to celcius
    */
  def celcius(temp : Double) : Double = ((temp - 32.0)*5.0)/9.0
  
  
  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    
    var avgTemperatureMap = mutable.Map[Location, (Temperature, Int)]()
    
    val recordsIter = records.iterator
    
    while(recordsIter.hasNext){
      val (date, location, temperature) = recordsIter.next() 
      if (avgTemperatureMap.contains(location)) {
        val totalTemp  = avgTemperatureMap(location)._1 + temperature
        val totalCount = avgTemperatureMap(location)._2 + 1
        avgTemperatureMap(location) = (totalTemp, totalCount)
      }
      else {
        val value = (temperature, 1)
        avgTemperatureMap += (location -> value)
      }
    }
    
    avgTemperatureMap.map{
      case (k,v) => (k, v._1/v._2)
    }
  }
  
  def extractYearlyData(from : Year , until : Year) : Iterable[Iterable[(Location, Temperature)]] = {
    val stationsFile = "/stations.csv"
    for (year <- (from until until)) yield {
      println("Extraction of year " + year +  " started ....")
      locationYearlyAverageRecords(locateTemperatures(year, stationsFile, "/" + year + ".csv"))
    }
  }
}
