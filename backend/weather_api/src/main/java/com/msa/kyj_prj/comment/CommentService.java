package com.msa.kyj_prj.comment;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class CommentService{
	@Autowired
	private CommentDAO conmmentDAO;

	// 댓글 등록
	@Transactional
	public int registForm(Comment commnet) {
		return conmmentDAO.regist(commnet);
	}

	// 댓글 업데이트
	@Transactional
	public boolean update(Comment commnet) {
			return conmmentDAO.update(commnet) > 0;
	}
	
	// 댓글 리스트
	public List<Comment> list(String bno) {
		return new ArrayList<Comment>(conmmentDAO.list(bno));
	}
	// 댓글 삭제
	@Transactional
	public boolean delete(int comment_no) {
		return conmmentDAO.delete(comment_no);
	}
}
