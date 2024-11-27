FROM registreimatges.sic.intranet.gencat.cat/gencatcloud/java:8-alpine-3.18

ENV DATA_DIR /data

VOLUME ${DATA_DIR}

COPY maven/STD2_ComposicioCSV.war ${APP_DIR}/app.war

RUN mkdir -p ${DATA_DIR}
RUN /fix-permissions.sh ${DATA_DIR}

COPY run.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh 

CMD ["/entrypoint.sh"]