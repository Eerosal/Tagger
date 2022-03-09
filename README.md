

https://user-images.githubusercontent.com/28145295/157330753-75e67552-bb2f-484b-83ad-e4eeea0107bd.mp4


# Tagger
Simple web application for managing a collection of files. Work in progress.

Core functionality includes
* File upload
* File tagging
* Searching files that contain given tags

## Technical details

Files and thumbnails are stored in Minio (self-hostable S3 compatible object storage). File metadata, tags and users are stored in SQL database.

Some of the libraries/tools used in frontend (TypeScript)
* React
* Axios
* ESLint

Backend (Java)
* Spring Boot
* OrmLite
* Lombok
* Minio client
* Gradle
* CheckStyle

## How to run
Please don't do it. I'll commit the existing Docker files when everything's ready enough, maybe then?

## Disclaimer
The application (Tagger) is provided AS IS without any kind of warranty.

The maintainer(s) of this repository are not responsible for any damages caused by the application. 
