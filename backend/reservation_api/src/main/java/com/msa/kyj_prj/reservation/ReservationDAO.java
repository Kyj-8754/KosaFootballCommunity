package com.msa.kyj_prj.reservation;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.kyj_prj.dto.ReservationDTO;
import com.msa.kyj_prj.dto.SlotDTO;

// DB연동 SQL
@Mapper
public interface ReservationDAO {
	public List<SlotDTO> getReservationForm(@Param("svcid") String svcid, @Param("date") String date);
	public int reservation(Reservation reservation);
	public ReservationDTO getReservations(Long reservation_id);
	public List<ReservationDTO> getReservationList(String user_no);
}
