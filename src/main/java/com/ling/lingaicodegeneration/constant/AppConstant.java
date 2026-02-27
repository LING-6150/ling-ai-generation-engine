package com.ling.lingaicodegeneration.constant;

public interface AppConstant {

    /**
     * Featured app priority
     */
    Integer GOOD_APP_PRIORITY = 99;

    /**
     * Default app priority
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * Code output root directory
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * Code deploy root directory
     */
    String CODE_DEPLOY_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_deploy";

    /**
     * App deploy host
     */
    String CODE_DEPLOY_HOST = "http://localhost";
}