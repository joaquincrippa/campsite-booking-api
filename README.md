# campsite-booking-api

REST API service to manage camping reservations

## Assumptions

* The time unit of booking is a day.
* The minimum booking time is one day.
* The maximum booking time is three days.
* The campsite has capacity for 10 people. This value es parameterized.
* The booking date can not be longer than one year.

For example: one day, two days and three are correct options, but two hours, thirty hours and four days aren't.

There's one 'availability' entity per day. Initially, the availability of each day is 10 (parametrized value).

## Development

Run the following command:

    ./mvnw spring-boot:run
    
## API Documentation

    http://localhost:8080/swagger-ui.html
    
## Building

Run the following command:

    ./mvnw clean package
    