# webcrawler
will crawl all the available links recursively and store title, url etc in json format to mongodb

## Requirement:
Java 8
Spring boot 1.5
Embeded Mongodb to store the json response and caching
Jsoup library to parse the HTML


## To run the 'webcrawler' application

cd webcrawler

mvn clean install

java -jar target/webcrawler-1.0.jar


Rest End Point:

curl localhost:8080/crawler/rest/api/2?url=http://www.nbnco.com.au

