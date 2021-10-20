package com.myorg.springbootdeploy.aws;

import com.myorg.springbootdeploy.k8s.Manifest;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.services.ecr.assets.DockerImageAsset;
import software.amazon.awscdk.services.ecr.assets.DockerImageAssetProps;
import software.amazon.awscdk.services.eks.*;
import software.constructs.Construct;


public class Stack extends software.amazon.awscdk.core.Stack {

    public Stack(@NotNull Construct scope, @NotNull String id) {
        super(scope, id);

        DockerImageAsset image = new DockerImageAsset(this, "Image", DockerImageAssetProps.builder()
                .directory("image")
                .build());

        Cluster cluster = new Cluster(this, "Cluster", ClusterProps.builder()
                .version(KubernetesVersion.V1_21)
                .build());

        new ALBController(this, "ALBController", cluster);

        Nodegroup nodeGroup = cluster.getDefaultNodegroup();

        if (nodeGroup != null) {
            image.getRepository().grantPull(cluster.getDefaultNodegroup().getRole());
        }

        cluster.addCdk8sChart("manifest", new Manifest("SpringBoot", image.getImageUri()));

    }

}
