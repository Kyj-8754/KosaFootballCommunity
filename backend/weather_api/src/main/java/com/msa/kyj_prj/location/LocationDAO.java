package com.msa.kyj_prj.location;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LocationDAO {
    List<Location> getAllLocations();
}
