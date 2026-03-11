1. config server 기동이 가장 먼저 되어야 한다.
> cd configserver
> ./gradlew bootRun

다만, cloud bus 사용하게 된다면 config server전에 doker 이용해서 rabbitMQ 실행

2. eureka server 기동
> ./gradlew bootJar
> java -jar XXXXX.jar
> java -jar build/libs/eureka-0.0.1-SNAPSHOT.jar

3. api gateWay 기동
> cd apigateway
> ./gradlew bootJar
> java -jar xxxx.jar

4. 각 서비스객체가 기동(user, product, order)
> cd user, product, order
> ./gradlew bootJar
> java -jar xxxx.jar

ps) 특이사항
order 기동시 kafka(비동기) - zookeeper - doker-compose
> docker-compose up

endpoint)
http://localhost:port/user-service/user/signIn
http://localhost:port/product-service/product/create
http://localhost:port/order-service/order/create