package com.msa.kyj_prj.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationDAO locationDAO;

    public List<Location> getAllLocations() {
        return locationDAO.getAllLocations();
    }

    public void insertLocation(Location location) {
        locationDAO.insertLocation(location);
    }

    public void deleteLocation(String weather_location) {
        locationDAO.deleteLocation(weather_location);
    }
}
