package com.myorg.springbootdeploy.manifest;

import com.myorg.springbootapp.GreetController;
import org.cdk8s.Chart;
import org.cdk8s.plus21.*;
import org.jetbrains.annotations.NotNull;
import software.constructs.Construct;

import java.util.Collections;

public class Manifest extends Chart {

  public Manifest(@NotNull Construct scope, @NotNull String id, String imageUri) {
    super(scope, id);

    Deployment deployment = new Deployment(this, "Deployment", DeploymentProps.builder()
            .containers(Collections.singletonList(ContainerProps.builder()
                    .image(imageUri)
                    .port(8080)
                    .build()))
            .build());

    Service service = deployment.exposeViaService(ExposeDeploymentViaServiceOptions.builder()
                    .serviceType(ServiceType.NODE_PORT)
            .build());

    service.exposeViaIngress(GreetController.PATH);
  }

}
