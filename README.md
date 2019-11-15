# github_challange

# Dependency
0. Java 8.
1. org.slf4j, version: '1.7.25'
2. commons-io, version: '2.6'
3. commons-codec, version: '1.5'
4. org.springframework, version: '5.0.12.RELEASE'
5. com.fasterxml', version: '0.9.1'

# Technical decision

Json proccessing: Decided to use stream api feature of jackson json libs.
Since I don't want to load every json structure into memory. There are other libs but, I am not familiar with them.

There are many room to improve. For the current design, I will re-read the json file from disk file to calculate the metric one by one.
We can improve it by build a data structure to keep the data when scanning the json file by using jackson stream api just once,
by using the jackson stream api We only need to read the property that we are interested in.

I decided to download by one hours. So the connection sometimes get rejected from github since it repeatedly request files. We should
adjust this by downloading the larger file, so github doesn't detect our connection like DDOS.

Right now, I focus on make it work first, so the design is simple, The performance is really slow. I want to check the memory usage before store more
information, so it can work on a machine with limitation resource. I don't want to use the framework like apache beam or airflow,
as I am not familiar with them,

The jvm head memory is 2G for now.

The unit test still not coverage. Most of assumption made by me. I cannot say the code is reliable.
And running the same with the other.

I only able to test this on my mac and fedora on google cloud.


# How to run

 navigate to the root of folder. and run the command below.

./gradlew run --args="2019-08-01T00:00:00Z 2019-09-01T00:00:00Z"

The program will collect data from [start, end)
## where:
#### 2019-08-01T00:00:00Z: start date that is the time to start collecting data. It must has that format YYYY-MM-DDTHH:MM:SSZ
#### 2019-09-01T00:00:00Z: end date that is the time to stop collecting data. It must has that format YYYY-MM-DDTHH:MM:SSZ
