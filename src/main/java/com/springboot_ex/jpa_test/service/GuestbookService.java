package com.springboot_ex.jpa_test.service;

import com.springboot_ex.jpa_test.dto.GuestbookDTO;
import com.springboot_ex.jpa_test.dto.PageRequestDTO;
import com.springboot_ex.jpa_test.dto.PageResultDTO;
import com.springboot_ex.jpa_test.entity.Guestbook;

public interface GuestbookService {
    Long register(GuestbookDTO dto);

    PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO);

    default Guestbook dtoToEntity(GuestbookDTO dto) {
        Guestbook entity = Guestbook.builder()
                .gno(dto.getGno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();

        return entity;
    }

    default GuestbookDTO entityToDto(Guestbook entity) {
        GuestbookDTO dto = GuestbookDTO.builder()
                .gno(entity.getGno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();

        return dto;
    }
}

/*  I/F default : I/F에 실제 내용을 가지는 코드를 default 키워드로 생성가능 (Java 8버전부터 추가됨)
    > 기존 추상클래스를 통해 전달해야했던 (I/F > 추상클래스 > 구현클래스)의 과정을 간략화 가능

 */
