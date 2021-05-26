package com.springboot_ex.jpa_test.controller;

import com.springboot_ex.jpa_test.dto.UploadResultDTO;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*  5/24 : 파일 업로드

    스프링부트 파일업로드 설정은 둘 중 하나 이용
    1. 기존에도 잘 쓰던 별도의 파일업로드 라이브러리 commnos-fileupload
    2. Servlet부터 추가된 자체적인 파일 업로드 라이브러리

    WAS버전이 낮거나 WAS를 쓰지 않는 경우는 1
    공부하는 책에서는 2를 사용 >> 내장 Tomcat을 사용하는 경우 application.properties 파일 수정만 들어가면 된다

    spring.servlet.multipart.enabled=true >> 파일 업로드 가능 여부
    spring.servlet.multipart.location=C:\\Users\\chinm\\Desktop\\jpa_upload_test >> 업로드 파일 임시저장경로
    spring.servlet.multipart.max-request-size=30MB >> 한번에 최대 업로드 가능 용량
    spring.servlet.multipart.max-file-size=10MB >> 파일 하나의 최대 크기

    보통 프론트에서 요청은 Ajax로 서버에서 반환처리는 JSON으로 처리 (이제 진짜 Ajaj가 아닐까... Asychronous Javascript And Xml이 아니라 JSON이여,,)


    업로드 파일 저장
    1. 스프링 자체 제공하는 FileCopyUtils
    2. MultipartFile 자체의 transferTo()
 */

@RestController
@Log4j2
public class UploadController {
    
    @Value("${com.springboot_ex.jpa_test.upload.path}")
    private String uploadPath;
    // application.properties에 저장한 속성의 값을 사용하기 위한 어노테이션
    // import는 beans factory의 것

    @PostMapping("/uploadAjax")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) { // 배열로 하여 동시에 여러 파일을 받을 수 있음
        
        List<UploadResultDTO> resultDTOList = new ArrayList<>(); // 순차저장 및 읽을 것이므로
        
        for(MultipartFile uploadFile : uploadFiles) {
            
            // 이미지 파일 확장자 검사
            if(uploadFile.getContentType().startsWith("image") == false) {
                log.warn("this file is not image type");

                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden 표시
            }

            String originalName = uploadFile.getOriginalFilename();
            // IE나 Edge 브라우저 환경의 경우 전체 경로가 들어온다
            String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
            
            log.info("fileName : " + fileName);

            //날짜 따른 폴더경로 생성
            String folderPath = makeFolder();

            //파일 이름 고유화
            String uuid = UUID.randomUUID().toString();

            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

            Path savePath = Paths.get(saveName);

            try {
                uploadFile.transferTo(savePath); // 경로 객체를 통해 업로드된 파일 저장

                // 섬네일 생성 파일 이름 앞에 s_를 붙인다
                String thumbnailSaveName = uploadPath + File.separator + folderPath + File.separator
                                            + "s_" + uuid + "_" + fileName;

                File thumbnailFile = new File(thumbnailSaveName);

                // 라이브러리로 섬네일 생성 savePath경로로 파일을 생성하며 (savePath.toFile()) 지정한 파일과 크기로 생성
                Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);

                resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
                
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(resultDTOList, HttpStatus.OK); // ResponseEntity를 이용해 JSON데이터와 HTTPStatus 반환
    }
    
    //날짜 기준으로 폴더경로를 생성
    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        
        String folderPath = str.replace("/", File.separator);
        
        File uploadPathFolder = new File(uploadPath, folderPath);
        //@Value로 가져온 경로에 폴더를 만듦
        
        if(uploadPathFolder.exists() == false) {
            uploadPathFolder.mkdirs();
            //기존 경로 같은 폴더 파일이 없을 때만 mkdirs()로 위 폴더들을 생성
        }
        
        return folderPath;
    }
    
    /*  파일 저장시 일반적으로 고려할 사항
    
        1. 동일이름 파일 문제 (기존 이름에 덮어 씌워짐)
        >> 1)시간 값을 파일 이름에 추가하거나
        >> 2)UUID를 이용
        >> 이름에 추가하여 고유한 이름을 생성함
        
        2. 폴더 용량
        하나의 폴더에 파일이 너무 쌓이면 성능 저하 
        그리고 운영체제에 따라 한 폴더에 넣을 수 있는 파일에도 제한이 있음 (FAT32방식은 65,534개)
        >> 저장시점의 년/월/일로 나눠 저장 (나는 월까지로)
        
        3. 파일 확장자 체크
        첨부파일을 이용하여 쉘 스크립트 파일 등을 업로드 해 공격하는 케이스 등이 존재
        파일 업로드 순간 / 서버에 파일 저장하는 순간 >> 검사하는 과정 필요
        MultipartFile은 getContentType()을 이용하여 체크할 수 있음(MIME 타입 String 반환)

        MIME type
        image/png, image/git, image/jpeg 등
        폼 태그에서 업로드시 multipart/form-data
        text/html, text/javascript text/xml 등
    
     */

    /*
        MIME (Multipurpose Internet Mail Extension)
        이메일과 함께 동봉할 파일을 텍스트로 전환해 이메일 시스템을 통해 전달하기 위해 개발

        클라이언트에게 전송된 문서의 다양성을 알려주기 위한 메커니즘

        음악, 영상, 워드, 이미지 등의 바이너리 파일들을 전송하기 위한 텍스트 파일로의 인코딩 및 역의 디코딩을 하게 됨
        MIME로 인코딩한 파일은 파일 앞에 Content-Type 정보를 담게 됨 (브라우저에 따라 지원 가능 타입이 달라진다)

        type(text, image, audio, video, application)/subtype으로 이뤄짐
        multipart/form-data나 multipart/byteranges 는 위 여러 MIME타입들이 개별적으로 여러 파트를 이루는 합성타입
        multipart/form-data은 폼을 통해 서버로 데이터 전송시 사용 각 파트마다 각 객체로 HTTP 헤더 가짐

        Content-Type
        클라이언트에서 어떤 파일,자원을 받을 때 웹서버는 일련의 HTTP 헤더로 파일이나 자원을 포함하는 바이트의 스트림을 보낸다.

        이 헤더에는 이밖에 웹서버 소프트웨어타입, 서버시간, HTTP프로토콜, 도메인에 대한 쿠키 등이 포함되며
        그중에는 당연 자원의 content type이 포함됨 >> 이를 통해 어떤 파일의 stream인지 알 수 있음
        (이 헤더로 지정되는 값의 표준은 MIME-type)
     */

    /*  5/25 : 섬네일 이미지 생성

        원본 이미지가 평상시에도 화면에 바로 나오면 데이터를 많이 소비하기에
        가능한 섬네일을 만들어 전송해주고 필요할 때 원본을 호출하는 것이 좋음

        섬네일 처리는 java.imageio패키지를 이용하거나 전용 라이브리를 사용
        >> 라이브러리가 코드절감 + 비율조정이 편하다

     */

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName) {
        //rest api상 위와 같은 ?커맨드 url은 올바르지 않으나 특수문자 문제로 올바르게 보내지지 않으므로 일단 사용

        ResponseEntity<byte[]> result = null;

        try {
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");

            log.info("fileName : " + srcFileName);

            //위에서 업로딩시 uploadPath는 DTO에 저장하지 않았었다.
            File file = new File(uploadPath + File.separator + srcFileName);

            log.info("file : " + file);

            //Cotent-type이 저장되는 곳이 HTTP 헤더이므로
            HttpHeaders header = new HttpHeaders();

            //MIME타입 처리
            //java.nio.file패키지의 probeContentType()은 파일확장자를 통해 MIME타입을 반환한다
            header.add("Content-Type", Files.probeContentType(file.toPath()));

            //파일 데이터 처리
            //FileCopyUtils은 스프링 유틸 패키지에 있는 파일 및 스트림 복사를 위한 간단한 메서드를 모은 클래스
            //https://m.blog.naver.com/gaeean/221739006045
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);

        } catch(Exception e) {
            log.error(e.getMessage()); // 출력하지 않고 로그에만 보이도록 getMessage이용
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); //500에러 >> 서버문제 문제의 구체를 표현할 수 없음
        }

        return result;
    }

    @PostMapping("/removeFile") // 마찬가지로 경로문제 때문에 POST방식으로 해서 BODY로 받아야함
    public ResponseEntity<Boolean> removeFile(String fileName) {

        String srcFileName = null;

        try {
            srcFileName = URLDecoder.decode(fileName, "UTF-8");
            File file = new File(uploadPath + File.separator + srcFileName);
            boolean result = file.delete();

            File thumbnail = new File(file.getParent(), "s_" + file.getName());

            result = thumbnail.delete();

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<> (false, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
