install:
	mvn clean install

run:
	mvn spring-boot:run

jar:
	mvn package
	cp target/cdk-cdk8s-reinvent-2021-0.1.jar image/

manifest:
	cdk8s synth

image: jar
	docker build -t reinvent image/

container: image
	docker run --name reinvent -p 8080\:8080 -d reinvent

kill:
	docker rm -vf reinvent
