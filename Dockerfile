FROM alpine

ENV DATA_DIR /data

VOLUME ${DATA_DIR}

COPY ../target/STD2_ComposicioCSV.war ${APP_DIR}/app.war

RUN mkdir -p ${DATA_DIR}

COPY run.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh 

CMD ["/entrypoint.sh"]