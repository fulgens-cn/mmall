package cn.fulgens.mmall.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    /**
     * 文件上传
     * @param file  上传文件
     * @param path  上传路径
     * @return  上传成功返回文件名，否则返回null
     */
    String uploadFile(MultipartFile file, String localPath);

}
