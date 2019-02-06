# Big Data Analysis and Visualization with Scala

This is the last course and captsone project of Functional Programming in Scala Specialization. Worked on this project to enhance my skills in Scala Language and to learn how to process and analyze massive amounts of data.

## Tasks

* In this project the first task is to analyze and visualize the climate temperatures data all the world from the year 1975 to 2015.
* Second task is to visualize how the temperatures values have varied or deviated all over the globe from the year 1990 to 2015. 

## Dataset

First, let's see how the dataset looks like:

* <strong> Stations Data </strong>

    This data represents information about Temperature Station all aroung the globe. Stn and Wbn identifies unique stations and Lat and       Lon are latitude and longitude values.

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/stations.jpg"/> 
</p>


* <strong> Years Data </strong>

    This represents the information about temperatures on every day for all the years from 1975 to 2015. Each year has its own file with       temperatures values. 
    
<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/year.jpg"/> 
</p>


## Tools and Technologies Used

* **Scala Language:** Scala is a general-purpose programming language providing support for functional programming and a strong static type system. Scala combines object-oriented and functional programming in one concise, high-level language. Scala's static types help avoid bugs in complex applications, and its JVM and JavaScript runtimes let you build high-performance systems with easy access to huge ecosystems of libraries.

* **Functional Programming:** In computer science, functional programming is a programming paradigm—a style of building the structure and elements of computer programs—that treats computation as the evaluation of mathematical functions and avoids changing-state and mutable data. It is a declarative programming paradigm, which means programming is done with expressions or declarations instead of statements. In functional code, the output value of a function depends only on the arguments that are passed to the function, so calling a function f twice with the same value for an argument x produces the same result f(x) each time; this is in contrast to procedures depending on a local or global state, which may produce different results at different times when called with the same arguments but a different program state.

* **Parallel Programming:** Parallel computing is a type of computation in which many calculations or the execution of processes are carried out simultaneously. Large problems can often be divided into smaller ones, which can then be solved at the same time. There are several different forms of parallel computing: bit-level, instruction-level, data, and task parallelism.

* **Scala IDE for Eclipse:** Scala IDE provides advanced editing and debugging support for the development of pure Scala and mixed Scala-Java applications. It has shiny Scala debugger, semantic highlight, more reliable JUnit test finder, an ecosystem of related plugins, and much more.

* **Scala Build Tool:** Scala Build Tool (SBT) is an open-source build tool for Scala and Java projects, similar to Java's Maven and Ant.

## Process

* **Task-1:-** Analysis and Visualization of Temperature Data all aorund the globe from 1975 to 2015.

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/task1.jpg"/> 
</p>

* Explanation:

    * **Data Cleaning:** The first and most importatnt step is to clean the data as some values are missing and few are wrong.
    * **Average Temperature Values:** Next find the average temperatures values for each year individually at all the unique lat and lon locations.
    * **Predict Temperature Values:** Using the average temperatures values, predict temperatures at other locations (Mercatron Projection and Tile Concept) using spatial interpolation.
    * **Predict Colors:** After finding all the temperatures values, predict the color values for them using linear interpolation.
    * **Visualize and Project Colors on Map:** Using Mercatron Projection and Tiles concept, visualize the temperatures on the global map with different zoom levels.

<br>
    
* **Task-2:-** Analysis and Visualization of Deviations in Temperature Data all aorund the globe from 1990 to 2015.

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/task2.jpg"/> 
</p>


 * Explanation:

    Part-1

    * **Data Cleaning:** The first and most importatnt step is to clean the data as some values are missing and few are wrong.
    * **Average Temperature Values:** Next find the average temperatures values for each year individually at all the unique lat and lon locations.
    * **Predict Fixed Grid Points Temperature Values:** Using average temperatures values, predict temperatures at fixed grid locations for the each year from 1975 to 1989 individually. Grid [lat, long] -- lat = [90, -89], lon = [-180, 179]
    * **Average the Temperature Values at Grid Points:** Averaget the temperatures at the fixed grid locations for all the years from 1975 to 1989.
    * **Now use these as Normals to find Deviations:** The above average temperatures are used as normals to find the deviation in temperature values.
    
    Part-2
    
    * **Data Cleaning Years 1990-2015:** The first and most importatnt step is to clean the data as some values are missing and few are wrong.
    * **Average Temperature Values:** Next find the average temperatures values for each year individually at all the unique lat and lon locations.
    * **Predict Fixed Grid Temperature Values:** Using average temperatures values, predict temperatures at fixed grid locations for the each year from 1990 to 2015 individually.
    * **Find the Deviations at Grid Points using Normals:** Using normals from part-1, find deviations at the grid points by using subtraction.
    * **Predict Temperature Values:** Using the deviation temperatures values, predict temperatures at other locations (Mercatron Projection and Tile Concept) using bilinear interpolation.
    * **Predict Colors for Deviation Values:** After finding all the deviation temperatures values, predict the color values for them using linear interpolation.
    * **Visualize and Project Colors on Map:** Using Mercatron Projection and Tiles concept, visualize the temperatures on the global map with different zoom levels.


## Visualization of Temperatures for the Year 2015

### **Zoom 0**
 
<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/temperatureszoom0.JPG"/> 
</p>

### **Zoom 1**

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/temperatureszoom1.JPG"/> 
</p>


### **Zoom 2**

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/temperatureszoom2.JPG"/> 
</p>


### **Zoom 3**

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/temperatureszoom3.JPG"/> 
</p>


## Visualization of Deviations in Temperatures for the Year 2009

### **Zoom 0**
 
<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/deviationszoom0.JPG"/> 
</p>

### **Zoom 1**

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/deviationszoom1.JPG"/> 
</p>


### **Zoom 2**

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/deviationszoom2.JPG"/> 
</p>


### **Zoom 3**

<p align ="center">
<img src="https://github.com/navjindervirdee/big-data-analysis-and-visualization-with-scala/blob/master/target/Util%20Images/deviationszoom3.JPG"/> 
</p>
