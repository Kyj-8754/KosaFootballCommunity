package com.msa.kyj_prj.member;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// DB연동 SQL
@Mapper
public interface LoginDAO {
	
	public Member getMember(String userid);
	public void setLoginTime(String userid);
	public int update(Member member);
	public int regist(Member member);
	public List<Member> list(Map<String, Object> map);
	public int getTotalCount(Map<String, Object> map);
	public int fail_login(String userid);
	public int unban(String userid);
	public boolean ban(String userid);
	public boolean delete(String userid);
	public Member findMember(@Param("username") String username,@Param("phone_no") String phone_no, @Param("userid") String userid);
	public boolean rePasswd(@Param("userid") String userid,@Param("passwd") String hash);
}
