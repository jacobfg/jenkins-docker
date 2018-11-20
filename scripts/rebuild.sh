#!/bin/bash -eu

docker-compose stop
docker-compose rm -f
rm -rf jenkins_home/
docker build -t local/jenkins:lts .
docker-compose up -d
