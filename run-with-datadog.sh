#!/bin/bash

# 로컬 Datadog Agent로 trace 전송 (기본: localhost:8126)
java -javaagent:dd-java-agent.jar \
  -Ddd.service=kopring-poc \
  -Ddd.env=local \
  -Ddd.version=1.0.0 \
  -Ddd.logs.injection=true \
  -Ddd.profiling.enabled=false \
  -jar build/libs/kopring-0.0.1-SNAPSHOT.jar
