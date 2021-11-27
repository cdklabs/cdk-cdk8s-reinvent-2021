package com.myorg.springbootdeploy.k8s;

import com.myorg.springbootapp.GreetController;
import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.plus21.*;
import org.jetbrains.annotations.NotNull;
import software.constructs.Construct;

import java.util.Collections;

public class Manifest extends Chart {

  private final IngressV1Beta1 ingress;

  public Manifest(@NotNull String id, String imageUri) {
    this(new App(), id, imageUri);
  }

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

    this.ingress = service.exposeViaIngress(GreetController.PATH);
  }

  public IngressV1Beta1 getIngress() {
    return this.ingress;
  }

}
