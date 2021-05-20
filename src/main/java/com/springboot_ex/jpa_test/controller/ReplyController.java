package com.springboot_ex.jpa_test.controller;

import com.springboot_ex.jpa_test.dto.ReplyDTO;
import com.springboot_ex.jpa_test.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*  5/18 : Rest API 간단 설명 및 밑준비 > RestContoller 설명

    API 서버 : 클라이언트가 원하는 데이터를 제공하는 서버

    현재는
    기존 JSP, Thymeleaf처럼 클라이언트에게 전송되어질 모든 데이터를 구성하는 SSR(Server Side Rendering)이 아닌
    프론트엔드와 백엔드가 분리되어
    CSR(Client Side Rendering)과 SPA(Single Page Application) 방식으로 프론트엔드를 구성하고 서버에는 중간에 필요한 데이터만을 비동기식으로 요청함

    SPA(Single Page App) : 모던 웹 패러다임 >> Native앱(그냥 우리가 생각하는 앱)과 유사한 UX 제공
                         : 초기에 모든 리소스를 받아 안에서 동작하며 필요시에만 부분적으로 서버에 요청 >> Web App의 핵심 / 구동은 느려도 이후 네트워크 비용과 속도가 매우 좋음
                         : 대신 기존 Web 서버에 비해 초기구동속도 + SEO(검색엔진최적화)가 문제

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

    ===================================================================================================================

    @RestController는 Spring에서 이런 REST API 구성을 위해 설정된 Controller Bean으로
    모든 메서드의 기본 리턴 타입이 JSON이다
    JSON : 마치 JS의 Object 타입 데이터 처럼 { key : value, key : value }의 형태로 구성된 데이터

    Ajax로 요청된 URI + HTTP은 각 Mapping 어노테이션으로 받는다
    @GetMapping / @PostMapping / @PutMaping / @DeleteMapping
    (기존 RequestMapping(method=RequestMethod.GET) 등을 간략화 시킨 어노테이션)

    반환타입을 ResponseEntity로 하면 HTTP 상태코드 등을 함께 전달 가능

    자주쓰는 Mapping Property
    Mapping의 value(url)에서 {param} 부분은 @PathVariable("param")으로 메서드의 파라미터로 받을 수 있음
    replies/board/100 > replies/board/{bno} > @PathVariable("bno")로 파라미터로 게시글번호 100 받을 수 있음

    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} 기본은 JSON이나 XML로 선택하거나 명시화 가능

    consumes = {MediaType.APPLICATION_JSON_VALUE} 얘도 기본 JSON 위가 반환 데이터 타입이라면 얘는 수신 데이터타입
 */
@RestController
@RequestMapping("/replies/")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping(value="/board/{bno}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReplyDTO>> getListByBoard (@PathVariable("bno") Long bno) {
        log.info("bno : " + bno);

        return new ResponseEntity<>(replyService.getList(bno), HttpStatus.OK);
    }

    // 설정해놓은 Swagger나 (localhost:8005/swagger-ui.html)
    // localhost:8005/replies/board/게시물번호 로 확인

    /*  Get Ajax 처리 예시

        function loadJSON() {
            $.getJSON('/replies/board/'+bno, function(arr) {

            console.log(arr);

            ~
        }

     */

    @PostMapping("")
    public ResponseEntity<Long> register (@RequestBody ReplyDTO replyDTO) { // @RequestBody : 수신된 JSON 데이터를 지정한 타입 객체로 매핑
        log.info(replyDTO);

        Long rno = replyService.register(replyDTO);

        return new ResponseEntity<>(rno, HttpStatus.OK);
    }

    /*  Post Ajax 처리 예시

        $(".replySave").click(function() {
            let reply = {
                bno : bno,
                text : $('input[name="replyText"]').val(),
                replyer: $('input[name="replyer"]').val()
            }
            console.log(reply);

            $.ajax({
                url: '/replies',
                method: 'post',
                data: JSON.stringify(reply), // 객체를 'JSON 문자열로' 변환
                contentType: 'application/json; charset=uft-8',
                dataType: 'json',
                success: function(data) {
                    console.log(data);

                    let newRno = parseInt(data);

                    alert(newRno + "번 댓글이 등록되었습니다");
                }
            })
        });

     */

    @PutMapping("/{rno}")
    public ResponseEntity<String> modify(@RequestBody ReplyDTO replyDTO) {
        log.info(replyDTO);

        replyService.modify(replyDTO);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
    
    /*  Put Ajax 처리 예시
        $(".replyModify").click(function() {
        
            let rno = $("input[name='rno']").val();
            
            let reply = {
                rno: rno,
                bno: bno,
                text: $('input[name="replyText"]').val(),
                replyer: $('input[name="replyer"]').val()
            }
            
            $.ajax({
                url: '/replies/' + rno;
                method: 'put',
                data: JSON.stringify(reply),
                contentType: 'application/json; charset=utf-8",
            
                success: function(result) {
                    console.log("result: "+result);
                    if(result=="success") {
                        alert("댓글이 수정되었습니다");
                    }
                }
            })
        });
    
     */

    @DeleteMapping("/{rno}")
    public ResponseEntity<String> remove(@PathVariable("rno") Long rno) {
        log.info("RNO : " + rno);

        replyService.remove(rno);

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
    
    /*  Delete Ajax 처리 예시
       $(".replyRemove").on("click", function() {
       
        let rno = $("input[name='rno']").val();
        
        $.ajax({
            url: '/replies/' + rno;
            method: 'delete',
            
            success: function(result) {
                    console.log("result: "+result);
                    if(result=="success") {
                        alert("댓글이 삭제되었습니다");
                    }
                }
          })
        });
    
     */



}
