FROM dockerfile/java:oracle-java8
MAINTAINER tom@windyroad.com.au
EXPOSE 80
ADD build/libs/service-gateway.jar /data/service-gateway.jar
CMD ["java", "-jar",  "service-gateway.jar"]