# Spring security JWT + SWAGGER 3

[Basic auth /api/login](http://127.0.0.1:8080/api/login)

if using with postman add body keys
```
username: john  
password: 1234  
```
And  
`Content-Type: application/x-www-form-urlencoded`
### For Swagger:
[Use access_token from /api/login to authorize /swagger-ui/index.html](http://127.0.0.1:8080/swagger-ui/index.html)
 

Run:
1) `mvn spring-boot:run`  
2) `docker run -it --rm -p 5432:5432 --name pg_test -e POSTGRES_USER=spring_test -e POSTGRES_PASSWORD=spring_test -e POSTGRES_DB=spring_test  postgres:11.11`    

Thanks for Youtube: Amigoscode{Spring Boot and Spring Security with JWT including Access and Refresh Tokens}
