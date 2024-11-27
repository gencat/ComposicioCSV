cd ../STD2-documents4j-transformer-msoffice-excel
mvn clean install
cd ../STD2-documents4j-transformer-msoffice-power-point
mvn clean install
cd ../STD2-documents4j-transformer-msoffice-word
mvn clean install
cd ../STD2-documents4j-server-standalone
mvn clean install
cd ../STD2-core
mvn clean install
cd ../STD2-ComposicioCSV/
mvn clean install -Dmaven.test.skip=true
cd ../STD2-ComposicioCSV/target/STD2_ComposicioCSV-docker-compose/app
# cp -R /data_std_copy_docker ./data
docker build -t gencatcloud/java/std2-composicio_csv .
docker stop std2-composicio_csv || true && docker rm std2-composicio_csv || true && docker run -d --name std2-composicio_csv -v /tmp:/tmp -v /data_std_copy_docker:/data -p 8082:8080 gencatcloud/java/std2-composicio_csv && docker logs -f std2-composicio_csv