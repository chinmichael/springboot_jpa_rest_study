package com.springboot_ex.jpa_test.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class PageResultDTO<DTO, EN> { // EN = Entity를 표현한 타입변수
                                      // 지네릭 타입변수를 이용해 여러 종류의 Data 페이징처리에 재사용이 가능

    private List<DTO> dtoList;

    //페이징 정보들
    private int totalPage; // 총 페이지 번호(개수)

    private int page; // 현재 페이지 번호
    private int size; // 페이지 당 목록 개수

    private int start, end; // 현재페이지에서 보이는 시작 페이지, 끝 페이지 번호

    private boolean prev, next; // 이전, 다음 여부

    private List<Integer> pageList; // 페이지 번호 목록

    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn) { // 지네릭 람다 스트림 공부 시작했으니 곧 정리 추가 ㄱㄱ함
        // Function<EN, DTO>는 Entity객체들을 DTO로 변환해주는 용도

        // 기존처럼 데이터 종류마다 추상클래스를 통해 객체변환을 처리해주지 않고,
        // Service I/F의 default 메서드로 변환처리해주는 메서드를 파라미터로 받아와 PageResultDTO에서 처리할 수 있게 해줌

        // 서비스에서 맡는 로직분담을 최대한 효율적으로 나누고 전달되는 데이터가 너무 많아지지 않도록 하기 위함인가??

        dtoList = result.stream().map(fn).collect(Collectors.toList());

        totalPage = result.getTotalPages();

        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable) { // 가져온 Pageable객체를 가지고 내부적으로 처리되야 안전

        this.page = pageable.getPageNumber() + 1; // JPA에선 항상 0부터 페이지시작
        this.size = pageable.getPageSize();

        int tempEnd = (int)(Math.ceil(page/10.0)) * 10;
        // 페이징처리 계산은 임시 끝번호를 세우고 (한 페이지의 페이지번호가 끝까지 있다고 가정할 때의 끝번호) 계산하는게 수월
        // 일반적으로 페이지 번호를 10개씩 보여주므로 우선 10을 세팅
        // 각 현재 페이지가 있는 라인에서 일의 자리를 올림처리하도록함

        start = tempEnd - 9;
        // 페이지 번호를 10개씩 보여주고 + tempEnd는 풀로 채워진 끝번호란 가정하에 있기에 -9만 해주면 됨

        end = totalPage > tempEnd ? tempEnd : totalPage;
        // 마지막 페이지번호 라인의 경우 실제 끝번호 체크를 실제 총 페이지 개수랑 비교해주면 됨

        prev = start > 1;

        next = totalPage > tempEnd;

        pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        // 현재페이지에서 보여줄 페이지 번호 라인은 시작번호에서 끝번호까지의 값을 리스트로 넣어 보낸다.

    }
}

/*  PageResultDTO : PageRequestDTO가 요청받은 페이지 정보(현재페이지, 사이즈, 정렬 등)으로 JPA처리를 위한 Pageable객체를 만들었다면

                    > PageReusultDTO에서는
                       - Repository에서 반환한 Page<Entity>타입의 Entity 객체들을 DTO로 변환해 자료구조로 담고
                       - 거기에 화면출력에 필요한 페이지 정보를 구성함 (총 페이지나 표시될 페이지 개수 등)
 */

/*  최종적으로 화면에 전달되는 목록 데이터 DTO는 PageResultDTO
    >> 기본적으로 리스트데이터 / 시작페이지번호 / 끝페이지번호 / 이전-다음 이동 링크 여부 / 현재페이지번호 >> 이것들이 보여야 하며
    >> 그리고 이들의 계산을 위해 총 페이지 개수가 추가적으로 필요함

 */
