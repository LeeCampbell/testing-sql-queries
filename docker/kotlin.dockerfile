FROM gradle

FROM gradle as builder
COPY ./ /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build --info