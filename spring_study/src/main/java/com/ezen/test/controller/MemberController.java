package com.ezen.test.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ezen.test.domain.MemberVO;
import com.ezen.test.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member/*")
@Controller
public class MemberController {

	private final MemberService msv;
	
	@GetMapping("/register")
	public void register() {}
	
	
	@PostMapping("/register")
	public String register(MemberVO mvo) {
		log.info("> member register mvo {}", mvo);
		int isOk = msv.insert(mvo);
		log.info(">>> member register isOk {}", (isOk > 0 ? "OK" : "FAIL"));
		
		return "index";
	}
	
	@GetMapping("/login")
	public void login() {}
	
	@PostMapping("/login")
	public String login(MemberVO mvo, HttpServletRequest request, Model m) {
		log.info("> member login mvo {}", mvo);
		
		// mvo 객체가 DB의 값과 일치하는 객체를 가져오기
		MemberVO loginMvo = msv.isUser(mvo);
		log.info(">> member loginMvo mvo {}", mvo);
		
		if(loginMvo != null) {
			//로그인 성공 시 
			HttpSession ses = request.getSession();
			ses.setAttribute("ses", loginMvo);		// 세션에 로그인 객체 저장 
			ses.setMaxInactiveInterval(10*30);		// 로그인 유지 시간
		}else {
			//로그인 실패 시
			m.addAttribute("msg_login", "1");
			
//			return "/member/login";
		}
		return "index";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, Model m) {
		// last_login 업데이트 > 세션 삭제 > 세션 끊기
		MemberVO mvo = (MemberVO)request.getSession().getAttribute("ses");
		msv.lastloginUpdate(mvo.getId());
		
		// 세션 삭제
		request.getSession().removeAttribute("ses");
		// 세션 끊기
		request.getSession().invalidate();
		
		m.addAttribute("msg_logout", "1");
		
		return "index";
	}
	
	@GetMapping("/modify")
	public void modify() {}
	
	@PostMapping("/modify")
	public String modify(MemberVO mvo) {
		log.info("> member modify mvo {}", mvo);
		
		msv.modify(mvo);
		
		return "redirect:/member/logout";
	}
	
	
	@GetMapping("/remove")
	public String remove(HttpServletRequest request, RedirectAttributes re) {
		MemberVO mvo = (MemberVO)request.getSession().getAttribute("ses");
		msv.remove(mvo.getId());
		
		// 리터값이 내부 메서드를 리턴하기 때문에 Model 속성을 사용할 수 없다.
		// 따라서, RedirectAttributes 속성을 사용하여 msg를 출력할 수 있다.
		//		re.addFlashAttribute("msg_remove", "1");
		re.addAttribute("msg_remove", "1");
		
		
		
		return "redirect:/member/logout";
	}
	
	
	
}
