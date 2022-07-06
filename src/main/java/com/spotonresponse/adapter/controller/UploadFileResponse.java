package com.spotonresponse.adapter.controller;

public class UploadFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileDownloadUri() {
        return this.fileDownloadUri;
    }

    public String getFileType() {
        return this.fileType;
    }

    public long getSize() {
        return this.size;
    }
}
