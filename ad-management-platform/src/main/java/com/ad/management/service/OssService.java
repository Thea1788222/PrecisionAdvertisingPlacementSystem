package com.ad.management.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface OssService {
    /**
     * 上传文件到阿里云 OSS
     *
     * @param file 文件
     * @param folder 文件夹路径（可选）
     * @return 文件在 OSS 中的访问 URL
     * @throws IOException IO 异常
     */
    String uploadFile(MultipartFile file, String folder) throws IOException;

    /**
     * 上传 InputStream 到阿里云 OSS
     *
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param folder 文件夹路径（可选）
     * @return 文件在 OSS 中的访问 URL
     */
    String uploadFile(InputStream inputStream, String fileName, String folder);
}