package com.myorg.springbootdeploy.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.eks.*;
import software.amazon.awscdk.services.iam.PolicyStatement;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.2/deploy/installation/
public class ALBController extends Construct {

    public ALBController(@NotNull software.constructs.Construct scope, @NotNull String id, Cluster cluster) {
        super(scope, id);

        String namespace = "kube-system";
        ServiceAccount serviceAccount = cluster.addServiceAccount("alb-account", ServiceAccountOptions.builder()
                        .namespace(namespace)
                        .name("aws-load-balancer-controller")
                .build());

        try {
            ObjectMapper mapper = new ObjectMapper();
            // https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.2.0/docs/install/iam_policy.json
            Map<?, ?> policy = mapper.readValue(Paths.get("src/main/resources/alb/iam_policy.json").toFile(), Map.class);
            List<Map<String, Object>> statements = (List<Map<String, Object>>)policy.get("Statement");
            for (Map<String, Object> s : statements) {
                serviceAccount.addToPrincipalPolicy(PolicyStatement.fromJson(s));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HelmChart chart = cluster.addHelmChart("Chart", HelmChartOptions.builder()
                        .chart("aws-load-balancer-controller")
                        .repository("https://aws.github.io/eks-charts")
                        .namespace(namespace)
                        .release("aws-load-balancer-controller")
                        .version("1.2.0")
                        .wait(true)
                        .timeout(Duration.minutes(15))
                        .values(new HashMap<String, Object>() {{
                            put("clusterName", cluster.getClusterName());
                            put("serviceAccount", new HashMap<String, Object>() {{
                                put("create", false);
                                put("name", serviceAccount.getServiceAccountName());
                            }});
                            put("region", Stack.of(cluster).getRegion());
                            put("vpcId", cluster.getVpc().getVpcId());
                        }})
                .build());

        if (cluster.getDefaultNodegroup() != null) {
            chart.getNode().addDependency(cluster.getDefaultNodegroup());
        }

        chart.getNode().addDependency(serviceAccount);
        chart.getNode().addDependency(cluster.getOpenIdConnectProvider());
        chart.getNode().addDependency(cluster.getAwsAuth());
    }
}
