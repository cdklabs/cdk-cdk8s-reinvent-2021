package com.myorg.springbootdeploy.k8s;

import com.myorg.springbootapp.GreetController;
import org.cdk8s.Chart;
import org.cdk8s.plus21.*;
import org.jetbrains.annotations.NotNull;
import software.constructs.Construct;

import java.util.Collections;

public class Manifest extends Chart {

  public Manifest(@NotNull Construct scope, @NotNull String id) {
    super(scope, id);

    Deployment deployment = new Deployment(this, "Deployment", DeploymentProps.builder()
            .containers(Collections.singletonList(ContainerProps.builder()
                    .image("public.ecr.aws/g5q9k7j5/springboot-example")
                    .port(8080)
                    .build()))
            .build());

    deployment.exposeViaIngress(GreetController.PATH);
  }

}