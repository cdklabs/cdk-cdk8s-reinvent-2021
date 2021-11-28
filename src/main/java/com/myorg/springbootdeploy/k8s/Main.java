package com.myorg.springbootdeploy.k8s;

import org.cdk8s.App;

public class Main {

    public static void main(final String[] args) {

        App app = new App();

        new Manifest(app,"SpringBoot", "286171437199.dkr.ecr.us-east-1.amazonaws.com/springboot-example");

        app.synth();
    }
}

