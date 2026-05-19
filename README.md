# Weather Forecast

This application exposes a REST endpoint for retrieving weather forecasts based on geographic coordinates and a target time.

## Table of Contents

- Technologies and Libraries
- Assumptions
- Installation
- Test
- Production
- Possible improvements in this code
- AI usage


## Technologies and Libraries
The program is built using the following technologies and libraries:

1. `Java 25` The programming language used.
2. `Spring Boot 4.0.6` Used for creating REST APIs.
3. `Junit 5`  Used for testing.
4. `Docker` Used to run the application in a container.
5. `Caffeine` Because it is thread-safe. If many requests for the same key arrive at the same time, only one request will be sent to the MET Norway API.

## Assumptions
1. I made the following assumption based on this part of the task: “for any event that starts in the next 7 days and has a location set.”
If an event starts more than 7 days from now, we should not receive any requests related to that event. Therefore, I cached 7 days of location-based data and added this rule as a validation in the REST API request.
2. If there is no weather forecast available for the exact time when an event starts, I use the nearest available value. If there are two equally close values, I use the one from the later time.

## Installation
To install this application, follow these steps:

1. Clone this repository to your local machine.
2. In order to install the required dependencies and run to tests by running `mvn clean install` -> run tests and build application artifact. Note that you need to have JDK 25 installed on your local machine.
3. Start the server by running `docker-compose build --no-cache && docker-compose up` -> run application

## Test
```
GET /api/v1/weather-forecast
```

### Query Parameters

| Parameter   | Type    | Required | Description                                                        |
|-------------|---------|----------|--------------------------------------------------------------------|
| `lat`       | Double  | Yes      | Latitude of the location. Must be between `-90.0` and `90.0`.      |
| `lon`       | Double  | Yes      | Longitude of the location. Must be between `-180.0` and `180.0`.   |
| `startTime` | Instant | Yes      | Target time in ISO-8601 format. Must not be more than 7 days ahead.|


#### GET method examples

To test this application, execute the command below:

**Valid request:**
```bash
curl -X GET "http://localhost:8080/api/v1/weather-forecast?lat=59.9139&lon=10.7522&startTime=2026-05-20T12:00:00Z"
```

If the parameters are not valid, client gets error.


## Production
For a production setup, I could design this application as follows:
When an event is created, I would fetch the weather data asynchronously, cache it, and refresh it periodically. If we assume there are many events, I would process this in batches. This would reduce the number of requests sent to the MET Norway API during sudden traffic peaks.
I would also create a Redis cluster in the backend to provide a distributed cache. In the current implementation, if the application is running on 5 different instances, each instance may call the MET Norway API separately for the same latitude and longitude values. With a distributed cache, this number would be reduced to one.
Based on my current assumptions, I do not think a separate data source is needed, but this could change depending on the requirements.
Also, the MET Norway API has a limit of 20 requests per second. I did not handle this separately because I think the approach above would solve it.
However, if we still want to add a safeguard, we could use a distributed rate limiter. We can implement this using Redis.

## Possible improvements in this code
I added a limited number of tests due to time constraints. I focused more on applying SOLID principles and reducing the number of requests sent to the MET Norway API under high traffic.
Ideally, every line could have been covered by tests. Also, some validations could have been implemented in a more generic way by using Spring’s built-in features.
Additionally, as I mentioned in the production design section, a second cache layer such as Redis could be added, together with a distributed rate limiter.

## AI usage
The design here is entirely my own, including the package structure, class naming, and overall architecture.
For the parts where I used AI, I added an AI USAGE note. In general, I used AI as an alternative to searching on Google.

   
