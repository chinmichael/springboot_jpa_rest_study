package com.springboot_ex.jpa_test.service;

import com.springboot_ex.jpa_test.dto.BoardDTO;
import com.springboot_ex.jpa_test.dto.PageRequestDTO;
import com.springboot_ex.jpa_test.dto.PageResultDTO;
import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Member;
import com.springboot_ex.jpa_test.repository.BoardRepository;
import com.springboot_ex.jpa_test.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImpl implements BoardService {

    private final BoardRepository repository;
    private final ReplyRepository replyRepository;

    @Override
    public Long register(BoardDTO dto) {

        log.info(dto);

        Board board = dtoToEntity(dto);

        repository.save(board);

        return board.getBno();
    }

    @Override
    public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {
        
        log.info(pageRequestDTO);
        
        Function<Object[], BoardDTO> fn = en -> entityToDTO((Board)en[0], (Member)en[1], (Long)en[2]);

        Page<Object[]> result = repository.getBoardWithReplyCount(pageRequestDTO.getPageable(Sort.by("bno").descending()));
        
        return new PageResultDTO<>(result, fn); //위에서 이미 반환타입으로 명시했으니까 지네릭 생략가능
    }

    @Override
    public BoardDTO get(Long bno) {

        Object[] result = (Object[]) repository.getBoardByBno(bno);

        return entityToDTO((Board) result[0], (Member)result[1], (Long)result[2]);
    }


    /*
    실제 개발에서 게시물 삭제 처리는 문제가 있음 >> 연관 댓글들이 동의없이 삭제될 수 있기에
    따라서 게시물 state 컬럼을 만들어서 이를 변경하는 식으로 처리하는 경우가 많음

    우선 책에서는 이를 고려하지 않고 참조 레코드들이 함께 삭제되는 것으로 처리

    단순히 참조 DELETE 옵션으로 처리할 것이 아닌 엔티티 객체 관리 측면의 문제도 있으므로
    1) 참조하는 Reply의 객체(레코드)들을 삭제하고
    2) 참조되는 Board의 객체(레코드)들을 삭제처리 할 수 있도록 짜야함

    ★ 그리고 무엇보다 두 작업이 하나의 트랜잭션으로 처리되야 중간에 어느 한쪽 처리만 되거나 중간에 작업이 끼거나 하는 불상사가 발생하지 않음
    >> 따라서 하나의 트랜잭션 처리를 위해 @Transactional 어노테이션을 붙인다.
    */
    @Override
    @Transactional
    public void removeWithReplies(Long bno) {

      replyRepository.deleteByBno(bno);
      // 참조하는 댓글먼저 삭제처리
      // 댓글은 Reply엔티티의 컬럼이 아닌 참조하는 Board의 번호를 기준으로 삭제되므로 따로 쿼리 메서드를 작성해야한다.

      repository.deleteById(bno);

    }
}
