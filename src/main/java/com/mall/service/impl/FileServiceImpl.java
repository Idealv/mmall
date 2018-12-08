package com.mall.service.impl;

import com.google.common.collect.Lists;
import com.mall.service.IFileService;
import com.mall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
    public String upload(MultipartFile file,String path){
        String filename = file.getOriginalFilename();
        String fileExtentionName = filename.substring(filename.lastIndexOf(".") + 1);
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtentionName;
        logger.info("文件上传开始,上传文件名:{},上传路径:{},新文件名:{}", filename, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件发生异常", e);
            return null;
        }
        return targetFile.getName();
    }
}
