# github_challange

# Dependency

### 1. org.slf4j, version: '1.7.25'
### 2. commons-io, version: '2.6'
### 3. commons-codec, version: '1.5'
### 4. org.springframewor, version: '5.0.12.RELEASE'
### 5. com.fasterxml', version: '0.9.1'

# Technical decision

## Json proccessing: Decided to use stream api feature of jackson json libarary.
Since I don't want to load every json structure into memory.

# How to run

./gradlew run --args="2019-08-01T00:00:00Z 2019-09-01T00:00:00Z"

## where:
#### 2019-08-01T00:00:00Z: start date to collect data. It must has that format YYYY-MM-DDTHH:MM:SSZ
#### 2019-09-01T00:00:00Z: end date to collect data. It must has that format YYYY-MM-DDTHH:MM:SSZ
