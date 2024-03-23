FROM gradle

FROM gradle as builder
COPY ./ /home/gradle/project
WORKDIR /home/gradle/project
# Build all the code (including tests), but do not run the tests yet.
RUN gradle build testClasses -x test --info