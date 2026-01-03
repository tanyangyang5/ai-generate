package com.example.aigenerate.util;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

import java.io.InputStream;

public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

   // @Override
    public MediaType getContentType() {
        return MediaType.IMAGE_JPEG; // 可根据扩展名动态判断
    }
}