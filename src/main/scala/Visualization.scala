package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    inverseDistanceWeighting(temperatures, location)
  }
  
  /**
   * degree to radian conversion
   */
  def degree2Radian(value : Double) : Double = {
    val pi : Double = math.Pi
    val degree : Double = 180.0
    val rad = (pi*value)/degree
    rad
  }
  
  /**
   * great circle distance formula
   * lat1, long1, lat2, long2 in radians
   * deltaLat = abs(lat1-lat2), deltaLong = abs(long1-long2)
   * case 1. deltaSigma = 0  if lat1==lat2 and long1==long2
   * case 2. deltaSigma = pi if antipodes
   * case 3. deltaSigma = arccos(sin(lat1)*sin(lat2) + cos(lat1)*cos(lat2)*cos(deltaLong))
   * dist = r * deltaSigma, where r is radius of earth
   */
  def greatCircleDistance(location1 : Location, location2 : Location) : Double = {
    val radius : Double = 6371 //radius of earth in km
    val pi = math.Pi
    val lat1 = degree2Radian(location1.lat)
    val lon1 = degree2Radian(location1.lon)
    val lat2 = degree2Radian(location2.lat)
    val lon2 = degree2Radian(location2.lon)
    
    val dist = {
      if (lat1 == lat2 && lon1 == lon2) 0.0
      else if (antipodes(location1, location2)) pi * radius
      else math.acos(math.sin(lat1)*math.sin(lat2) + math.cos(lat1)*math.cos(lat2)*math.cos(math.abs(lon1-lon2))) * radius
    }
    dist
  }
  
  def antipodes(location1 : Location, location2 : Location) : Boolean = { 
    val lat1 = location1.lat
    val lon1 = location1.lon
    val lat2 = location2.lat
    val lon2 = location2.lon
    
    lat1 == -lat2 && (lon1 == lon2 + 180.0 || lon1 == lon2 - 180.0)
  }
  
  /**
   * utility function in inverse distance weighting
   * w(x) = 1/d(x,xi)^p
   */
  def w(distance : Double) : Double = 1.0/math.pow(distance, 6)
  
  /**
   * inverse distance weighting spatial interpolation
   */
  def inverseDistanceWeighting(temperatures : Iterable[(Location, Temperature)], location : Location) : Temperature = {
    val temperaturesPar = temperatures.par
    val distances = temperaturesPar.map(temp => (temp._2, greatCircleDistance(temp._1, location)))
    val minDistance = distances.minBy(_._2)
    
    if (minDistance._2 == 0) minDistance._1
    else {
      val interpolate = distances.aggregate((0.0, 0.0))(
        { (acc, distance) => (acc._1 + distance._1 * w(distance._2), acc._2 + w(distance._2)) },
        { (dist1, dist2) => (dist1._1 + dist2._1, dist1._2 + dist2._2) })
        
      val temperature = interpolate._1/interpolate._2
      temperature
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    val sortedKeys = points.map(point => point._1).toSeq.sorted
    val temp2ColorMap : Map[Temperature, Color] = points.toMap
    
    if (value <= sortedKeys(0)) temp2ColorMap(sortedKeys(0))
    else if (value >= sortedKeys(sortedKeys.length-1)) temp2ColorMap(sortedKeys(sortedKeys.length-1))
    else if (temp2ColorMap.contains(value)) temp2ColorMap(value)
    else linearInterpolate(sortedKeys, temp2ColorMap, value)
  }
  
  /**
   * linear interpolation
   */
  def linearInterpolate(sortedKeys : Seq[Double], temp2ColorMap : Map[Temperature, Color], t : Temperature) : Color = {
    var i = 0
    var t0 = Double.MinValue
    var t1 = Double.MinValue
    
    while (i < sortedKeys.length-1 && t0 == Double.MinValue) {
      if (t > sortedKeys(i) && t < sortedKeys(i+1)) {
        t0 = sortedKeys(i)
        t1 = sortedKeys(i+1)
      }
      i += 1
    }
    if(t0 == Double.MinValue || t1 == Double.MinValue) println("tempe = " + t)
    
    val c0 = temp2ColorMap(t0)
    val c1 = temp2ColorMap(t1)
    
    val red : Int = math.round((c0.red * (t1 - t) + c1.red * (t - t0))/(t1 - t0)).toInt
    val green : Int = math.round((c0.green * (t1 - t) + c1.green * (t - t0))/(t1 - t0)).toInt
    val blue : Int = math.round((c0.blue * (t1 - t) + c1.blue * (t - t0))/(t1 - t0)).toInt
    
    Color(red, green, blue)
  }
  
  
  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val pixels = new Array[Pixel](360*180)
    val topLeft = (90.0, -180.0)
    
    val pixelLocations = for ( i <- (90 until -90 by -1); j <- (-180 until 180)) yield Location(i, j)
    val pixelTemp = pixelLocations.par.map(loc => (loc, predictTemperature(temperatures, loc)))
    
    for ((loc, temp) <- pixelTemp) {
      val row : Int = getRow(loc.lat, topLeft._1)
      val col : Int = getCol(loc.lon, topLeft._2)
      
      val pixelIndex : Int = index(row, col)
      val color : Color = interpolateColor(colors, temp)
      val pixel = getPixel(color)
      
      pixels(pixelIndex) = pixel
    }
    
    val image : Image = Image(360, 180, pixels)
    image
  }
  
  /**
   * get single index from Array(x, y)
   */
  def index(row : Int, col : Int) : Int = row * 360 + col
  
  /**
   * get the row of array from latitude
   */
  def getRow(lat : Double, topLeft : Double) : Int = {
    val row = math.round(topLeft - lat).toInt
    row
  }
  
  /**
   * get the col of array from longitude
   */
  def getCol(lon : Double, topLeft : Double) : Int = {
    val col = math.round(lon - topLeft).toInt
    col
  }
  
  /**
   * get pixel from rgb values
   */
  def getPixel(color : Color) : Pixel = {
    Pixel(color.red, color.green, color.blue, 127)
  }
}