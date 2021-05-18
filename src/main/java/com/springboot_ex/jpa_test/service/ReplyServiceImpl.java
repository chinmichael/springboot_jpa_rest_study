package com.springboot_ex.jpa_test.service;

import com.springboot_ex.jpa_test.dto.ReplyDTO;
import com.springboot_ex.jpa_test.entity.Board;
import com.springboot_ex.jpa_test.entity.Reply;
import com.springboot_ex.jpa_test.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository repository;

    @Override
    public Long register(ReplyDTO dto) {

        Reply reply = dtoToEntity(dto);

        repository.save(reply);

        return reply.getRno();
    }

    @Override
    public List<ReplyDTO> getList(Long bno) {

        List<Reply> result = repository.getRepliesByBoardOrderByRno(Board.builder().bno(bno).build());

        return result.stream().map(reply -> entityToDTO(reply)).collect(Collectors.toList());
        // 항상 콜백함수 순서 생각 >> result란 List를 stream으로 뽑아다 map으로 DTO타입으로 내부객체를 연결해서 collect함수로 다시 컬렉션 객체로함
    }

    @Override
    public void modify(ReplyDTO dto) {

        Reply reply = dtoToEntity(dto);

        repository.save(reply);
    }

    @Override
    public void remove(Long rno) {

        repository.deleteById(rno);
    }
}
