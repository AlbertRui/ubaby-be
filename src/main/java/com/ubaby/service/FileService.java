package com.ubaby.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author AlbertRui
 * @date 2018-05-07 20:19
 */
@SuppressWarnings("JavaDoc")
public interface FileService {

    /**
     * 上传文件
     *
     * @param file
     * @param path
     * @return
     */
    String upload(MultipartFile file, String path);

}
