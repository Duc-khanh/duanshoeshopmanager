package com.codegym.shoeshopmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UploadFileService {

    private final String folderUpload = "D:\\IdeaProjects\\shoeShopManager\\src\\main\\webapp\\upload_File\\";

    public String uploadFile(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Lấy tên file gốc
            String fileName = file.getOriginalFilename();

            // Tạo file đích
            File destinationFile = new File(folderUpload + fileName);

            // Lưu file
            FileCopyUtils.copy(file.getBytes(), destinationFile);

            return fileName; // Trả về tên file đã lưu để lưu vào database nếu cần
        }
        return null;
    }
}
