# IS-SPAPI
Repository for the source code of "Querying APIs With SPARQL"

## Instructions
You have to compile the code with Maven, changing the paths where the database is stored (using TDB).

## Clarifications

1. The `FUSEKI_ENABLED` variable has to be `false`. This is for allowing the code to interact with Apache Fuseki and support concurrent requests. I used this only for the demo.
2. In order to run the queries, you have to store your data with Jena TDB. If you have an RDF document and you want to use as your database you have to load it. There is a class called `LoadTDB`. This class needs a `TDBDirectory`, which is a folder that must exists in your computer, where the data will be stored. The first time you use it, it has to be empty. Second you have to say where is your data, and the format. I recommend to use NT or TTL.
3. You have to look at the class ExperimentsReal, where you can find the queries using SERVICE-to-API. Here you have to declare de `TDBDirectory` too.
4. In this class there exists some Strategy and Params variables. The first one indicates which Strategy will you use. It may be `OAuthStrategy` (Twitter and Yelp use this) or `BasicStrategy` (OpenWeather uses this). You have to create Objects which have to be stored in a StrategiesArray (I sent you the class with examples). The params are required for passing some configuration values that are needed. I recommend to keep them as they are. Also note that the number of params and strategy objects are equal to the number of SERVICEs. If you have a question with this just let me know.

## Example
1) First, load the NT into an empty folder with the LoadTDB class.
2) Take a look to the `ExampleOpenWeather` class into the project.
3) Change the path for the one where you stored the data.
4) Then execute the query. Recall that no extra parametrization is needed.
