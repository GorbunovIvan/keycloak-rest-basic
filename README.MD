This app has two microservices:<br/>
    - <b>Book-service</b> - a RESTful microservice protected with the Keycloak<br/>
    - <b>Client-service</b> - a RESTful microservice that interacts with the “book-service” via RestClient or FeignClient with prior invisible (done under the hood) authorization<br/>

If you want to see a more proper way (via Spring-Security) to do the same thing, check out the <b>keycloak-rest-proper-way</b> project