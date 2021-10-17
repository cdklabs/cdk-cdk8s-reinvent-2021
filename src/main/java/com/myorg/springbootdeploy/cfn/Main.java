package com.myorg.springbootdeploy.cfn;

import software.amazon.awscdk.core.App;

public class Main {

    public static void main(final String[] args) {

        App app = new App();

        new Template(app, "SpringBoot");

        app.synth();
    }
}

