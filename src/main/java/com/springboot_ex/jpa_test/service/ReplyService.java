package com.springboot_ex.jpa_test.service;

import com.springboot_ex.jpa_test.dto.ReplyDTO;
import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Reply;

import java.util.List;

public interface ReplyService {

    // 댓글 등록
    Long register(ReplyDTO dto);

    // 게시물 댓글 조회
    List<ReplyDTO> getList(Long bno);

    // 댓글 수정
    void modify(ReplyDTO dto);

    // 댓글 삭제
    void remove(Long rno);
    
    // Service I/F 단골 DTO와 Entity 객체 사이 전환 메서드들
    default Reply dtoToEntity(ReplyDTO dto) {

        Board board = Board.builder().bno(dto.getBno()).build();

        Reply reply = Reply.builder()
                .rno(dto.getRno())
                .text(dto.getText())
                .replyer(dto.getReplyer())
                .board(board)
                .build();

        return reply;
    }
    
    default ReplyDTO entityToDTO(Reply reply) {
        
        // 게시물 번호는 필요상황에서 setter로

        ReplyDTO dto = ReplyDTO.builder()
                .rno(reply.getRno())
                .text(reply.getText())
                .replyer(reply.getReplyer())
                .regDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build();

        return dto;
    }
}
