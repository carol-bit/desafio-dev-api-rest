# Etapa 1: Build com Maven + JDK 17
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copia arquivos do projeto
COPY pom.xml .
COPY src ./src

# Build da aplicação e criação do jar
RUN mvn clean package -DskipTests

# Etapa 2: Imagem leve para rodar a aplicação
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia o jar da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]
