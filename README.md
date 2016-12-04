## Basketball Roster Optimizer

Java wrapper for the fantasy roster optimization algorithm.

##Basic code overview:
### Server Side (Java land)
- src/main/java/Main.java: This file declares the web service and all of its routes. Here is where we implement the entry point to API calls.
- src/main/java/optimizer/helpers/: This directory contains the classes used to run the algorithm: Player, Team, TeamOptimizer, MonserParser)

### Client Side (Angular land)
- src/main/resources/home.html: This is the landing page for the site right now. It is an HTML page that brings in all of the javascript needed to run the page. Check the file for comments explaining the different sections. 
- src/main/resources/js/app.js: This instantiates the angular application and is where we declare the dependencies it has.
- src/main/resources/js/controllers.js: This is where I implement the angular controller that provides the functionality to the page.
- src/main/resources/bower_components: These are dependencies (ie libraries, miscellaneous things needed for the site to run)


