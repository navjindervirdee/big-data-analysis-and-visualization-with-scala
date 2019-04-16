package observatory

object Main extends App {
  
  // for the visualization of the temperatures of all years
  val years = Seq(2006,2007)
  for (year <- years){
    def yearlyRecords = Interaction.getSingleYearData(year)
    println(year + " extracted")
    Interaction.generateTiles(Seq((year,yearlyRecords)), Interaction.generateImage)
  }
  
  // for visualization of temperatures deviations from 1990 to 2015
  val yearlyRecords = Extraction.extractYearlyData(1975, 1990)
  
  val normals = Manipulation.average(yearlyRecords)
  println("normals complete")
  
  val yearlyRecordsForDeviation = Seq(2000, 2001)
  for (record <- yearlyRecordsForDeviation) {
  	val colors = Seq((7.0, Color(0,0,0)), (4.0, Color(255,0,0)), (2.0, Color(255,255,0)), (0.0, Color(255,255,255)),
      (-2.0, Color(0,255,255)), (-7.0, Color(0,0,255)))
  	def yearlyRecords = Interaction.getSingleYearData(record)
    def deviationRecords = Manipulation.deviation(yearlyRecords, normals)
    println(record + " deviation done ....")
    
    for (z <- (0 until 4)) {Visualization2.generateImages(deviationRecords, colors, record, z)}
  }
}
