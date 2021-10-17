package com.myorg.springbootdeploy.manifest;

import com.myorg.springbootapp.GreetController;
import org.cdk8s.App;
import org.cdk8s.Chart;
import org.cdk8s.plus21.*;
import org.jetbrains.annotations.NotNull;
import software.constructs.Construct;

import java.util.Collections;

public class Manifest extends Chart {

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

    // the ingress path must match the controller path
    // because url rewrites are not supported in ALB
    // see https://github.com/kubernetes-sigs/aws-load-balancer-controller/issues/835
    IngressV1Beta1 ingress = service.exposeViaIngress(GreetController.PATH);
    ingress.getMetadata().addAnnotation("kubernetes.io/ingress.class", "alb");
    ingress.getMetadata().addAnnotation("alb.ingress.kubernetes.io/scheme", "internet-facing");
  }

}
