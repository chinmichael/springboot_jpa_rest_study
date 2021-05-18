package com.springboot_ex.jpa_test.controller;

import org.springframework.web.bind.annotation.RestController;

/*  5/18 : Rest API 간단 설명 및 밑준비 > RestContoller 설명

    API 서버 : 클라이언트가 원하는 데이터를 제공하는 서버

    현재는
    기존 JSP, Thymeleaf처럼 클라이언트에게 전송되어질 모든 데이터를 구성하는 SSR(Server Side Rendering)이 아닌
    프론트엔드와 백엔드가 분리되어
    CSR(Client Side Rendering)과 SPA(Single Page Application) 방식으로 프론트엔드를 구성하고 서버에는 중간에 필요한 데이터만을 비동기식으로 요청함

    즉 쉽게 말해
    프론트 엔드는 React.js 등의 라이브러리 도구 등을 통해 Web-app의 형태로 만들어 SPA방식으로 초기 구동에 필요한 데이터만 서버로 받고
    중간에 데이터 요청 전달이 필요할 때만 Ajax로 비동기 식으로 처리
    백엔드 서버는 XML, JSON 형태로 필요한 데이터만 전송해준다

    이를 통해 재사용성이 상승하는데
    문제는 이렇게 재사용성이 높은 서버를 구축하려면 Ajax로 데이터 요청 및 전송 시 URL 혹은 URI가 가능하면 식별성이 높아야 함
    따라서 기존 get, post 방식으로 자원 + 명령의 구조의 uri를 보내던 방식이 아닌

    HTTP 자원을 풀로 활용하여
    URI에는 자원만을 명시하고 + Ajax로 HTTP 메서드 POST / GET / PUT / DELETE를 활용하여 서버와 주고받게 구성된 API 서버가 RESTful API

    >> 짜피 모든 데이터 처리는 DB에 처리되는 대로 CRUD로 집약되고 | 모든 웹도 사실 저 CRUD를 화면에 구성한 게시판의 확장형이다

    @RestController는 Spring에서 이런 REST API 구성을 위해 설정된 Controller Bean으로
    모든 메서드의 기본 리턴 타입이 JSON이다
    JSON : 마치 JS의 Object 타입 데이터 처럼 { key : value, key : value }의 형태로 구성된 데이터 타입

    Ajax로 요청된 URI + HTTP은 각 Mapping 어노테이션으로 받는다
    @GetMapping / @PostMapping / @PutMaping / @DeleteMapping
    (기존 RequestMapping(method=GET) 등을 간략화 시킨 어노테이션)


 */
@RestController
public class ReplyController {
}
