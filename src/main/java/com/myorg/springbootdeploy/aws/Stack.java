package com.myorg.springbootdeploy.aws;

import com.myorg.springbootdeploy.k8s.Manifest;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.services.ecr.assets.DockerImageAsset;
import software.amazon.awscdk.services.ecr.assets.DockerImageAssetProps;
import software.amazon.awscdk.services.eks.*;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.Role;
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

        IRole admin = Role.fromRoleArn(this, "AdminRole", "arn:aws:iam::286171437199:role/Admin");
        cluster.getAwsAuth().addMastersRole(admin);

        new ALBController(this, "ALBController", cluster);

        Nodegroup nodeGroup = cluster.getDefaultNodegroup();

        if (nodeGroup != null) {
            image.getRepository().grantPull(cluster.getDefaultNodegroup().getRole());
        }

        Manifest manifest = new Manifest("SpringBoot", image.getImageUri());
        cluster.addCdk8sChart("manifest", manifest);

        KubernetesObjectValue lbAddress = new KubernetesObjectValue(this, "LBAddress", KubernetesObjectValueProps.builder()
                .cluster(cluster)
                .objectType("ingress")
                .objectName(manifest.getIngress().getName())
                .jsonPath(".status.loadBalancer.ingress[0].hostname")
                .build());

        new CfnOutput(this, "LBAddressValue", CfnOutputProps.builder()
                .value(lbAddress.getValue() + "/greeting")
                .build());

    }

}
