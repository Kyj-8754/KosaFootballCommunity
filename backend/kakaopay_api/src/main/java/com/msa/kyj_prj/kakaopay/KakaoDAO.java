package com.msa.kyj_prj.kakaopay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
	// 결제 승인
	public int paymentApprove(PaymentDTO payment);

	// 결제 승인 업데이트
	public int updatePayment(PaymentDTO updated);

}
