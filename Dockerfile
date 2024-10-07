FROM elasticsearch:8.15.2

COPY ./build/distributions/elasticsearch-ubi-1.0.0-SNAPSHOT.zip /tmp/

RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install --batch file:/tmp/elasticsearch-ubi-1.0.0-SNAPSHOT.zip

