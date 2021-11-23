install:
	mvn clean install

run:
	mvn spring-boot:run

jar:
	mvn package
	cp target/cdk-cdk8s-reinvent-2021-0.1.jar image/

stack:
	cdk synth

deploy: jar manifest
	cdk deploy

destroy:
	cdk destroy

manifest:
	cdk8s synth

image: jar
	docker build -t springboot-example image/
	aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 286171437199.dkr.ecr.us-east-1.amazonaws.com
	docker tag springboot-example 286171437199.dkr.ecr.us-east-1.amazonaws.com/springboot-example
	docker push 286171437199.dkr.ecr.us-east-1.amazonaws.com/springboot-example

container: image
	docker run --name springboot-example -p 8080\:8080 -d springboot-example

kill:
	docker rm -vf springboot-example
