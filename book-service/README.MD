<h3>Book-service</h3>

<p>This is a simple RESTful microservice protected with the Keycloak for testing the Keycloak on it.</p>

<p>To start using it, you need to first obtain an <b>access-token</b> by requesting the "/auth/login" endpoint.<br/>For example:</p>
<pre>
    POST http://localhost:8081/auth/login
    Content-Type: application/json
    {
      "username": "user-1",
      "password": "user-1"
    }
</pre>

<p>It will return an <b>access-token</b> (if the credentials are correct and the Keycloak server is running).</p>
<p>You should then remember this <b>access-token</b> and add it to all your requests to "/api" endpoints on this service as an authorization bearer.<br/>For example:</p>
<pre>
    GET http://localhost:8081/api/v1/books
    Authorization: Bearer eyJhbGliOiJSUzI1NiIsInR5cCIgOiAiLldUIiwia2lkIiA6ICJleHU0OTFqZ282QkV1TWVLcyg5RBc0R..
</pre>

<p>Please note that the <b>access-token</b> is only valid for a short period of time, so if you want to regenerate it smoothly, you can make for example this request:</p>
<pre>
    POST http://localhost:8081/auth/check-access-token/eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJleHU0OTFqZ282QkV1TWVLczg5RUc0RWpYX2Nfc0pQaE1wd3FfY0xWSWFNIn0.eyJleHAiOjE3MTU4NTgyMzMsImlhdCI6MTcxNTg1NzkzMywianRpIjoiZWYyYjIzYjUtNWU2Zi00ODYzLTkzODItNTg1MzA5NjU2MTM1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9rZXljbG9hay1yZXN0LWJhc2ljIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImQ0NmZmNTQ2LWE2MjUtNDlkYS1hMjBkLTNiMzRkNDhhZjFkMCIsInR5cCI6IkJlYXJlciIsImF6cCI6ImJvb2stc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiIxZTBkZGQwZS0wMjRkLTQ2ZWItYmFjYS0wYTgwMzU0NzMxMDkiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9hcGkvdjEvYm9va3MiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLWtleWNsb2FrLXJlc3QtYmFzaWMiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiIxZTBkZGQwZS0wMjRkLTQ2ZWItYmFjYS0wYTgwMzU0NzMxMDkiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJ1c2VyLTEgdXNlci0xIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlci0xIiwiZ2l2ZW5fbmFtZSI6InVzZXItMSIsImZhbWlseV9uYW1lIjoidXNlci0xIiwiZW1haWwiOiJ1c2VyLTFAbWFpbC5jb20ifQ.Kj9Ra9Qk459mfUR_VWNgfpwfMEHnVvcEmpngz5LGRen49nOp4NDuA8hfJuRt7cmnL7X2YunKgE1ejzGaTZH5VyOjWpukNO2zw3JkVi27W_WNvTDrzBrfE7TLNbzvm19r8R3vso51eT6mAL7dInxzUuzPA3sKDmxTbXCxG2oLyMGcPYIbsdMcRIHb3s8cDeveulO9xHZ28E5tyIYfH7wrCD3R1xE9JwRYRS4elahKrx_FBDrVVnocMGRonOxvSujF9Q9_LWe978xvx41wVE8Wn-tMRB2qKJJIAUbiwxBQZxD9zmp4tmE65jqkTkXsePqEiBtU6xjCyeZmN33ZHPJcUg
    Content-Type: application/json
    {
      "username": "user-1",
      "password": "user-1"
    }
</pre>
