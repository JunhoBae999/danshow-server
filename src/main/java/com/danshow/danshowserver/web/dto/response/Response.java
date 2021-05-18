package com.danshow.danshowserver.web.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    private int order;

    private File file;

    public Response(int i, File fileA) {
        this.order = i;
        this.file = fileA;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
