# Используем официальный образ Maven для сборки
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Копируем pom.xml и загружаем зависимости (кэшируется если pom.xml не изменился)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests

# Финальный образ с приложением
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем собранный JAR из stage build
COPY --from=build /app/target/pet-project-*.jar app.jar

# Открываем порт приложения
EXPOSE 8081

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]

