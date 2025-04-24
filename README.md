# Velocity Birds App

## Spring Boot App

The Birds Spring Boot application provides a REST API for CRUD operations on Birds and Sightings. It also enables Redis for caching DB operations and Elasticsearch for supporting fuzzy search for both Bird and Sighting records.

After building it, you can check the generated javadoc by opening the ```birds/target/javadoc/apidocs/index.html``` file in a browser.

You can spin up the app and required services - PostgreSQL DB, Redis and Elasticsearch - using the docker-compose command below.

### build
``` shell
cd birds && mvn clean package
```

### start docker containers
``` shell
docker-compose up -d
```

## Eclipse RCP App

The Birds Eclipse RCP application serves as a UI component for interacting with the Spring Boot app above. It has 4 views:
- Birds View - for checking the Bird records
- Sightings View - for checking the Sighting records
- Add Bird - for adding new Bird records
- Add Sighting - for adding new Sighting records

The Sightings View supports fuzzy search by Bird and Sighting attributes.

### build
``` shell
cd birds-rcp && mvn clean verify
```

### start the app by running the generated executable:
- for Windows, you can find the executable file at (eg.): <br>
```birds-rcp\com.abercu.velo.birds.product\target\products\com.abercu.velo.birds.product\win32\win32\x86_64\eclipse.exe```
- for Linux, you can find the executable file at (eg.): <br>
```birds-rcp/com.abercu.velo.birds.product/target/products/com.abercu.velo.birds.product/linux/gtk/x86_64/eclipse```

For convenience, example build results for the Eclipse RCP app are also saved in the birds-rcp/exported-app folder.

Enjoy!
