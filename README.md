# Crime-event-localization-and-deduplication
This repository contains the source code of two Java applications: 
1. the Crime Ingestion App aims at extracting, geolocalizing and deduplicating crime-related news articles from two online newspapers (ModenaToday https://www.modenatoday.it/ and Gazzetta di Modena https://gazzettadimodena.gelocal.it/modena), then information is stored in a PostgreSQL database,
2. the Crime Visualization App allows visualizing crime-related data in a web application (online version is available at https://dbgroup.ing.unimore.it/crimemap).

<img src="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/Crime%20Visualization%20App/screen.png" width="100%" height="100%" />


The <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/crimedb.sql">crimedb.sql</a> file contains the structure of the PostgreSQL database in which the data of the news articles are stored (the database has to be created before running the Crime Ingestion App).


## Requirements
Java (JDK 8+)

PostgreSQL 9.6

Python 3.6


## How to run the Crime Ingestion App

### Windows
Create the database on your machine.

Then, install Eclipse (https://www.eclipse.org/downloads/) and import the <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/tree/master/Crime%20Ingestion%20App">Crime Ingestion App project</a>. Modify the config.json file with the configuration parameters to connect to your database.
