package com.myorg.springbootdeploy.cfn;

import com.myorg.springbootdeploy.manifest.Manifest;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecr.assets.DockerImageAsset;
import software.amazon.awscdk.services.ecr.assets.DockerImageAssetProps;
import software.amazon.awscdk.services.eks.*;
import software.constructs.Construct;

public class Template extends Stack {

    private final DockerImageAsset image;

    public Template(@NotNull String id) {
        this(new App(), id);
    }

    public Template(@NotNull Construct scope, @NotNull String id) {
        super(scope, id, StackProps.builder()
                .env(Environment.builder()
                        .account("185706627232")
                        .region("us-east-1")
                        .build())
                .build());

        this.image = new DockerImageAsset(this, "Image", DockerImageAssetProps.builder()
                .directory("image")
                .build());

        Vpc vpc = new Vpc(this, "Vpc");

        Cluster cluster = new Cluster(this, "Cluster", ClusterProps.builder()
                .version(KubernetesVersion.V1_21)
                .vpc(vpc)
                .build());

        new ALBController(this, "ALBController", cluster);

        Nodegroup nodeGroup = cluster.getDefaultNodegroup();

        if (nodeGroup != null) {
            this.image.getRepository().grantPull(cluster.getDefaultNodegroup().getRole());
        }

        cluster.addCdk8sChart("manifest", new Manifest("Manifest", this.getImageUri()));

    }

    public String getImageUri() {
        return this.image.getImageUri();
    }

}
