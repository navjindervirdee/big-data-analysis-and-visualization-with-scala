package observatory

import com.sksamuel.scrimage.{Image}
import Interaction.{zoomedSubTiles, getSubTileLocation, getSubTileColors, makeImage}

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    point: CellPoint,
    d00: Temperature,
    d01: Temperature,
    d10: Temperature,
    d11: Temperature
  ): Temperature = {
    val x : Double = point.x
    val y : Double = point.y
    val temp = d00 * (1-x) * (1-y) + d01 * (1-x) * y + d10 * x * (1-y) + d11 * x * y 
    temp  
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param tile Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: GridLocation => Temperature,
    colors: Iterable[(Temperature, Color)],
    tile: Tile
  ): Image = {
    val subTileTemperatures : Seq[Temperature] = getSubTileTemperatures(tile, grid)
    val subTileColors : Seq[Color] = getSubTileColors(subTileTemperatures, colors)
    val image : Image = makeImage(subTileColors, 256, 256)
    image
  }
  
  def getSubTileTemperatures(
    tile : Tile,
    grid : GridLocation => Temperature
  ) : Seq[Temperature] = {
    zoomedSubTiles.par.map(subTile => getTemperature(getSubTileLocation(subTile, tile), grid)).seq
  }
  
  /**
    * get the temperature of the given location 
    */
  def getTemperature(location : Location, grid : GridLocation => Temperature) : Temperature = {
    val d00 : Temperature = getD00(location, grid)
    val d10 : Temperature = getD10(location, grid)
    val d01 : Temperature = getD01(location, grid)
    val d11 : Temperature = getD11(location, grid) 
    
    val point : CellPoint = getCellPoint(location)
    bilinearInterpolation(point, d00, d01, d10, d11)
  }
  
  
  /**
    * get the d00 temperature for bilinear interpolation 
    */
  def getD00(location : Location, grid : GridLocation => Temperature) : Temperature = {
    val lat : Int = {
      val l = math.floor(location.lat).toInt
      if (l < -89) 90 else l
    }
    val lon : Int = {
      val l = math.floor(location.lon).toInt
      if (l < -180) 179 else l
    }
    grid(GridLocation(lat, lon))
  }
  
  /**
    * get the d10 temperature for bilinear interpolation 
    */
  def getD10(location : Location, grid : GridLocation => Temperature) : Temperature = {
    val lat : Int = {
      val l = math.floor(location.lat).toInt
      if (l < -89) 90 else l
    }
    val lon : Int = {
      val l = math.ceil(location.lon).toInt
      if (l > 179) -180 else l
    }
    grid(GridLocation(lat, lon))
  }
  
  /**
    * get the d01 temperature for bilinear interpolation 
    */
  def getD01(location : Location, grid : GridLocation => Temperature) : Temperature = {
    val lat : Int = {
      val l = math.ceil(location.lat).toInt
      if (l > 90) -89 else l
    }
    val lon : Int = {
      val l = math.floor(location.lon).toInt
      if (l < -180) 179 else l
    }
    grid(GridLocation(lat, lon))
  }
  
  
  /**
    * get the d11 temperature for bilinear interpolation 
    */
  def getD11(location : Location, grid : GridLocation => Temperature) : Temperature = {
    val lat : Int = {
      val l = math.ceil(location.lat).toInt
      if (l > 90) -89 else l
    }
    val lon : Int = {
      val l = math.ceil(location.lon).toInt
      if (l > 179) -180 else l
    }
    grid(GridLocation(lat, lon))
  }
  
  /**
    * get CellPoint from Location(lat, lon)  
    */
  def getCellPoint(location : Location) : CellPoint = {
    val flLat = math.floor(location.lat)
    val flLon = math.floor(location.lon)
    
    val y = location.lat - flLat
    val x = location.lon - flLon
    
    CellPoint(x,y)
  }
  
  /**
    * generate tiles for deviation 
    */
  def generateImages(grid : GridLocation => Temperature, colors : Seq[(Temperature,Color)], year : Year, zoom : Int) = {
    val tiles : Seq[Tile] = generateTiles(zoom)
    for (tile <- tiles) {
      val image = visualizeGrid(grid, colors, tile)
      val path = "target/deviations/" + year + "/" + zoom + "/" + tile.x + "-" + tile.y + ".png"
      image.output(new java.io.File(path))
      println("Deviation Image generated --> " + year + "/" + zoom + "/" + tile.x + "-" + tile.y + ".png ....")
    }
  }
  
  /**
    * generate tiles given a zoom 
    */
  def generateTiles(zoom : Int) : Seq[Tile] = {
    val tiles : Int = math.pow(2,zoom).toInt
    for {
      y <- (0 until tiles)
      x <- (0 until tiles)
    } yield Tile(x,y,zoom)
  }
}
