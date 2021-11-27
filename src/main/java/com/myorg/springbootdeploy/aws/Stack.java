package com.myorg.springbootdeploy.aws;

import com.myorg.springbootdeploy.k8s.Manifest;
import org.cdk8s.App;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
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
                .albController(AlbControllerOptions.builder()
                        .version(AlbControllerVersion.V2_3_0)
                        .build())
                .build());

        Nodegroup nodeGroup = cluster.getDefaultNodegroup();

        if (nodeGroup != null) {
            image.getRepository().grantPull(cluster.getDefaultNodegroup().getRole());
        }

        Manifest manifest = new Manifest(new App(), "SpringBoot", image.getImageUri());
        cluster.addCdk8sChart("manifest", manifest, KubernetesManifestOptions.builder()
                .ingressAlb(true)
                .ingressAlbScheme(AlbScheme.INTERNET_FACING)
                .build());

        String lbAddress = cluster.getIngressLoadBalancerAddress(manifest.getIngress().getName());

        new CfnOutput(this, "LBAddressValue", CfnOutputProps.builder()
                .value(lbAddress + "/greeting")
                .build());

    }

}
