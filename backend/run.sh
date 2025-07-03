#!/bin/bash

echo "[ Spring Boot Application restart ]"

cd ~/deploy || exit 1

echo "[ Stop Application ]"
pkill -f 'java -jar' || true

echo "[ Start Application ]"
nohup env $(cat .env | xargs) java -jar backend-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

echo "[Success, Check log: tail -f ~/deploy/app.log]"