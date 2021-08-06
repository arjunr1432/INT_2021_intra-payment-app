# Master Card Payments Coding Assignment

## Introduction
We are developing an Intra Payment solution for Modern Bank PLC. 
The application will be a simple HTTP-based REST API with basic 
account operations and payment transfer operation.

System should be:

• accessible by Restful Webservices

• able to tell account balance in real time

• able to get mini statement for last 20 transactions

• able to transfer money in real time

• able to fetch accounts details from accounts service (new / deleted)

## How to install and run the application:
This is a simple spring-boot application with basic spring security.

Inorder to start with local, do the following steps.
1. Clone the repository to your local
```
 git@github.com:arjunr1432/intra-payment-app.git

```
2. Install the application using following command.
```
mvn clean install
```
3. Test the application using following command.
```
mvn clean test
```
4. Start the application using following command.
```
mvn spring-boot:run
```
OR you can get the docker image for the application from my personal repository.
```
https://hub.docker.com/repository/docker/arjunr1432/payment-app
```


## Open API Specification
We have defined the API specification in Open API 3.0, and the same can be found at the below git url.
```
https://github.com/arjunr1432/intra-payment-app/blob/master/swagger/IntraPayment_OpenApi_Specification_V1.yaml
```
## Postman Collection
The complete API collection is added in the following git url, which you can directly import and test our APIs
```
https://github.com/arjunr1432/intra-payment-app/blob/master/MasterCard.postman_collection.json
```

