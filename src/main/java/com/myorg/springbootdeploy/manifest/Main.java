package com.myorg.springbootdeploy.manifest;

import org.cdk8s.App;

public class Main {

    public static void main(final String[] args) {

        App app = new App();

        new Manifest(app,"SpringBoot", "PLACEHOLDER");

        app.synth();
    }
}

