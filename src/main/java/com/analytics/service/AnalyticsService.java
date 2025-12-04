package com.analytics.service;

import com.analytics.dto.LinkAnalyticsResponse;
import com.analytics.dto.TimeSeriesData;
import com.analytics.model.LinkClick;
import com.analytics.repository.LinkClickRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AnalyticsService {

    @Autowired
    private LinkClickRepository clickRepository;

    @Autowired
    private UserAgentParserService userAgentParser;

    public LinkClick saveClick(Long linkId, String alias, String originalUrl,
                               String ipAddress, String userAgent, String referer) {
        LinkClick click = new LinkClick(linkId, alias);
        click.setOriginalUrl(originalUrl);
        click.setIpAddress(ipAddress);
        click.setIpHash(hashIp(ipAddress));
        click.setUserAgent(userAgent);
        click.setReferer(referer);

        Map<String, String> uaInfo = userAgentParser.parseUserAgent(userAgent);
        click.setBrowser(uaInfo.get("browser"));
        click.setBrowserVersion(uaInfo.get("browserVersion"));
        click.setOperatingSystem(uaInfo.get("operatingSystem"));
        click.setDeviceType(uaInfo.get("deviceType"));

        return clickRepository.save(click);
    }

    public LinkAnalyticsResponse getAnalytics(String alias, LocalDateTime start, LocalDateTime end, boolean allTime) {
        LinkAnalyticsResponse response = new LinkAnalyticsResponse();
        response.setAlias(alias);

        LocalDateTime actualStart;
        LocalDateTime actualEnd;

        if (allTime) {
            actualStart = LocalDateTime.of(1970, 1, 1, 0, 0);
            actualEnd = LocalDateTime.now().plusYears(10);
        } else {
            actualEnd = (end != null) ? end : LocalDateTime.now();
            actualStart = (start != null) ? start : actualEnd.minusDays(30);
        }

        LinkAnalyticsResponse.DateRange period = new LinkAnalyticsResponse.DateRange();
        period.setStart(actualStart);
        period.setEnd(actualEnd);
        response.setPeriod(period);

        long totalClicks = clickRepository.countByAliasInRange(alias, actualStart, actualEnd);
        response.setTotalClicks(totalClicks);

        long uniqueClicks = clickRepository.countUniqueByAliasInRange(alias, actualStart, actualEnd);
        response.setUniqueClicks(uniqueClicks);

        response.setBrowserStats(buildStats(clickRepository.getBrowserStatsInRange(alias, actualStart, actualEnd), totalClicks));
        response.setDeviceStats(buildStats(clickRepository.getDeviceStatsInRange(alias, actualStart, actualEnd), totalClicks));
        response.setTopReferrers(buildStats(clickRepository.getTopReferrersInRange(alias, actualStart, actualEnd), totalClicks));

        LinkAnalyticsResponse.GlobalStats globalStats = new LinkAnalyticsResponse.GlobalStats();
        globalStats.setData(convertToTimeSeries(clickRepository.getClicksByMonth(alias)));
        response.setGlobalStats(globalStats);

        return response;
    }

    private List<LinkAnalyticsResponse.StatItem> buildStats(List<Map<String, Object>> raw, long total) {
        return raw.stream()
            .map(row -> {
                String rawValue = row.entrySet().stream()
                    .filter(e -> !e.getKey().equals("count"))
                    .map(e -> Objects.toString(e.getValue(), null))
                    .findFirst()
                    .orElse(null);

                String name = normalizeName(rawValue);

                long count = ((Number) row.get("count")).longValue();
                double percent = total > 0 ? Math.round(count * 10000.0 / total) / 100.0 : 0.0;

                return new LinkAnalyticsResponse.StatItem(name, count, percent);
            })
            .sorted(Comparator.comparingLong((LinkAnalyticsResponse.StatItem s) -> s.getCount()).reversed())
            .toList();
    }

    private String normalizeName(String input) {
        if (input == null || input.isBlank()) {
            return "Direct";
        }

        String trimmed = input.trim();

        if (trimmed.contains("://") || trimmed.startsWith("www.")) {
            try {
                URI uri = new URI(trimmed);
                String host = uri.getHost();
                if (host != null) {
                    return host.startsWith("www.") ? host.substring(4) : host;
                }
            } catch (Exception e) {
            }
        }

        if ("Unknown".equalsIgnoreCase(trimmed)) {
            return "Other";
        }

        if ("direct".equalsIgnoreCase(trimmed)) {
            return "Direct";
        }

        return trimmed;
    }

    private List<TimeSeriesData> convertToTimeSeries(List<Map<String, Object>> stats) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return stats.stream()
                .map(row -> {
                    Object dateObj = row.entrySet().stream()
                            .filter(e -> !e.getKey().equals("count"))
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse(null);

                    long count = ((Number) row.get("count")).longValue();

                    String dateStr = "Unknown";
                    if (dateObj instanceof java.sql.Timestamp ts) {
                        dateStr = ts.toLocalDateTime().withDayOfMonth(1).format(formatter);
                    } else if (dateObj instanceof LocalDateTime ldt) {
                        dateStr = ldt.withDayOfMonth(1).format(formatter);
                    } else if (dateObj != null) {
                        dateStr = dateObj.toString().substring(0, 7);
                    }

                    return new TimeSeriesData(dateStr, count);
                })
                .sorted(Comparator.comparing(TimeSeriesData::getDate))
                .toList();
    }

    private String hashIp(String ip) {
        if (ip == null || ip.isBlank()) return "unknown";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return "error";
        }
    }
}
