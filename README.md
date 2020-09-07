# Crime-event-localization-and-deduplication
This repository contains the source code of two applications: 
1. the Crime Ingestion App is a Java applications and aims at extracting, geolocalizing and deduplicating crime-related news articles from two online newspapers of the province of Modena in Italy (ModenaToday https://www.modenatoday.it/ and Gazzetta di Modena https://gazzettadimodena.gelocal.it/modena), the information extracted by the application is stored in a PostgreSQL database,
2. the Crime Visualization App is a Python application and allows visualizing in a web application the crime-related data stored in the PostgreSQ database (online version is available at the link https://dbgroup.ing.unimore.it/crimemap).

<img src="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/Crime%20Visualization%20App/screen.png" width="100%" height="100%" />


The <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/crimedb.sql">crimedb.sql</a> file contains the structure of the PostgreSQL database in which the data of the news articles are stored (the database has to be created before running the Crime Ingestion App).


## Requirements
Java (JDK 8+)

PostgreSQL 9.6

Python 3.5


## How to run the Crime Ingestion App

### Windows
After installing PostgreSQL on your machine, create the database by using the <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/crimedb.sql">crimedb.sql</a> file.


Then, install Eclipse (https://www.eclipse.org/downloads/) and import the <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/tree/master/Crime%20Ingestion%20App">Crime Ingestion App project</a> as a new Java project.

Modify the config.json file with the configuration parameters to connect to your database.

Run the pom.xml file to generate the tint library.

Configure the build path by adding all the libraries you can find in the folder called lib as explained at <a href="https://www.tutorialspoint.com/eclipse/eclipse_java_build_path.htm">this link</a>.
