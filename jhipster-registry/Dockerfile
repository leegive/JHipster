FROM java:openjdk-8-jdk-alpine

# Add OpenSSH
RUN apk update && apk add openssh

# add jhipster-registry source
ADD pom.xml mvnw /code/
ADD src /code/src
ADD .mvn /code/.mvn
RUN chmod +x /code/mvnw

# package the application and delete all lib
RUN cd /code/ && \
    ./mvnw package && \
    mv /code/target/*.war /jhipster-registry.war && \
    rm -Rf /root/.m2/wrapper/ && \
    rm -Rf /root/.m2/repository/

RUN sh -c 'touch /jhipster-registry.war'
EXPOSE 8761
EXPOSE 8888
VOLUME /tmp

ENV SPRING_PROFILES_ACTIVE=prod
ENV GIT_URI=git@github.com:leegive/JHipster.git
ENV GIT_SEARCH_PATHS=config

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/jhipster-registry.war","--spring.profiles.active=${SPRING_PROFILES_ACTIVE}","--spring.cloud.config.server.git.uri=${GIT_URI}","--spring.cloud.config.server.git.search-paths=${GIT_SEARCH_PATHS}"]
