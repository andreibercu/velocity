# Velocity Birds App

## Spring Boot App 

cd birds

### build
mvn clean package

### start docker containers
docker-compose up -d


## Eclipse RCP App

cd birds-rcp

### build
mvn clean verify

### start the app by running the generated executable:
- for Windows, you can find the eclipse.exe executable file in (eg.): birds-rcp\com.abercu.velo.birds.product\target\products\com.abercu.velo.birds.product\win32\win32\x86_64
- for Linux, you can find the eclipse executable file in (eg.): birds-rcp/com.abercu.velo.birds.product/target/products/com.abercu.velo.birds.product/linux/gtk/x86_64/

### for convenience, example build results are also saved in the birds-rcp/exported-app folder

Enjoy!
