package com.ling.lingaicodegeneration.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ProjectDownloadService {

    /**
     * Download project source code as ZIP file
     *
     * @param projectPath      path to the project directory
     * @param downloadFileName name of the ZIP file to download
     * @param response         HTTP response
     */
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}