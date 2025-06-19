package com.msa.kyj_prj.recruit;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RecruitDAO {

    private final SqlSession sqlSession;

    // ✅ XML mapper의 namespace와 일치하게 수정
    private static final String NAMESPACE = "com.msa.kyj_prj.recruit.RecruitDAO.";

    // 클럽별 모집글 목록 조회
    public List<RecruitBoard> findByClubId(int clubId) {
        return sqlSession.selectList(NAMESPACE + "findByClubId", clubId);
    }

    // 모집글 단건 조회
    public RecruitBoard findById(int bno) {
        return sqlSession.selectOne(NAMESPACE + "findById", bno);
    }

    // 모집글 등록
    public void insert(RecruitBoard board) {
        sqlSession.insert(NAMESPACE + "insert", board);
    }
    
    // 전체 모집글 목록
    public List<RecruitBoard> findAll() {
        return sqlSession.selectList(NAMESPACE + "findAll");
    }
    
    // 조회수 기준 인기순 정렬
    public List<RecruitBoard> findAllOrderByViewCount() {
        return sqlSession.selectList(NAMESPACE + "findAllOrderByViewCount");
    }



    // 모집글 수정
    public void update(RecruitBoard board) {
        sqlSession.update(NAMESPACE + "update", board);
    }

    // 모집글 삭제
    public void delete(int bno) {
        sqlSession.delete(NAMESPACE + "delete", bno);
    }

    // 팀장 여부 확인
    public boolean isClubLeader(String userId, int clubId) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("clubId", clubId);
        Integer result = sqlSession.selectOne(NAMESPACE + "isClubLeader", param);
        return result != null && result > 0;
    }
}
