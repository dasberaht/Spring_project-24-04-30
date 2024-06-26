package com.ezen.test.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ezen.test.domain.BoardDTO;
import com.ezen.test.domain.BoardVO;
import com.ezen.test.domain.CommentVO;
import com.ezen.test.domain.FileVO;
import com.ezen.test.domain.PagingVO;
import com.ezen.test.handler.FileHandler;
import com.ezen.test.handler.PagingHandler;
import com.ezen.test.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequestMapping("/board/*")
@Slf4j
@RequiredArgsConstructor
@Controller
// Mapping명(/board/*)을 views의 folder명(board)과 동일하게 맞춘다.

public class BoardController {

	
	// inject : 생성자 주입 역할 (또는 @Autowired) > 이와 같이 사용 시 final 은 사용하지 X
	//	@Inject
	private final BoardService bsv;	
	
	// 파일핸들러 호출
	private final FileHandler fhd;
	
	// 메서드생성 영역
	// 이동 메서드(이동경로가 리턴 값)
	@GetMapping("/register")
	public String register() {
		return "/board/register";
	}
	
	
	// get방식, post방식인 경우 mapping 경로가 같아도 사용가능
	// @RequestParam("name")String name : 파라미터를 받을 때
	// required = false는 필수여부를 확인 (파라미터가 없어도 예외가 발생하지 않는다.) 
	@PostMapping("/insert")
	public String insert(BoardVO bvo, @RequestParam(name = "files", required = false) MultipartFile[] files) {
		log.info("> board bvo {}", bvo);
		log.info("> board files {}", files);
			
		// 파일 핸들러 처리
		// 파일 저장처리 > fileList 리턴
		List<FileVO> flist = null;
				
		// 파일이 있다면, 파일핸들러를 호출
		if(files[0].getSize() > 0) {
			// 핸들러호출
			flist = fhd.uploadFiles(files);
			log.info("> board flist {}", flist);
			
			// 파일 개수 출력 시
//			bvo.setHas_file(flist.size());	// 들어가는 파일 개수를 바로 set
			
			
		}
		
		BoardDTO bdto = new BoardDTO(bvo, flist);		
		int isOk = bsv.insert(bdto);
				
		return "redirect:/board/list";
	}
	
	
	@GetMapping("/list")
	public String list(Model m, PagingVO pgvo) {	// PagingVO 파라미터가 없으면 null값이 출력
		log.info("> pgvo의 parameter : {}", pgvo);
		// 리턴타입은 목적지 경로에 대한 타입( destPage가 return값과 유사 )
		// Model 객체 = (JSP)request,setAttribute의 역할을 하는 객체
		
		// cmt_qty, has_file update 후 리스트 가져오기
		bsv.cmtFileUpdate();
		
		
		List<BoardVO> list = bsv.getList(pgvo);
		
		
		// totalCount DB에서 검색
		int totalCount = bsv.getTotalCount(pgvo);
		log.info("> board totalCount {}", totalCount);
		PagingHandler ph = new PagingHandler(pgvo, totalCount);
		
		m.addAttribute("list", list);
		log.info("> board list {}", list);
		m.addAttribute("ph", ph);
		log.info("> board ph {}", ph);
		
		
		return "/board/list";
	}
	
// 댓글 수 처리 전
//	@GetMapping("/list")
//	public String list(Model m, PagingVO pgvo) {	// PagingVO 파라미터가 없으면 null값이 출력
//		log.info("> pgvo의 parameter : {}", pgvo);
//		// 리턴타입은 목적지 경로에 대한 타입( destPage가 return값과 유사 )
//		// Model 객체 = (JSP)request,setAttribute의 역할을 하는 객체
//		List<BoardVO> list = bsv.getList(pgvo);
//		
//		
//		// totalCount DB에서 검색
//		int totalCount = bsv.getTotalCount(pgvo);
//		log.info("> board totalCount {}", totalCount);
//		PagingHandler ph = new PagingHandler(pgvo, totalCount);
//		
//		m.addAttribute("list", list);
//		log.info("> board list {}", list);
//		m.addAttribute("ph", ph);
//		log.info("> board ph {}", ph);
//		
//		
//		return "/board/list";
//	}
	
	
	// detail > /board/detail (jsp > contr~) > return : /board/detail
	// modify > /board/modity (jsp > contr~) > return : /board/modify
	// 즉, controller로 들어오는 경로와 jsp로 나가는 경로가 일치하면 void 처리 가능
	@GetMapping({"/detail", "/modify"})
	public void detail(Model m, @RequestParam("bno") int bno) {
		log.info("> board bno {}", bno);
//		BoardVO bvo = bsv.getDetail(bno);
		BoardDTO bdto = bsv.getDetail(bno);
		log.info("> board bdto {}", bdto);
		
//		m.addAttribute("bvo", bvo);
		m.addAttribute("bdto", bdto);				
		
//		return "/board/detail";
	}
	
	@PostMapping("/modify")
	public String modify(BoardVO bvo, @RequestParam(name="files", required = false) MultipartFile[] files) {
		log.info("> board modify {}", bvo);
		
		// file 수정 추가
		List<FileVO> flist = null;
		
		// file 수정 : fileHandler multipartfile[] >>> flist
		if(files[0].getSize() > 0) {
			flist = fhd.uploadFiles(files);
			
			// 파일 개수 출력 시
//			bvo.setHas_file(bvo.getHas_file()+flist.size());
			
		}
		
		BoardDTO bdto = new BoardDTO(bvo, flist);
		bsv.update(bdto);
		
		
//		bsv.update(bvo);		// 게시글만 수정할 경우
		// return을 /board/detail.jsp로 설정할 경우 새로운 데이터를 가지고 가야하므로, redirect method 사용
		// (단, detail 메서드를 사용하려면, bno 파라미터가 필요하므로, 아래와 같이 설정 필요)
		return "redirect:/board/detail?bno="+bvo.getBno();
	}
	
	
	@GetMapping("/remove")
	public String remove(@RequestParam("bno") int bno) {
		log.info("> board remove {}", bno);
		bsv.remove(bno);
		
		return "redirect:/board/list";
	}
	
	@DeleteMapping(value = "/{uuid}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> removeFile(@PathVariable("uuid") String uuid){
		log.info("> board removeFile uuid {}", uuid);
		int isOk = bsv.removeFile(uuid);	
		return isOk>0 ? new ResponseEntity<String>("1", HttpStatus.OK) : new ResponseEntity<String>("0", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
	
}
