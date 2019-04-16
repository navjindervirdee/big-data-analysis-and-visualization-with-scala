package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import Visualization._
import scala.collection._
import Extraction._
import java.time.LocalDate

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    val lon : Double = toLongitude(tile.x, tile.zoom)
    val lat : Double = toLatitude(tile.y, tile.zoom)
    Location(lat, lon)
  }
  
  /**
    * convert x co-ordinate of tile to longitude in degrees
    * long = (x * 360)/numOfTiles - 180
    */
  def toLongitude(x : Int, zoom : Int) : Double = {
    val tiles : Double = numOfTiles(zoom)
    val longitude : Double = (x.toDouble * 360.0)/tiles - 180.0
    longitude
  }
  
  /**
    * convert y co-ordinate of tile to latitude in degrees
    * lat = atan(sinh(pi - (y*2*pi)/numOfTiles)) * 180/pi
    */
  def toLatitude(y : Int, zoom : Int) : Double = {
    val tiles : Double = numOfTiles(zoom)
    val pi : Double = math.Pi
    val latitude : Double = math.atan(math.sinh(pi - (y.toDouble * 2.0 * pi) / tiles)) * 180.0 / pi
    latitude
  }
  
  /**
    * number of tiles from value zoom
    * n = 2^zoom
    */
  def numOfTiles(zoom : Int) : Double = {
    math.pow(2, zoom)
  }
  
  
  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    val subTileTemperatures : Seq[Temperature] = getSubTileTemperatures(tile, temperatures)
    val subTileColors : Seq[Color] = getSubTileColors(subTileTemperatures, colors)
    val image : Image = makeImage(subTileColors, 256, 256)
    image
  }
  
  // zoom level of subtiles
  val zoom : Int = 8
  
  /**
    * create subtiles of a tile
    * @params zoom level z - 2^z num of sub tiles to generate
    * @params the tile 
    */
  val zoomedSubTiles : Seq[(Int, Int)] = {
    val numOfSubTiles : Int = numOfTiles(zoom).toInt
    for {
      y <- (0 until numOfSubTiles)
      x <- (0 until numOfSubTiles)
    } yield (x,y)
  }
  
  /**
    * convert subTiles to their Location values
    */
  def getSubTileLocation(subTile : (Int, Int), tile : Tile) : Location = {
    val numOfSubTiles : Int = numOfTiles(zoom).toInt
    val addX = tile.x * numOfSubTiles
    val addY = tile.y * numOfSubTiles
    tileLocation(Tile(subTile._1 + addX, subTile._2 + addY, zoom + tile.zoom))
  }

  /**
    * get the temperatures values of each tile
    */
  def getSubTileTemperatures(
    tile : Tile,
    temperatures : Iterable[(Location, Temperature)]): Seq[Temperature] = {
    val subTileTemperatures : Seq[Temperature] = zoomedSubTiles.par.map (
      subTile => predictTemperature(temperatures, getSubTileLocation(subTile, tile))
    ).seq
    subTileTemperatures 
  }

  /**
    * get colors of subtiles from the 
    * corresponding temperatures values
    */
  def getSubTileColors(temperatures : Seq[Temperature], colors : Iterable[(Temperature,Color)]) : Seq[Color] = {
    val subTileColors : Seq[Color] = temperatures.par.map(temp => interpolateColor(colors, temp)).seq
    subTileColors
  }
  
  /**
    * make the Image from Seq[Color]
    */
  def makeImage(colors : Seq[Color], width : Int, height : Int) : Image = {
    val pixels : Array[Pixel] = getArrayOfPixels(colors)
    assert(pixels.length == width*height)
    Image(width, height, pixels)
  }
  
  /**
    * make an array of pixel from Seq[Colors]
    */
  def getArrayOfPixels(colors : Seq[Color]) : Array[Pixel] = {
    val pixels = colors.par.map(color => getPixel(color, 127)).seq.toArray
    pixels
  }
  
  /**
    *  get pixel from array
    */
  def getPixel(color : Color, alpha : Int) : Pixel = {
    Pixel(color.red, color.green, color.blue, alpha)
  }
  
  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = {
    
    val allTiles : Seq[Seq[Tile]] = for ( z <- (0 until 4)) yield generateTiles(z)
    for {
      data <- yearlyData
      tiles <- allTiles
      tile <- tiles
    }{
      generateImage(data._1, tile, data._2)
    }
  }
  
  /**
    * get the data of a single year
    * using Extraction.scala
    */
  def getSingleYearData(year : Year) : Iterable[(Location, Temperature)] = {
    val stationsFile : String = "/stations.csv"
    val temperaturesFile : String = "/" + year + ".csv"
    val records : Iterable[(LocalDate, Location, Temperature)] = locateTemperatures(year, stationsFile, temperaturesFile)
    val avgRecords : Iterable[(Location, Temperature)] = locationYearlyAverageRecords(records)
    println("Avg Records for year " + year + " compelete ....")
    avgRecords
  }
  
  /**
    * extract yearly data and return tuples (year, data)
    */
  def extractYearlyData = {
    for (year <- (2001 until 2006)) yield (year, getSingleYearData(year))
  }

  /**
    * Function that generates an image given a year, a zoom level, the x and
    * y coordinates of the tile and the data to build the image from
    */
  def generateImage(year: Year, singleTile: Tile, data: Iterable[(Location, Temperature)]) = {
    val colors = Seq(
      (60.0, Color(255, 255, 255)), (32.0, Color(255, 0, 0)),
      (12.0, Color(255, 255, 0)), (0.0, Color(0, 255, 255)),
      (-15.0, Color(0, 0, 255)), (-27.0, Color(255, 0, 255)),
      (-50.0, Color(33, 0, 107)), (-60.0, Color(0, 0, 0)))
    
      val image = tile(data, colors, singleTile)
      val path = "target/temperatures/" + year + "/" + singleTile.zoom + "/" + singleTile.x + "-" + singleTile.y + ".png"
      image.output(new java.io.File(path))
      println("Image generated --> " +  + year + "/" + singleTile.zoom + "/" + singleTile.x + "-" + singleTile.y + "....")
  }
  
  /**
    * generate tiles given a zoom 
    */
  def generateTiles(zoom : Int) : Seq[Tile] = {
    val tiles : Int = numOfTiles(zoom).toInt
    for {
      y <- (0 until tiles)
      x <- (0 until tiles)
    } yield Tile(x,y,zoom)
  }

}
