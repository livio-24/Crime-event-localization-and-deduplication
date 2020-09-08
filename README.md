# Crime-event-localization-and-deduplication
This repository contains the source code of two applications: 
1. the Crime Ingestion App is a Java applications and aims at extracting, geolocalizing and deduplicating crime-related news articles from two online newspapers of the province of Modena in Italy (ModenaToday https://www.modenatoday.it/ and Gazzetta di Modena https://gazzettadimodena.gelocal.it/modena), the information extracted by the application is stored in a PostgreSQL database,
2. the Crime Visualization App is a Python application and allows visualizing in a web application the crime-related data stored in the PostgreSQ database (online version is available at the link https://dbgroup.ing.unimore.it/crimemap).

<img src="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/Crime%20Visualization%20App/screen.png" width="100%" height="100%" />


The <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/crimedb.sql">crimedb.sql</a> file contains the structure of the PostgreSQL database in which the data of the news articles are stored (the database has to be created before running the Crime Ingestion App).

<img src="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/crimedb.png" width="70%" height="70%" />

## Requirements
Java (JDK 8+)

PostgreSQL 9.6

Python 3.5


## How to run the Crime Ingestion App

### Windows
After installing PostgreSQL on your machine, create the database by using the <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/crimedb.sql">crimedb.sql</a> file.


Then, install Eclipse (https://www.eclipse.org/downloads/) and import the <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/tree/master/Crime%20Ingestion%20App">Crime Ingestion App project</a> as a new Java project.

Modify the config.json file with the configuration parameters to connect to your database.

Build the pom.xml file with Maven to generate the tint library (in Eclipse right-click on the pom.xml file, click Run As, then Maven Build).

Configure the build path by adding all the libraries you can find in the folder called lib, as explained at <a href="https://www.tutorialspoint.com/eclipse/eclipse_java_build_path.htm">this link</a>.

Run the application!

The database will be populated with the information extracted from the online newspapers (the urls of the web pages are listed in the files modenatoday.json and gazzettadimodena.json with the types of crime of the news published on the newspaper).

### Linux
After installing PostgreSQL on your machine, create the database by using the <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/blob/master/crimedb.sql">crimedb.sql</a> file.

After downloading the <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/tree/master/Crime%20Ingestion%20App">Crime Ingestion App folder</a>, create the main.sh file with the following commands:

<div class="highlight highlight-source-shell"><pre>
#!/bin/bash

export CLASSPATH=""
b="/path to the folder/Crime Ingestion App"
cd "/path to the folder/"
for i in $b/bin $b/lib/*.jar
do
&nbsp;&nbsp;&nbsp;&nbsp;export CLASSPATH=$CLASSPATH:$i
done
echo $CLASSPATH<br>
java Main</pre></div>

Then, run the application with the command:
<div class="highlight highlight-source-shell"><pre>
bash main.sh</pre></div>


## How to run the Crime Visualization App
Download the <a href="https://github.com/federicarollo/Crime-event-localization-and-deduplication/tree/master/Crime%20Visualization%20App">Crime Visualization App folder</a>.

Modify the crime_visualization_app.py file inserting the credentials to access to your database:

<div class="highlight highlight-source-shell"><pre>connection_string="dbname='crime_news' user='************' host='localhost' port=5432 password='***************'"</pre></div>
