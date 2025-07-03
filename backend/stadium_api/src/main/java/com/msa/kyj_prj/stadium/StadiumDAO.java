package com.msa.kyj_prj.stadium;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

// DB연동 SQL
@Mapper
public interface StadiumDAO {
	public Stadium getStadium(String svcid);
	public List<String> findAllSvcIds();
	public int update(Stadium stadium);
	public int regist(Map<String, Object> stadium);
	public int registPatch(Map<String, Object> stadium);
	public void callSyncStadiumProcedure();
	public List<Stadium> list(Map<String, Object> map);
	public int getTotalCount(Map<String, Object> map);
	public void insertApiDetailLog(Map<String, Object> log);
	public List<Slot> getAvailableDate(String SVCID);
}
