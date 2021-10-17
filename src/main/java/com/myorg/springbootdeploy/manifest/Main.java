package com.myorg.springbootdeploy.manifest;

import com.myorg.springbootdeploy.cfn.Template;
import org.cdk8s.App;

public class Main {

    public static void main(final String[] args) {

        App app = new App();

        Template template = new Template("SpringBoot");

        new Manifest(app,"SpringBoot", template.getImageUri());

        app.synth();
    }
}

