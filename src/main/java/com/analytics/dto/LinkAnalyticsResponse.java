package com.analytics.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinkAnalyticsResponse {
    private String alias;

    private DateRange period;

    private long totalClicks;
    private long uniqueClicks;

    private List<StatItem> browserStats = new ArrayList<>();
    private List<StatItem> deviceStats = new ArrayList<>();
    private List<StatItem> topReferrers = new ArrayList<>();

    private GlobalStats globalStats;    

    public static class GlobalStats {
        private String grouping = "month";
        private List<TimeSeriesData> data = new ArrayList<>();

        public String getGrouping() { return grouping; }
        public void setGrouping(String grouping) { this.grouping = grouping; }

        public List<TimeSeriesData> getData() { return data; }
        public void setData(List<TimeSeriesData> data) { this.data = data; }
    }
    
    public static class DateRange {
        private LocalDateTime start;
        private LocalDateTime end;

        public LocalDateTime getStart() { return start; }
        public void setStart(LocalDateTime start) { this.start = start; }
        public LocalDateTime getEnd() { return end; }
        public void setEnd(LocalDateTime end) { this.end = end; }
    }

    public static class StatItem {
        private String name;
        private long count;
        private double percent;

        public StatItem(String name, long count, double percent) {
            this.name = name;
            this.count = count;
            this.percent = percent;
        }

        public StatItem() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }

        public double getPercent() { return percent; }
        public void setPercent(double percent) { this.percent = percent; }
    }

    public static class TimeSeries {
        private String grouping;
        private java.util.List<TimeSeriesData> data = new java.util.ArrayList<>();

        public String getGrouping() { return grouping; }
        public void setGrouping(String grouping) { this.grouping = grouping; }
        public java.util.List<TimeSeriesData> getData() { return data; }
        public void setData(java.util.List<TimeSeriesData> data) { this.data = data; }
    }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public DateRange getPeriod() { return period; }
    public void setPeriod(DateRange period) { this.period = period; }

    public long getTotalClicks() { return totalClicks; }
    public void setTotalClicks(long totalClicks) { this.totalClicks = totalClicks; }

    public long getUniqueClicks() { return uniqueClicks; }
    public void setUniqueClicks(long uniqueClicks) { this.uniqueClicks = uniqueClicks; }

    public List<StatItem> getBrowserStats() { return browserStats; }
    public void setBrowserStats(List<StatItem> browserStats) { this.browserStats = browserStats; }

    public List<StatItem> getDeviceStats() { return deviceStats; }
    public void setDeviceStats(List<StatItem> deviceStats) { this.deviceStats = deviceStats; }

    public List<StatItem> getTopReferrers() { return topReferrers; }
    public void setTopReferrers(List<StatItem> topReferrers) { this.topReferrers = topReferrers; }

    public GlobalStats getGlobalStats() { return globalStats; }
    public void setGlobalStats(GlobalStats globalStats) { this.globalStats = globalStats; }
}
