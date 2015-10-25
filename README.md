# Open Video Guard
This is a open source platform for processing videos and pictures from cameras.
![Open Video Guard overview](https://raw.githubusercontent.com/pbolle/openvideoguard/master/doc/ovg.png)

## Dependencies
Open Video Guard is base on Scala, Akka and Play.

## Configuration
The application can configured like all Play apps in application.conf.
In this file you will find explanations in the comments.

## Aktor List
List with all actors an it responsibility

| Actor       | Description |
|-------------|-------------|
|Archive      |       |
|DeleteImage  |   |
|LoadImage  |   |
|LoadVideo  |   |
|Reimport  |   |

## Create a Release

sbt dist

## License

This project is licensed under the Apache 2.0 License. See LICENSE for full license text.