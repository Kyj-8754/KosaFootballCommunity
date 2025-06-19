package com.msa.kyj_prj.recruit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitDAO recruitDAO;

    // ✅ 전체 모집글 목록 조회 (최신순)
    public List<RecruitBoard> getAllRecruits() {
        return recruitDAO.findAll();
    }

    // ✅ 조회수 기준 인기순 정렬
    public List<RecruitBoard> getRecruitsOrderByViews() {
        return recruitDAO.findAllOrderByViewCount();
    }

    // ✅ 클럽별 모집글 목록 조회
    public List<RecruitBoard> getRecruitsByClub(int clubId) {
        return recruitDAO.findByClubId(clubId);
    }

    // ✅ 모집글 단건 조회
    public RecruitBoard getRecruit(int bno) {
        return recruitDAO.findById(bno);
    }

    // ✅ 모집글 등록
    public void createRecruit(RecruitBoard board) {
        recruitDAO.insert(board);
    }

    // ✅ 모집글 수정
    public void updateRecruit(RecruitBoard board) {
        recruitDAO.update(board);
    }

    // ✅ 모집글 삭제
    public void deleteRecruit(int bno) {
        recruitDAO.delete(bno);
    }

    // ✅ 팀장 여부 확인
    public boolean isClubLeader(String userId, int clubId) {
        return recruitDAO.isClubLeader(userId, clubId);
    }
}
