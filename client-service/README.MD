<h3>Client-service</h3>

<p>This is a simple microservice that can interact with the “book-service” via RestClient or FeignClient with prior authorization.</p>

<p>Clients of this service do not need to know about the "book-service" or worry about authorization there.
<br/>
When interacting with this service, they simply send their requests, and the “client-service” will independently connect to the "book-service" under the hood and carry out all the necessary authorization. 
</p>
