# =========================
# 1. Etapa de compilacion
# =========================
# Imagen base con Java 21 y el JDK completo (incluye compilador y herramientas de desarrollo).
# La llamamos "build" para poder referenciarla despues desde la segunda etapa.
FROM eclipse-temurin:21-jdk AS build

# Carpeta de trabajo dentro del contenedor. A partir de aqui, todo se ejecuta relativo a /app.
WORKDIR /app

# Copiamos primero solo lo necesario para descargar dependencias:
# el Maven Wrapper, su configuracion, y el pom.xml.
# Todavia NO copiamos el codigo fuente (src) a proposito.
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Damos permisos de ejecucion al script mvnw.
# En Linux, un archivo no es ejecutable por defecto solo por existir.
RUN chmod +x mvnw

# Descargamos todas las dependencias del pom.xml y las dejamos cacheadas en esta capa.
# -B = batch mode (modo no interactivo).
# Truco de cache: como esta capa solo depende del pom.xml, si despues cambiamos
# solo el codigo Java, Docker puede reutilizar esta capa sin volver a descargar nada.
RUN ./mvnw dependency:go-offline -B

# Ahora si copiamos el codigo fuente completo.
COPY src src

# Compilamos el proyecto y generamos el .jar final dentro de target/.
# -DskipTests: no ejecutamos los tests aqui porque ya se ejecutan en local antes de llegar a este paso.
RUN ./mvnw clean package -DskipTests


# =========================
# 2. Etapa de ejecucion
# =========================
# Segunda imagen, independiente de la primera.
# Solo trae el JRE (Java Runtime Environment): lo minimo para EJECUTAR Java,
# sin compilador ni herramientas de desarrollo. Por eso es mas ligera que la del JDK.
FROM eclipse-temurin:21-jre

# Carpeta de trabajo dentro de esta segunda imagen (independiente de la de la fase 1).
WORKDIR /app

# Copiamos SOLO el .jar ya compilado desde la fase "build" (--from=build).
# Todo lo demas de la fase 1 (Maven, JDK completo, codigo fuente sin compilar) se descarta:
# no forma parte de esta imagen final, por eso el resultado es mucho mas ligero.
# El comodin *.jar evita depender del nombre exacto del artefacto (por si cambia la version).
COPY --from=build /app/target/*.jar app.jar

# Documenta que la aplicacion escucha en el puerto 9090 dentro del contenedor.
# Esto no abre el puerto hacia fuera por si solo; eso se define al ejecutar el contenedor
# (con -p en docker run, o con "ports" en docker-compose.yml).
EXPOSE 9090

# Comando que se ejecuta automaticamente cuando arranca el contenedor.
# Equivale a ejecutar "java -jar app.jar" a mano.
ENTRYPOINT ["java", "-jar", "app.jar"]