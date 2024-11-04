# Functional tests for todo application
- Java 17 or higher
- Kotlin 1.9.x
- Gradle 8.x
- Docker

## Getting Started

### Before running tests

To set up the Docker container for the tested todo list application, follow these steps:
The image of the tested todo list application in [app](/app) folder.

Complete this command:

`docker load -i ./app/todo-app.tar`

`docker run -d -p 8080:4242 --name todo-app todo-app:latest`

Application available by address http://127.0.0.1:8080

### Run test

To run the tests, execute the following command:

`./gradlew clean test`

### Build allure report

To generate and serve the Allure report, execute the following commands:

`./gradlew :allureServe`

`./gradlew :allureReport./gradlew :allureReport`

report located `.\testertask\build\reports\allure-report\allureReport`


