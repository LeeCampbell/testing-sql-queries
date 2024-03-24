FROM gradle

FROM gradle as builder

# Copy sources that dont change much but contribute to the slowest part of the build.
#   This will allow slow downloads to be cached as a docker layer
COPY ./build.gradle.kts     /home/gradle/project/
COPY ./gradle.properties    /home/gradle/project/
COPY ./gradlew              /home/gradle/project/
COPY ./gradlew.bat          /home/gradle/project/
COPY ./settings.gradle.kts  /home/gradle/project/

WORKDIR /home/gradle/project
# Build all the code (including tests), but do not run the tests yet.
RUN gradle build --info

COPY ./ /home/gradle/project
WORKDIR /home/gradle/project
# Build all the code (including tests), but do not run the tests yet.
RUN gradle build testClasses -x test --info