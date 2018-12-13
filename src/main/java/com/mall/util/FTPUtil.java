package com.mall.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.derby.iapi.services.io.LimitInputStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {
    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getPropery("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getPropery("ftp.user");
    private static String ftpPass = PropertiesUtil.getPropery("ftp.pass");

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

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img", fileList);
        logger.info("结束上传,上传结果:{}", result);
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        if (connectServer(ip, user, pwd)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setBufferSize(1024);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem :
                        fileList) {
                    fis = new FileInputStream(fileItem);
//                    logger.info("fileItem name: "+fileItem.getName());
                    ftpClient.storeFile(fileItem.getName(), fis);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ftpClient.disconnect();
                fis.close();
            }
        }
        return true;
    }

    public boolean connectServer(String ip, String user, String pwd) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, pwd);
        } catch (IOException e) {
            logger.error("ftp服务器连接失败" + e);
        }
        return isSuccess;
    }

    public static void main(String[] args) {
        FTPClient ftpClient = new FTPClient();
        boolean isSuccess=false;
        try {
            ftpClient.connect("172.19.56.86");
            isSuccess=ftpClient.login("ideal","ideal");
            System.out.println("isSuccess = " + isSuccess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
