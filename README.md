# Open Video Guard
This is a open source platform for processing videos and pictures from cameras.
![Open Video Guard overview](https://raw.githubusercontent.com/pbolle/openvideoguard/master/doc/ovg.png)

## Dependencies
Open Video Guard is base on Scala, Akka and Play.

## Configuration
The application can configured like all Play apps in application.conf.
In this file you will find explanations in the comments.

## Quickrun Open Video Guard
Quickrun use a embedded Datase for storing Metadata and the Filesystem for Imgages an Videos.
 
Requirements
 * Java 7 or higher

Download a snoapshot version of Open Video Guard from [here](https://drive.google.com/file/d/0B6UiLsPWpqykUGJqU2hKMTZtNW8/view?usp=sharing).
Extract the zipfile.
Create a data directory for all video and image data. This directory must be configured in openvideobuard/conf/application.conf at ovg.homeDirectory.

Start server with
```
openvideoguard-web -DapplyEvolution.default=true 
```

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