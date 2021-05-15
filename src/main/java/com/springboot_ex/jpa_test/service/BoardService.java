package com.springboot_ex.jpa_test.service;

import com.springboot_ex.jpa_test.dto.BoardDTO;
import com.springboot_ex.jpa_test.dto.PageRequestDTO;
import com.springboot_ex.jpa_test.dto.PageResultDTO;
import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Member;

public interface BoardService {

    Long register(BoardDTO dto);

    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

    BoardDTO get(Long bno);

    void removeWithReplies(Long bno);

    void modify(BoardDTO dto);

    /*
    Board Entity는 내부에 Member Entity를 참조하므로
    실제 DB에 존재하는 Member의 PK(Board입장에선 참조하는 FK)을 통해 Member Entity 객체를 처리하여
    Board Entity를 Build한다
     */
    default Board dtoToEntity(BoardDTO dto) {

        Member member = Member.builder().email(dto.getWriterEmail()).build();

        Board board = Board.builder()
                .bno(dto.getBno())
                .title((dto.getTitle()))
                .content(dto.getContent())
                .writer(member)
                .build();

        return board;
    }
    
    /*
    게시물 목록 처리 >> 페이징 필요 >> DTO로 PageRequestDTO, PageResultDTO 사용
    PageResultDTO >> JPQL의 결과인 Object[]을 DTO로 전환 (함수형 인터페이스를 이용)
    이때 Object[]의 포함되는 board, member, replyCount을 DTO로 전환할 수 있는 메서드
    */
    default BoardDTO entityToDTO (Board board, Member member, Long replyCount) {
        
        BoardDTO dto = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writerEmail(member.getEmail())
                .writerName((member.getName()))
                .replyCount(replyCount.intValue())
                .build();
        
        return dto;
    }
}
