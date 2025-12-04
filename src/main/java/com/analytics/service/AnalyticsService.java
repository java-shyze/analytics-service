package com.analytics.service;

import com.analytics.dto.LinkAnalyticsResponse;
import com.analytics.dto.TimeSeriesData;
import com.analytics.model.LinkClick;
import com.analytics.repository.LinkClickRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private LinkClickRepository clickRepository;

    @Autowired
    private UserAgentParserService userAgentParser;

    @Autowired
    private GeoLocationService geoLocationService;

    /**
     * Сохраняет клик с полной аналитической информацией
     */
    public LinkClick saveClick(Long linkId, String alias, String originalUrl,
                               String ipAddress, String userAgent, String referer) {
        LinkClick click = new LinkClick(linkId, alias);
        click.setOriginalUrl(originalUrl);
        click.setIpAddress(ipAddress);
        click.setIpHash(hashIp(ipAddress));
        click.setUserAgent(userAgent);
        click.setReferer(referer);

        // Парсим User-Agent
        Map<String, String> uaInfo = userAgentParser.parseUserAgent(userAgent);
        click.setBrowser(uaInfo.get("browser"));
        click.setBrowserVersion(uaInfo.get("browserVersion"));
        click.setOperatingSystem(uaInfo.get("operatingSystem"));
        click.setDeviceType(uaInfo.get("deviceType"));

        // Определяем геолокацию
        Map<String, String> geoInfo = geoLocationService.getGeoLocation(ipAddress);
        click.setCountry(geoInfo.get("country"));
        click.setCountryCode(geoInfo.get("countryCode"));
        click.setCity(geoInfo.get("city"));

        return clickRepository.save(click);
    }

    /**
     * Получает полную аналитику по alias
     */
    public LinkAnalyticsResponse getAnalytics(String alias) {
        LinkAnalyticsResponse response = new LinkAnalyticsResponse();
        response.setAlias(alias);

        // Общее количество кликов
        long totalClicks = clickRepository.countByAlias(alias);
        response.setTotalClicks(totalClicks);

        // Уникальные клики
        long uniqueClicks = clickRepository.countUniqueClicksByAlias(alias);
        response.setUniqueClicks(uniqueClicks);

        // Статистика по браузерам
        List<Map<String, Object>> browserStats = clickRepository.getBrowserStats(alias);
        response.setBrowserStats(convertToStats(browserStats));

        // Статистика по устройствам
        List<Map<String, Object>> deviceStats = clickRepository.getDeviceStats(alias);
        response.setDeviceStats(convertToStats(deviceStats));

        // Статистика по странам
        List<Map<String, Object>> countryStats = clickRepository.getCountryStats(alias);
        response.setCountryStats(convertToCountryStats(countryStats));

        // Клики по месяцам
        List<Map<String, Object>> monthlyStats = clickRepository.getClicksByMonth(alias);
        response.setMonthlyStats(convertToTimeSeries(monthlyStats));

        // Клики по дням (последние 30 дней)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Map<String, Object>> dailyStats = clickRepository.getClicksByDay(alias, thirtyDaysAgo);
        response.setDailyStats(convertToTimeSeries(dailyStats));

        // Топ referrers
        List<Map<String, Object>> topReferrers = clickRepository.getTopReferrers(alias);
        response.setTopReferrers(convertToStats(topReferrers));

        return response;
    }

    /**
     * Хеширование IP для подсчета уникальных пользователей (GDPR friendly)
     */
    private String hashIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * Конвертация статистики в удобный формат
     */
    private Map<String, Long> convertToStats(List<Map<String, Object>> stats) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Map<String, Object> stat : stats) {
            String key = stat.keySet().stream()
                .filter(k -> !k.equals("count"))
                .findFirst()
                .orElse("unknown");
            
            Object value = stat.get(key);
            Long count = ((Number) stat.get("count")).longValue();
            
            String label = value != null ? value.toString() : "Unknown";
            result.put(label, count);
        }
        return result;
    }

    /**
     * Конвертация статистики по странам
     */
    private Map<String, Map<String, Object>> convertToCountryStats(List<Map<String, Object>> stats) {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        for (Map<String, Object> stat : stats) {
            String country = stat.get("country") != null ? stat.get("country").toString() : "Unknown";
            String countryCode = stat.get("countryCode") != null ? stat.get("countryCode").toString() : "XX";
            Long count = ((Number) stat.get("count")).longValue();
            
            Map<String, Object> countryData = new HashMap<>();
            countryData.put("code", countryCode);
            countryData.put("count", count);
            
            result.put(country, countryData);
        }
        return result;
    }

    /**
     * Конвертация временных рядов
     */
    private List<TimeSeriesData> convertToTimeSeries(List<Map<String, Object>> stats) {
        return stats.stream()
            .map(stat -> {
                Object dateObj = stat.keySet().stream()
                    .filter(k -> !k.equals("count"))
                    .map(stat::get)
                    .findFirst()
                    .orElse(null);
                
                Long count = ((Number) stat.get("count")).longValue();
                
                String dateStr = dateObj != null ? dateObj.toString() : "Unknown";
                return new TimeSeriesData(dateStr, count);
            })
            .collect(Collectors.toList());
    }
}
