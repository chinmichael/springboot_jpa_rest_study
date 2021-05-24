package com.springboot_ex.jpa_test.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@AllArgsConstructor
public class UploadResultDTO {
    
    private String fileName;
    private String uuid;
    private String folderPath;
    
    // 프론트에서 전체경로가 필요할 경우 인코딩처리(경로 + uuid + 파일이름) 하여 반환하는 메서드
    public String getImageURL() {
        try {
            return URLEncoder.encode(folderPath + "\\" + uuid + "_" + fileName, "UTF-8");
            
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return "";
    }
}
