package com.analytics.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "link_clicks", indexes = {
    @Index(name = "idx_link_id", columnList = "linkId"),
    @Index(name = "idx_alias", columnList = "alias"),
    @Index(name = "idx_clicked_at", columnList = "clickedAt"),
    @Index(name = "idx_ip_hash", columnList = "ipHash")
})
public class LinkClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link_id", nullable = false)
    private Long linkId;

    @Column(name = "alias", nullable = false, length = 50)
    private String alias;

    @Column(name = "original_url", length = 2048)
    private String originalUrl;

    // IP информация
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "ip_hash", length = 64) // Хеш IP для подсчета уникальных
    private String ipHash;

    // Browser & Device информация
    @Column(name = "browser", length = 100)
    private String browser;

    @Column(name = "browser_version", length = 50)
    private String browserVersion;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "device_type", length = 50)
    private String deviceType; // DESKTOP, MOBILE, TABLET, etc.

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // Referrer
    @Column(name = "referer", length = 500)
    private String referer;

    // Временные метки
    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (clickedAt == null) {
            clickedAt = LocalDateTime.now();
        }
    }

    public LinkClick() {}

    public LinkClick(Long linkId, String alias) {
        this.linkId = linkId;
        this.alias = alias;
        this.clickedAt = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLinkId() { return linkId; }
    public void setLinkId(Long linkId) { this.linkId = linkId; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getIpHash() { return ipHash; }
    public void setIpHash(String ipHash) { this.ipHash = ipHash; }

    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }

    public String getBrowserVersion() { return browserVersion; }
    public void setBrowserVersion(String browserVersion) { this.browserVersion = browserVersion; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getReferer() { return referer; }
    public void setReferer(String referer) { this.referer = referer; }

    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
