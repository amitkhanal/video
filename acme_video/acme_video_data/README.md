# Purpose
This is a demo application that's intended to pull movies and related records from AWS S3 and processes and persist them to local storage.

## Application Features
 
1. Retrieve movie artifacts for the year 2017. Artifacts are store in AWS S3 and can be retrieved via "Requester-Pays" scheme. Details on these datasets and accessing them can be found at - http://www.imdb.com/interfaces.
For the purpose of demo, the application will only pull following articats - 
	* title.basics.tsv.gz
	* title.ratings.tsv.gz
	* title.principals.tsv.gz
	* name.basics.tsv.gz

The datasets are pulled only once. There is no recurring / scheduled based pull. 

2. Process and load the datasets into Mongo repository. The datasets are processed into following sequence - 
	* title.basics.tsv.gz (Movie information)
	* name.basics.tsv.gz (Cast information)
	* title.ratings.tsv.gz (Contains ratings by movie)
	* title.principals.tsv.gz (Contains ]casts per movie)

3. Expose REST GET APIs as follows 
	a. **MovieResource**
		i) /movie - Returns paginated list of movies with their ratings and cast if available. Results are sorted by primary tile in descending order
		ii) /movie/search/<searchTerm> - Returns searched paginated list of movies that match the search criteria. Currently, search is performed on primaryTitle of 			movie
   	The data for movie is processed through title.basics.tsv.gz & title.principals.tsv.gz

	b. **RatingResource**
   		i) /rating - Returns paginated list of ratings sorted by average rating in descending order
   		The data for this is processed through title.ratings.tsv.gz 

	c. **CastResource**
   		i) /cast - Returns paginated list of casts. Results are sorted by primary name in ascending order
   he data for this is processed through name.basics.tsv.gz 

	d. **DataResource**
   		i) /data/retrieve - Retrieves the movie datasets from AWS S3 bucket and stores them in local file system. File system locations are configured in 		'netflix_video/netflix_video_data/src/main/java/com/netflix/demo/video/Configuration.java'
   		ii) /data/load - Loads the datasets that were retrieved from AWS S3. Data is loaded into Mongo DB

4. Expose Web UI to access the movie and related records using REST APIs defined on #3


## Supporting software and frameworks
	1. Spring Boot - core application framework
	2. Glassfish Jersey - for exposing REST resources
	3. Amazon S3 - system where movie artifacts are stored
	4. Mongo DB - persistence system
	5. Maven - build configurations
	6. JQuery - UI screen
	7. Amazon S3
	8. JDK 8


## Application Configuration and Installation
	1. Pull aplication locally
	2. Install Mongo DB (if it doesn't already exist). Start mongo instance. Create 'movie' database on it.
	3. Login to your AWS account and generate "Access Key ID" and "Secret Access Key". These will be required to access AWS S3 to retrieve movie datasets. Save the keys in 'credentials' file. Update the location to your credentials file at "netflix_video_data/src/main/java/com/resources/Configuration.properties"
	4. Update file locations and other AWS details at "netflix_video_data/src/main/java/com/resources/Configuration.properties"
	5. Goto 'netflix_video/netflix_video_data'
	6. Execute - mvn spring-boot:run

	Once step 4 is executed -
	1. Access following url to retrieve datasets - /local/retrieve. This will pull movie datasets. This will take some time as there will be multiple files and file 	   sizes are large
	2. After step #1 completes, verify files are created in unprocessed folders as configured in Configuration.
	3. Access /local/load. This will load datasets into Mongo DB. This process will take time to extract, process and load the data.
	4. Once step #3 completes, application data can be accessed via /index.html 

## Implementation Summary
	1. Application uses classes from java nio package to retrieve and load files.
	2. Streams with parallel options are used to load and process files. Refer to persistData method in netflix_video/netflix_video_data/src/main/java/com/netflix/		   demo/video/service/MovieService.java for one such instance.
	3. Since this is a demo exercise and in the interest of time, some aspects are not production grade. For example, some error handling and failover are not 		 properly handled. Also, records are saved into Mongo one at a time instead of batch.
	4. Unit tests are not implemented.
	5. JAVADOC does not exist for all classes and methods.
	6. UI implementation has minimal focus and does not use any data-binding or template mapping frameworks to render data into pages. Instead, simple JQuery APIs are used to construct UI and make REST API calls.
	7. Only some of the entity properties are indexed for faster access in Mongo DB.
