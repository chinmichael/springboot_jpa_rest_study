package com.springboot_ex.jpa_test.service;

import com.springboot_ex.jpa_test.dto.GuestbookDTO;
import com.springboot_ex.jpa_test.dto.PageRequestDTO;
import com.springboot_ex.jpa_test.dto.PageResultDTO;
import com.springboot_ex.jpa_test.entity.Guestbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@SpringBootTest
public class GuestbookServiceTests {

    @Autowired
    private GuestbookService service;

    @Test
    public void testRegister() {
        GuestbookDTO guestbookDTO = GuestbookDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("Sample Writer...")
                .build();

        System.out.println(service.register(guestbookDTO));
    }

    @Test
    public void testList() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build(); // 사식 기본 생성자 변수초기화가 있으니 1, 10 값이면 이리 안가도 되지만 되도록 쓰는 습관을

        PageResultDTO<GuestbookDTO, Guestbook> resultDTO = service.getList(pageRequestDTO);
        // 흠... 지네릭 extends 제한을 걸기에는 Entity는 그렇다치고 DTO는 걍 Object나 다름이 없게 되나

        for(GuestbookDTO guestbookDTO : resultDTO.getDtoList()) {
            System.out.println(guestbookDTO);
        }
    }

    @Test
    public void testList2() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResultDTO<GuestbookDTO, Guestbook> resultDTO = service.getList(pageRequestDTO);

        System.out.println("Prev : " + resultDTO.isPrev());
        System.out.println("Next : " + resultDTO.isNext());
        System.out.println("Total : " + resultDTO.getTotalPage());
        System.out.println();
        System.out.println("=============================================================");
        System.out.println();
        for(GuestbookDTO guestbookDTO : resultDTO.getDtoList()) {
            System.out.println(guestbookDTO);
        }
        System.out.println();
        System.out.println("=============================================================");
        System.out.println();
        resultDTO.getPageList().forEach(i -> System.out.println(i));
    }
}
