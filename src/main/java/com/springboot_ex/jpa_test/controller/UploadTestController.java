package com.springboot_ex.jpa_test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadTestController {
    @GetMapping("/uploadTest")
    public String uploadTest() {
        return "uploadTest.html";
    }
    // 파일 업로드의 경우 직접 파일전송을 하며 테스트를 해보는게 좋기에 테스트용 HTML파일과 매핑 메서드 세팅
    // Ajax의 경우 위 파일에서 참조하자

    /*  MVC 디자인패턴 간략 정리
        Model : 비즈니스 규칙 표현
        View : 프레젠테이션 표현
        Controller : 양측 사이에 배치된 I/F

        >> @Controller는 사용자 요청 처리 후 지정한 뷰에 모델 객체를 넘겨주는 역할
        >> 요청처리를 위해 비즈니스 로직 호출 + 전달모델 정보 + 이동할 뷰 정보 dispatchServelt에 반환

        >> @RequestMapping : 사용자 요청에 대해 어떤 컨트롤러 or 컨트롤러 메서드를 사용할지 매핑

        >> RestController : 기존 @Controller 메서드들에 @ResponseBody를 기본으로 적용시켰다고 생각하면 간단
     */
}
