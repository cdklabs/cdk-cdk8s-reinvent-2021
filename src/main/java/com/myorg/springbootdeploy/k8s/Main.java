package com.myorg.springbootdeploy.k8s;

import org.cdk8s.App;

public class Main {

    public static void main(final String[] args) {

        App app = new App();

        new Manifest(app,"SpringBoot");

        app.synth();
    }
}

