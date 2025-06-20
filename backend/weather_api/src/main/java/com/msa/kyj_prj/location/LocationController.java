package com.msa.kyj_prj.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @PostMapping
    public Map<String, Object> addLocation(@RequestBody Location location) {
        locationService.insertLocation(location);
        return Map.of("result", "created");
    }

    @DeleteMapping("/{weather_location}")
    public Map<String, Object> deleteLocation(@PathVariable String weather_location) {
        locationService.deleteLocation(weather_location);
        return Map.of("result", "deleted");
    }
}
