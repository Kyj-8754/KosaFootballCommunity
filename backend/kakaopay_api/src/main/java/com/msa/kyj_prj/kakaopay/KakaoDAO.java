package com.msa.kyj_prj.kakaopay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.kyj_prj.dto.PaymentDTO;

@Mapper
public interface KakaoDAO {
	// 결제 준비 전 결제 판단 여부용
	public String findReservationId(PaymentDTO param);
	// 결제 준비 TID x
	public void paymentReady(PaymentDTO payment);
	// 결제 준비 TID o
	public void updateTid(@Param("id") Long id,@Param("tid") String tid);
	// ID를 통한 데이터 파싱
	public PaymentDTO findById(Long id);
	// 결제 결과 업데이트용
	public int updatePayment(PaymentDTO updated);

}
