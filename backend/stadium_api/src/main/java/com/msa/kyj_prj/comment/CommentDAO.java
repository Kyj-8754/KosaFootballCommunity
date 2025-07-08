package com.msa.kyj_prj.comment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.kyj_prj.dto.Comment;

// DB연동 SQL
@Mapper
public interface CommentDAO {
	public int regist(Comment comment);
	public int update(Comment comment);
	public boolean delete(int comment_no);
	public List<Comment> list(String list);
}
