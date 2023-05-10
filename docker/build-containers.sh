#!/bin/bash

docker build -t tracing/sleuth-legacy-tracing ../sleuth-legacy-tracing
docker build -t tracing/micrometer-tracing ../micrometer-tracing
docker build -t tracing/authorization-server ../authorization-server
docker build -t tracing/resource-server ../resource-server
docker build -t tracing/client-application ../client-application

