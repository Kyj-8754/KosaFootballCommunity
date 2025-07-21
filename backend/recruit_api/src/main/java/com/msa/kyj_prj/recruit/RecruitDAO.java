package com.msa.kyj_prj.recruit;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecruitDAO {

    // 전체 모집글 목록
    List<RecruitBoard> find_all();

    // 조회수 기준 인기순 정렬
    List<RecruitBoard> find_all_order_by_view_count();

    // 클럽별 모집글 목록 조회
    List<RecruitBoard> find_by_club_id(@Param("club_id") int club_id);

    // 전체 모집글에서 keyword로 검색
    List<RecruitBoard> find_by_keyword(String keyword);

    // 조회수순 + keyword 검색
    List<RecruitBoard> find_by_keyword_order_by_view_count(String keyword);

    // 모집글 단건 조회
    RecruitBoard find_by_id(@Param("bno") int bno);

    // 모집글 등록
    void insert(RecruitBoard board);

    // 모집글 수정
    void update(RecruitBoard board);

    // 조회수 증가
    void increase_view_count(@Param("bno") int bno);

    // 모집글 마감
    void update_is_closed(@Param("bno") int bno, @Param("is_closed") int is_closed);

    // 모집글 삭제
    void delete_recruit(@Param("bno") int bno);

    // ✅ 최신순 페이징 목록 조회 (검색어 포함)
    List<RecruitBoard> find_paginated_order_by_recent(
        @Param("startRow") int startRow,
        @Param("pageSize") int pageSize,
        @Param("keyword") String keyword
    );

    // ✅ 인기순 페이징 목록 조회 (검색어 포함)
    List<RecruitBoard> find_paginated_order_by_view_count(
        @Param("startRow") int startRow,
        @Param("pageSize") int pageSize,
        @Param("keyword") String keyword
    );

    // ✅ 전체 게시글 수 조회 (검색어 포함)
    int count_total(@Param("keyword") String keyword);
}
