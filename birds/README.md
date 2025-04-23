# Build your app first
./mvnw clean package -DskipTests

# Then start the containers
docker-compose up --build
