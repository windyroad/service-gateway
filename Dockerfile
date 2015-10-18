FROM dockerfile/java:oracle-java8

# Set the WORKDIR. All following commands will be run in this directory.
WORKDIR /app

# Copying all gradle files necessary to install gradle with gradlew
COPY gradle gradle
COPY \
  build.gradle \
  gradle.properties \
  gradlew \
  settings.gradle \
  ./

# Install the gradle version used in the repository through gradlew
RUN ./gradlew

# Run gradle assemble to install dependencies before adding the whole repository
RUN gradle assemble

ADD . ./


#MAINTAINER tom@windyroad.com.au
#EXPOSE 80
#ADD build/libs/service-gateway.jar /data/service-gateway.jar
#CMD ["java", "-jar",  "service-gateway.jar"]