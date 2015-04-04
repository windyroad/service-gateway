FROM dockerfile/java:oracle-java7
MAINTAINER tom@windyroad.com.au
EXPOSE 80
CMD java -jar service-gateway.jar
ADD build/libs/service-gateway.jar /data/service-gateway.jar