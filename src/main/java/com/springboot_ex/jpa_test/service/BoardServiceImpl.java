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
        // Function<T, R> | R apply(T t) : T타입의 매개변수를 하나 받아 R타입의 반환값을 내는 경우

        //Page<Object[]> result = repository.getBoardWithReplyCount(pageRequestDTO.getPageable(Sort.by("bno").descending()));

        Page<Object[]> result = repository.searchPage(pageRequestDTO.getType(), pageRequestDTO.getKeyword(), pageRequestDTO.getPageable(Sort.by("bno").descending()));

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

    @Override
    @Transactional // 책에는 없었지만 있어야 오류가 안터진다... getOne은 참조만 따오는거라 한 @Transaction으로 묶어 같은 영속컨텍스트 안에서 움직여야 엔티티객체도 참조되고 save처리도 가능
    public void modify(BoardDTO dto) {

        Board board = repository.getOne(dto.getBno());
        //findById()가 아닌 getOne을 통해 필요한 순간까지 로딩을 지연시킨다
        
        //find는 DB의 데이터를 바로 가져오고 get은 데이터를 반환하기 위한 참조만을 가져옴
        //영속컨텍스트에서 관리하는 엔티티의 모든 필드가 필요한 것이 아닐때 LAZY Evaluation을 적용해 참조만 가져와 필요할 때 적용시킬 때 get
        //LAZY Evaluation을 이용하면 실제 데이터가 아닌 참조를 위한 프록시 객체를 생성

        //@Transaction이 프록시를 이용함 >> Target을 감싸는 구조를 취함 >> JPA는 @Transaction으로 묶은 영속 컨텍스트에서 Entity를 관리함 (프록시 설명은 아래에)
        
        //따라서 한 트랜잭션으로 묶어줘서 같은 트랜잭션으로 묶인 안의 영속 컨텍스트에서 처리를 해줘야 initializationException no Session이 발생하지 않음

        /*
        Proxy : 대리, 위임, 대리인 >> 시스템의 일부 기능을 다른 것이 임시로 대행하는 것

        Proxy(서버)
        데이터 요청 수신시 해당 서버에서 바로 자신의 PC로 가져오는 것이 아니라 임시 중계 서버를(프록시)를 거쳐서 가져오는 것. (클라이언트 < 프록시 < 서버)
        컴퓨터 네트워크에서 다른 서버로의 자원 요청을 중계하는 서버로, 분산 시스템의 구조를 단순화+캡슐화하여 서비스의 복잡도를 줄이는 역할을 한다.
        원격서버에 요청한 데이터들이 캐시되어 데이터 재요청 시 프록시에서 바로 제공이 가능하다 >> 전송시간과 트래픽 문제 완화
        프록시 측에서 위험이 예상되는 악성코드 필터링을 하거나 익명의 사용자가 바로 서버에 접근하는것을 막아 보안에도 효과가 있다. 역으로 사용자측에서 서핑기록을 익명화 하는데에도 쓰일 수 있다

        @Transaction은 스프링이 트랜잭션을 관리하는 방법중 하나로 프록시를 이용해 동작
        근데 위의 @Transaction에서 이야기하는 것은 스프링의 AOP패러다임의 Proxy일 것.

        AOP 패러다임
        Aspect > Concern > 개발자가 개발 시 고려해야 할 부분 중
        파라미터, 권한, 예외처리 등 핵심 로직은 아니나 온전한 코드를 위해 필요한 부분 (사전, 사후조건 같은 부분)

        AOP는 이런 관심사(Concern, Aspect)의 분리를 추구하여 핵심 비즈니스 로직에 집중하게 함
        관심사를 분리하여 모듈화 한 뒤
        실행 or 컴파일 시 결합시키도록 하여 코드 수정 최소화 등 유지보수 및 관리가 편하게 됨

        Target : 순수 핵심 비즈니스 로직
        Aspect : 관심사 > 이를 구현한 코드가 Advice
        Proxy : 중간에 필요한 Advice를 거쳐 내부의 Target을 호출함 by Target 내부의 JointPoint 메서드
        Pointcut : 여러 JointPoint 중(Target 내 메서드가 다양하므로) 어떤 Aspect가 필요한지 결정 (관심사+로직 지점)
	             : 결합 기준 : @execution(메서드), @within(클래스), this(I/F), @args(파라미터), @annotation

        여기서 AOP를 심도있게 하기보다
        @Transaction이 프록시를 이용함 >> Target을 감싸는 구조를 취함 >> JPA는 @Transaction으로 묶은 영속 컨텍스트에서 Entity를 관리함

        */
        board.changeTitle(dto.getTitle());
        board.changeContent(dto.getContent());

        repository.save(board);
    }
}
