package cn.fulgens.mmall.common.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpServerIp = PropertiesUtil.getProperty("ftp.server.ip");

    private static int ftpServerPort = Integer.valueOf(PropertiesUtil.getProperty("ftp.server.port"));

    private static String ftpServerUsername = PropertiesUtil.getProperty("ftp.server.username");

    private static String ftpServerPassword = PropertiesUtil.getProperty("ftp.server.password");

    private FTPClient ftpClient;

    private String hostname;

    private int port;

    private String username;

    private String password;

    public FTPUtil(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 连接FTP服务器
     * @param hostname  FTP服务器ip地址
     * @param port      FTP服务器端口
     * @param username  FTP服务器登录用户名
     * @param password  FTP服务器登录密码
     * @return  连接成功返回true，否则返回false
     */
    private boolean connectFtpServer(String hostname, int port, String username, String password) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(hostname, port);
            isSuccess = ftpClient.login(username, password);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    /**
     * 判断是否已经成功连接到FTP服务器
     * @return  成功连接返回true，否则返回false
     */
    private boolean isConnected() {
        return connectFtpServer(this.hostname, this.port, this.username, this.password);
    }

    /**
     * 创建并切换到FTP服务器上的目录
     * @param remotePath    FTP服务器上的目录，如path1/path2
     * @return  成功创建并切换返回true
     */
    private boolean mkDirsAndCd(String remotePath) {
        if (!isConnected()) {
            return false;
        }
        // 若路径以/开头,去除起始的/
        if (remotePath.indexOf("/") == 0) {
            remotePath = remotePath.substring(1, remotePath.length());
        }
        // 路径以/结尾,去除末尾的/
        if (remotePath.lastIndexOf("/") == remotePath.length() - 1) {
            remotePath = remotePath.substring(0, remotePath.length()-1);
        }
        try {
            if (remotePath.indexOf("/") == -1) {
                // 只有一层目录
                ftpClient.makeDirectory(remotePath);
                ftpClient.changeWorkingDirectory(remotePath);
            }else {
                // 多级目录，循环创建
                String[] paths = remotePath.split("/");
                for (String path : paths) {
                    ftpClient.makeDirectory(path);
                    ftpClient.changeWorkingDirectory(path);
                }
            }
        }catch (IOException e) {
            logger.error("创建ftp服务器目录异常", e);
        }
        return true;
    }

    private boolean uploadFile(String remotePath, String remoteFileName, File file) {
        boolean uploaded = false;
        FileInputStream fis = null;
        //连接FTP服务器
        if(isConnected()){
            try {
                // 切换工作目录
                if (!ftpClient.changeWorkingDirectory(remotePath)) {
                    mkDirsAndCd(remotePath);
                }
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
                // 开启被动模式
                ftpClient.enterLocalPassiveMode();
                fis = new FileInputStream(file);
                uploaded = ftpClient.storeFile(remoteFileName, fis);
            } catch (IOException e) {
                logger.error("上传文件到ftp服务器异常",e);
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("关闭文件输入流异常", e);
                    e.printStackTrace();
                }
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error("与ftp服务器断开连接异常", e);
                    e.printStackTrace();
                }
            }
        }
        return uploaded;
    }

    public static boolean uploadFile(String remotePath, File file) {
        FTPUtil ftpUtil = new FTPUtil(ftpServerIp, ftpServerPort, ftpServerUsername, ftpServerPassword);
        logger.info("开始连接ftp服务器,准备上传文件");
        boolean uploaded = ftpUtil.uploadFile(remotePath, file.getName(), file);
        logger.info("结束上传,上传结果: {}", uploaded == true ? "上传成功" : "上传失败");
        return uploaded;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
