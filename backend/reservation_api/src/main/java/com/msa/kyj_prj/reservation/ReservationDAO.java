package com.msa.kyj_prj.reservation;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

// DB연동 SQL
@Mapper
public interface ReservationDAO {
	public Reservation getReservationForm(String svcid, String date, String userNo);
}
