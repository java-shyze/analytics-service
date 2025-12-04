package com.analytics.controller;

import com.analytics.dto.LinkAnalyticsResponse;
import com.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Analytics API", description = "Получение аналитики по ссылкам")
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Operation(
        summary = "Получить аналитику по ссылке",
        description = "Возвращает полную аналитику: клики, браузеры, устройства, страны, временные ряды"
    )
    @GetMapping("/{alias}")
    public ResponseEntity<LinkAnalyticsResponse> getAnalytics(@PathVariable String alias) {
        LinkAnalyticsResponse analytics = analyticsService.getAnalytics(alias);
        return ResponseEntity.ok(analytics);
    }

    @Operation(
        summary = "Health check",
        description = "Проверка работоспособности сервиса"
    )
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Analytics service is running");
    }
}
