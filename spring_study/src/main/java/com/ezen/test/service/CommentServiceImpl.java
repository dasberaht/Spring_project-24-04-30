package com.ezen.test.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ezen.test.domain.CommentVO;
import com.ezen.test.repository.BoardDAO;
import com.ezen.test.repository.CommentDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

	private final CommentDAO cdao;
	private final BoardDAO bdao;

	@Override
	public int post(CommentVO cvo) {
		log.info(">> comment post service in");
//		int isOk = cdao.post(cvo);
		
		// comment에서 post할 때마다 1씩 증가하도록 설정
//		if(isOk > 0) {
//			isOk *= bdao.cmtCount(cvo.getBno(), 1);		
//		}
//		return isOk;
		return cdao.post(cvo);
	}

	@Override
	public List<CommentVO> getList(int bno) {
		log.info(">> comment getList service in");
		
		return cdao.getList(bno);
	}

	@Override
	public int modify(CommentVO cvo) {
		log.info(">> comment modify service in");
		return cdao.update(cvo);
	}

	@Override
	public int delete(int cno) {
		log.info(">> comment delete service in");
//		int isOk = cdao.delete(cno);
//		if(isOk > 0) {
////			isOk *= bdao.cmtCountMi(bno);
//			int cnt = -1;
//			isOk *= bdao.cmtCount(bno, cnt);
//		}
		
//		return isOk;
		return cdao.delete(cno);
	}
	
}
