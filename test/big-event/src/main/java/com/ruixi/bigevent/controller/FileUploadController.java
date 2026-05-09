package com.ruixi.bigevent.controller;

import com.ruixi.bigevent.pojo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        //把文件内容存储到本地磁盘中
        String originalFilename = file.getOriginalFilename();
        //保证文件名唯一
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        file.transferTo(new File("D:\\IdeaProjects\\big-event\\files\\" + fileName));
        return Result.success("url访问地址");
    }
}
