package com.springboot_ex.jpa_test.service;

import com.springboot_ex.jpa_test.dto.BoardDTO;
import com.springboot_ex.jpa_test.dto.PageRequestDTO;
import com.springboot_ex.jpa_test.dto.PageResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister() {

        BoardDTO dto = BoardDTO.builder()
                .title("Test.")
                .content("Test....")
                .writerEmail("user55@aaa.com") // DB에 이미 생성해놓은 이메일
                .build();

        Long bno = boardService.register(dto);
        System.out.println(bno);
    }

    @Test
    public void testList() {

        PageRequestDTO pageRequestDTO = new PageRequestDTO();

        PageResultDTO<BoardDTO, Object[]> result = boardService.getList(pageRequestDTO);

        for(BoardDTO dto : result.getDtoList()) {
            System.out.println(dto);
        }
    }

    @Test
    public void testGet() {
        Long bno = 100L;
        BoardDTO dto = boardService.get(bno);
        System.out.println(dto);
    }

    @Test
    public void testRemove() {
        Long bno = 1L;
        boardService.removeWithReplies(bno);
    }

    @Test
    public void testModify() {

        BoardDTO dto = BoardDTO.builder()
                .bno(2L)
                .title("Title Change...")
                .content("Content Change...")
                .build();

        boardService.modify(dto);

    }

}
