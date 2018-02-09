package cn.fulgens.mmall.service.impl;

import cn.fulgens.mmall.service.IFileService;
import cn.fulgens.mmall.utils.FTPUtil;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements IFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file, String path) {
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件扩展名
        String extension = FilenameUtils.getExtension(originalFilename);
        // 指定新文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") +
                "." + extension;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            // 本地文件暂存目录不存在则创建
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path + "/" +fileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            logger.error("文件上传出错", e);
            return null;
        }
        // 将文件上传到ftp服务器
        boolean isSuccess = FTPUtil.uploadFile("img", targetFile);
        if (!isSuccess) {
            return null;
        }
        // 删除本地文件暂存目录中的文件
        targetFile.delete();
        return targetFile.getName();
    }

}
