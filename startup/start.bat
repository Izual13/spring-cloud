cd ../
call mvn clean package
start java -Xmx256m -jar eureka-server\target\eurekaserver-0.0.1-SNAPSHOT.jar
TIMEOUT 20
start java -Xmx256m -jar config-server\target\configserver-0.0.1-SNAPSHOT.jar
TIMEOUT 20
start java -Xmx256m -jar router\target\router-0.0.1-SNAPSHOT.jar
#TIMEOUT 10
start java -Xmx256m -jar websocket-server\target\websocketserver-0.0.1-SNAPSHOT.jar
#TIMEOUT 10
start java -Xmx256m -jar websocket-server-without-config-server\target\websocketserverwithoutconfigserver-0.0.1-SNAPSHOT.jar