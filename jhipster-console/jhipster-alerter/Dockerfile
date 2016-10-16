FROM python:2.7-wheezy

RUN apt-get update
RUN apt-get -y install curl

ENV SET_CONTAINER_TIMEZONE false
ENV ELASTALERT_VERSION 0.0.95
ENV CONTAINER_TIMEZONE America/Los_Angeles
ENV ELASTALERT_URL https://github.com/Yelp/elastalert/archive/v${ELASTALERT_VERSION}.tar.gz

ENV ELASTALERT_HOME /opt/elastalert
ENV RULES_DIRECTORY /opt/elastalert/rules

ENV ELASTICSEARCH_HOST http://jhipster-elasticsearch
ENV ELASTICSEARCH_PORT 9200
ENV ELASTICSEARCH_USERNAME ""
ENV ELASTICSEARCH_PASSWORD ""


RUN mkdir -p /opt/elastalert

RUN curl -Lo elastalert.tar.gz ${ELASTALERT_URL} && \
    tar xvf *.tar.gz -C ${ELASTALERT_HOME} --strip-components 1 && \
    rm *.tar.gz

WORKDIR /opt/elastalert

RUN python setup.py install && \
    pip install -e .

COPY ./start-elastalert.sh /opt/start-elastalert.sh
RUN chmod +x /opt/start-elastalert.sh
#RUN sed -i -e 's/\r$//' /opt/start-elastalert.sh

COPY config.yaml /opt/elastalert/
RUN mkdir /opt/elastalert/rules
COPY rules/* rules/

VOLUME ["/opt/elastalert/"]
VOLUME ["/opt/elastalert/rules"]

# Launch Elastalert when a container is started.
CMD ["/opt/start-elastalert.sh"]
