package com.analytics.controller;

import com.analytics.dto.LinkAnalyticsResponse;
import com.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Tag(name = "Analytics API", description = "Аналитика по коротким ссылкам")
@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Operation(summary = "Полная аналитика по ссылке за период")
    @GetMapping("/{alias}")
    public ResponseEntity<LinkAnalyticsResponse> getAnalytics(
            @PathVariable String alias,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "false") boolean allTime) {

        ZonedDateTime startUtc = start != null ? start.atZone(ZoneOffset.UTC) : null;
        ZonedDateTime endUtc = end != null ? end.atZone(ZoneOffset.UTC) : null;

        LocalDateTime startLdt = startUtc != null ? startUtc.toLocalDateTime() : null;
        LocalDateTime endLdt = endUtc != null ? endUtc.toLocalDateTime() : null;

        LinkAnalyticsResponse analytics = analyticsService.getAnalytics(alias, startLdt, endLdt, allTime);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Analytics service is running");
    }
}
