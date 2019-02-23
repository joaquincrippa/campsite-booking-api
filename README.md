# campsite-booking-api

REST API service to manage camping reservations

## Assumptions

* The time unit of booking is a day.
* The minimum booking time is one day.
* The maximum booking time is three days.
* The campsite has capacity for 10 people. This value is parameterized.
* The booking date can not be longer than one year.

For example: one day, two days and three are correct options, but two hours, thirty hours and four days aren't.

There's one 'availability' entity per day. Initially, the availability of each day is 10 (parametrized value).

## Development

Run the following command:

    ./mvnw spring-boot:run
    
## API Documentation

The Doucmentation API was created using Postman. Here is the link to access to:

    https://documenter.getpostman.com/view/4683377/S11GQem5
    
Anybody is able to open the documentatoin using postman and test the endpoints (First, should to start the API).

## Testing

### Integration tests

The integration tests are in src/test/java.

### Concurrency tests

There is a JMETER file, located in src/test/resources, named concurrency-test.jmx, to test the concurrency features of API.
 
    
## Building

Run the following command:

    ./mvnw clean package
    