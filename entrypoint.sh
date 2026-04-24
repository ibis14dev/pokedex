#!/bin/sh

JAVA_OPTS="$APP_VM_ARGS"

if [ ! -z "$SPRING_PROFILES_ACTIVE" ]; then
  JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE"
fi

exec java $JAVA_OPTS -jar "$APP_JAR"