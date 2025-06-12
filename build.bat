@echo off
set GRADLE_OPTS=-Dorg.gradle.internal.http.socketTimeout=120000 -Dorg.gradle.internal.http.connectionTimeout=120000
set JAVA_OPTS=-Xmx4g -XX:MaxMetaspaceSize=1g
gradlew.bat clean assembleDebug --refresh-dependencies --no-daemon --stacktrace 