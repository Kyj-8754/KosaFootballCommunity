package com.msa.kyj_prj.club;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClubDAO {
    Club getClub(Integer clubId);
    int insert(Club club);
    int update(Club club);
    int delete(int clubId);
    List<Club> list(Map<String, Object> params);
    int getTotalCount(Map<String, Object> params);
}