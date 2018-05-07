package com.ubaby.service.impl;

import com.google.common.collect.Lists;
import com.ubaby.service.FileService;
import com.ubaby.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author AlbertRui
 * @date 2018-05-07 20:20
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@Service("fileService")
public class FileServiceImpl implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {

        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件，上传的文件名：{}，上传的路径：{}，新文件名：{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);
        try {
            file.transferTo(targetFile);
            boolean flag = FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            if (flag)
                logger.info("文件上传成功");
            targetFile.delete();
        } catch (IOException e) {
            logger.error("file upload error", e);
            return null;
        }

        return targetFile.getName();

    }

}
