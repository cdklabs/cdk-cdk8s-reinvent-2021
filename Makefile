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
	docker build -t springboot-example image/
	aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws
	docker tag springboot-example public.ecr.aws/z5f9g9k5/springboot-example
	docker push public.ecr.aws/z5f9g9k5/springboot-example

container: image
	docker run --name springboot-example -p 8080\:8080 -d springboot-example

kill:
	docker rm -vf springboot-example
