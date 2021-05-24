package com.kh.codelit.community.study.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.codelit.community.study.model.dao.CommentDAO;
import com.kh.codelit.community.study.model.vo.Comment;
import com.kh.codelit.community.study.model.vo.StudyBoard;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentDAO dao;

	@Override
	public int insertCmt(Comment cmt) {
		return dao.insertCmt(cmt);
	}

	@Override
	public Comment selectStdNo(int cmtNo) {
		return dao.selectStdNo(cmtNo);
	}

	@Override
	public int delete(int cmtNo) {
		return dao.delete(cmtNo);
	}

	@Override
	public int update(int stdCmtNo) {
		return dao.update(stdCmtNo);
	}



	
}
