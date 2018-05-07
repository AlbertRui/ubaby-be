package com.ubaby.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author AlbertRui
 * @date 2018-05-07 20:57
 */
public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static final String FTP_USER = PropertiesUtil.getProperty("ftp.user");

    private static final String FTP_IP = PropertiesUtil.getProperty("ftp.server.ip");

    private static final int FTP_PORT = 21;

    private static final String FTP_PASS = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    private FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> files) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(FTP_IP, FTP_PORT, FTP_USER, FTP_PASS);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img/ubaby", files);
        logger.info("结束上传，上传结果：{}", result);
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> files) throws IOException {

        boolean uploaded = true;

        FileInputStream fileInputStream = null;
        if (connectServer(ip, user, pwd)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();

                for (File file : files) {
                    fileInputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fileInputStream);
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("上传文件异常", e);
            } finally {
                if (fileInputStream != null)
                    fileInputStream.close();
                ftpClient.disconnect();
            }

        }

        return uploaded;

    }

    private boolean connectServer(String ip, String user, String pwd) {

        boolean isSuccess = false;

        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, pwd);
        } catch (IOException e) {
            logger.error("ftp服务器异常", e);
        }

        return isSuccess;

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
