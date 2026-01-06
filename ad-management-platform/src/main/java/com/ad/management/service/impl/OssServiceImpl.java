package com.ad.management.service.impl;

import com.ad.management.config.OssConfig;
import com.ad.management.service.OssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    private final OSS ossClient;
    private final OssConfig ossConfig;

    public OssServiceImpl(OSS ossClient, OssConfig ossConfig) {
        this.ossClient = ossClient;
        this.ossConfig = ossConfig;
    }

    /**
     * 上传文件到阿里云 OSS
     *
     * @param file 文件
     * @param folder 文件夹路径（可选）
     * @return 文件在 OSS 中的访问 URL
     * @throws IOException IO 异常
     */
    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 构造完整的文件路径
        String objectName = (folder != null && !folder.isEmpty() ? folder + "/" : "") + fileName;

        try {
            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    objectName,
                    file.getInputStream()
            );
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            // 返回文件访问 URL
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + objectName;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            throw new RuntimeException("文件上传失败: " + oe.getMessage());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            throw new RuntimeException("文件上传失败: " + ce.getMessage());
        }
    }

    /**
     * 上传 InputStream 到阿里云 OSS
     *
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param folder 文件夹路径（可选）
     * @return 文件在 OSS 中的访问 URL
     */
    @Override
    public String uploadFile(InputStream inputStream, String fileName, String folder) {
        // 构造完整的文件路径
        String objectName = (folder != null && !folder.isEmpty() ? folder + "/" : "") + fileName;

        try {
            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    objectName,
                    inputStream
            );
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            // 返回文件访问 URL
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + objectName;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            throw new RuntimeException("文件上传失败: " + oe.getMessage());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            throw new RuntimeException("文件上传失败: " + ce.getMessage());
        }
    }
}