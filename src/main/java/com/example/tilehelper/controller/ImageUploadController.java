package com.example.tilehelper.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
public class ImageUploadController {

    // 图片存储目录
    private static final String IMAGE_DIR =
            System.getProperty("user.dir") + "/src/main/resources/static/images/";

    @PostMapping("/upload")
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        // 1. 原始文件名
        String originalFilename = file.getOriginalFilename();

        // 2. 生成唯一文件名
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        // 3. 创建文件
        File dest = new File(IMAGE_DIR + fileName);

        // 4. 创建目录（如果不存在）
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        // 5. 保存文件
        file.transferTo(dest);

        // 6. 返回访问路径
        return Map.of("url", "/images/" + fileName);
    }
}
