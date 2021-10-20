package com.myorg.springbootdeploy.aws;

import software.amazon.awscdk.core.App;

public class Main {

    public static void main(final String[] args) {

        App app = new App();

        new Stack(app, "SpringBoot");

        app.synth();
    }
}

