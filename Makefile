.PHONY: install run jar manifest image container kill

install:
	mvn clean install

run:
	mvn spring-boot:run

jar:
	mvn package
