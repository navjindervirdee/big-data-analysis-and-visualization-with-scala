package observatory

/**
  * 6th (and last) milestone: user interface polishing
  */
object Interaction2 {

  import LayerName._
  /**
    * @return The available layers of the application
    */
  def availableLayers: Seq[Layer] = {
    val temperatures = Temperatures
    val deviations = Deviations

    val temperaturesLayer : Layer = Layer(temperatures, temperatures.colorScale, temperatures.yearRange)
    val deviationsLayer : Layer = Layer(deviations, deviations.colorScale, deviations.yearRange)
    Seq(temperaturesLayer, deviationsLayer)
  }

  /**
    * @param selectedLayer A signal carrying the layer selected by the user
    * @return A signal containing the year bounds corresponding to the selected layer
    */
  def yearBounds(selectedLayer: Signal[Layer]): Signal[Range] = {
    Signal(selectedLayer().bounds)
  }

  /**
    * @param selectedLayer The selected layer
    * @param sliderValue The value of the year slider
    * @return The value of the selected year, so that it never goes out of the layer bounds.
    *         If the value of `sliderValue` is out of the `selectedLayer` bounds,
    *         this method should return the closest value that is included
    *         in the `selectedLayer` bounds.
    */
  def yearSelection(selectedLayer: Signal[Layer], sliderValue: Signal[Year]): Signal[Year] = {
    val bounds = Signal(selectedLayer().bounds)
    val minYear = Signal(bounds()(0))
    val maxYear = Signal(bounds()(bounds().length-1))
    val year = Signal(math.min(math.max(minYear(), sliderValue()),maxYear()))
    year
  }

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The URL pattern to retrieve tiles
    */
  def layerUrlPattern(selectedLayer: Signal[Layer], selectedYear: Signal[Year]): Signal[String] = {
    val url : Signal[String] = Signal("target/" + selectedLayer().layerName.id + "/" + selectedYear() + "/{z}/{x}-{y}.png" )
    url
  }

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The caption to show
    */
  def caption(selectedLayer: Signal[Layer], selectedYear: Signal[Year]): Signal[String] = {
    val name = Signal(selectedLayer().layerName.id)
    val cap : Signal[String] = Signal(name().substring(0,1).toUpperCase + name().substring(1) + " (" + selectedYear() + ")")
    cap
  }

}

sealed abstract class LayerName(val id: String)
object LayerName {
  case object Temperatures extends LayerName("temperatures") {
    val colorScale : Seq[(Temperature, Color)] = {
      Seq(
          (-60.0, Color(0,0,0)), (-50.0, Color(33,0,107)), (-27.0, Color(255,0,255)),
          (-15.0,  Color(0,0,255)),   (0.0, Color(0,255,255)),(12.0, Color(255,255,0)), 
          (32.0, Color(255,0,0)), (60.0, Color(255,255,255)))
    }
    val yearRange : Range = (1975 until 2016)
  }
  
  case object Deviations extends LayerName("deviations") {
    val colorScale : Seq[(Temperature, Color)] = {
      Seq(
          (-7.0, Color(0,0,255)), (-2.0, Color(0,255,255)), (0.0, Color(255,255,255)), 
          (2.0, Color(255,255,0)), (4.0, Color(255,0,0)), (7.0, Color(0,0,0)))
    }
    
    val yearRange : Range = (1990 until 2016)
  }
}

/**
  * @param layerName Name of the layer
  * @param colorScale Color scale used by the layer
  * @param bounds Minimum and maximum year supported by the layer
  */
case class Layer(layerName: LayerName, colorScale: Seq[(Temperature, Color)], bounds: Range)

