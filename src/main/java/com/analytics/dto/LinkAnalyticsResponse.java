package com.analytics.dto;

import java.util.List;
import java.util.Map;

public class LinkAnalyticsResponse {
    
    private String alias;
    private long totalClicks;
    private long uniqueClicks;
    
    // Статистика по браузерам: {"Chrome": 150, "Firefox": 80, ...}
    private Map<String, Long> browserStats;
    
    // Статистика по устройствам: {"Desktop": 200, "Mobile": 50, ...}
    private Map<String, Long> deviceStats;
    
    // Статистика по странам: {"United States": {"code": "US", "count": 100}, ...}
    private Map<String, Map<String, Object>> countryStats;
    
    // Клики по месяцам для графика
    private List<TimeSeriesData> monthlyStats;
    
    // Клики по дням (последние 30 дней) для графика
    private List<TimeSeriesData> dailyStats;
    
    // Топ источников трафика
    private Map<String, Long> topReferrers;

    // Constructors
    public LinkAnalyticsResponse() {}

    // Getters and Setters
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public long getTotalClicks() { return totalClicks; }
    public void setTotalClicks(long totalClicks) { this.totalClicks = totalClicks; }

    public long getUniqueClicks() { return uniqueClicks; }
    public void setUniqueClicks(long uniqueClicks) { this.uniqueClicks = uniqueClicks; }

    public Map<String, Long> getBrowserStats() { return browserStats; }
    public void setBrowserStats(Map<String, Long> browserStats) { this.browserStats = browserStats; }

    public Map<String, Long> getDeviceStats() { return deviceStats; }
    public void setDeviceStats(Map<String, Long> deviceStats) { this.deviceStats = deviceStats; }

    public Map<String, Map<String, Object>> getCountryStats() { return countryStats; }
    public void setCountryStats(Map<String, Map<String, Object>> countryStats) { 
        this.countryStats = countryStats; 
    }

    public List<TimeSeriesData> getMonthlyStats() { return monthlyStats; }
    public void setMonthlyStats(List<TimeSeriesData> monthlyStats) { 
        this.monthlyStats = monthlyStats; 
    }

    public List<TimeSeriesData> getDailyStats() { return dailyStats; }
    public void setDailyStats(List<TimeSeriesData> dailyStats) { 
        this.dailyStats = dailyStats; 
    }

    public Map<String, Long> getTopReferrers() { return topReferrers; }
    public void setTopReferrers(Map<String, Long> topReferrers) { 
        this.topReferrers = topReferrers; 
    }
}
