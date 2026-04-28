package com.rescue.controller;

import com.rescue.dto.ApiResponse;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/routes")
public class RoutesController {

    @Data
    public static class SavedRoute {
        private String routeId;
        private String name;
        private CoordPoint origin;
        private CoordPoint destination;
    }

    @Data
    public static class CoordPoint {
        private String address;
        private String lon;
        private String lat;
    }

    private final Map<String, SavedRoute> store = new ConcurrentHashMap<>();

    @GetMapping("/list")
    public ApiResponse<List<SavedRoute>> list() {
        return ApiResponse.ok(new ArrayList<>(store.values()));
    }

    @PostMapping("/save")
    public ApiResponse<Map<String, String>> save(@RequestBody Map<String, Object> body) {
        String routeId = "R_" + System.currentTimeMillis();
        SavedRoute route = new SavedRoute();
        route.setRouteId(routeId);
        route.setName((String) body.get("name"));

        @SuppressWarnings("unchecked")
        Map<String, String> orig = (Map<String, String>) body.get("origin");
        @SuppressWarnings("unchecked")
        Map<String, String> dest = (Map<String, String>) body.get("destination");

        if (orig != null) {
            CoordPoint o = new CoordPoint();
            o.setAddress(orig.get("address")); o.setLon(orig.get("lon")); o.setLat(orig.get("lat"));
            route.setOrigin(o);
        }
        if (dest != null) {
            CoordPoint d = new CoordPoint();
            d.setAddress(dest.get("address")); d.setLon(dest.get("lon")); d.setLat(dest.get("lat"));
            route.setDestination(d);
        }

        store.put(routeId, route);
        return ApiResponse.ok(Map.of("routeId", routeId));
    }

    @DeleteMapping("/{routeId}")
    public ApiResponse<Void> delete(@PathVariable String routeId) {
        store.remove(routeId);
        return ApiResponse.ok(null);
    }
}
