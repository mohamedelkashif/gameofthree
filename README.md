# Game of three Java implementation

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)


## General Info
1. The game is implemented using Java as programming language as a [maven](https://maven.apache.org/) project.
2. I used [IntelliJ IDEA](https://www.jetbrains.com/idea/) as an IDE.
3. I only used Vanilla Java with Junit and log4j dependencies.
4. I used sockets to handle communication between players playing in same time with each other.

## Definition of done

1. Each player run Independently.
2. Usage of sockets in communications
3. Use DDD in designing and implementation of the projects.
4. The project is configurable through `application.properties`

## Installation and setup

##### Requirements

- Java 8 
- Maven
- tool for tcp communication, I used mac terminal with [netcat](https://nmap.org/ncat/)

##### Configuration

- You should have the following two file, take a look at them and you can change the values in them depending on your preferences
`resources/application.properties` and `resources/log4j.properties`
- Run the following command in order to build the maven project `mvn clean install` 
- Start the server through running the following command `mvn exec:java`
- Run [netcat](https://nmap.org/ncat/) client on the same port the server running `netcat localhost 9999`
- Tou should see a message with connected string on both terminals.

##### How its working

```
➜  ~ netcat localhost 9999
connected
ADD_PLAYER:MOHAMED
Added player MOHAMED to game.
ADD_MACHINE
Added computer player Computer2 to game.
START
Game started. The starting number is 100. Next to play is player MOHAMED.
PLAY:1
ERROR: could not play this round because of invalid input last round output was 100 and your input was 1
PLAY:0
ERROR: could not play this round because of invalid input last round output was 100 and your input was 0
PLAY:-1
player MOHAMED played number -1. The result is Round result:outputNumber 33, winner false.

computer Computer2 played number 0. The result is Round result:outputNumber 11, winner false.
PLAY:1
player MOHAMED played number 1. The result is Round result:outputNumber 4, winner false.

computer Computer2 played number -1. The result is Round result:outputNumber 1, winner true.
EXIT
Quitting
```

#### Testing
- You can run tests through either from IDE or through commandline `mvn test`

#### Future work

1. Adding more test cases.
2. Having a front-end or a dashboard for players instead of terminal.
3. Dockerfile 

#### Note: you will notice the there is two contributors, this is becayse my terminal and GitKraken is configured with two different emails so I continiued with my main one.

## Author
[Mohamed Mahmoud Hassan Elkashif](mailto:muhammedmahmmoudd@gmail.com
)
