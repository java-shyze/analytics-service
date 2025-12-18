package com.analytics.service;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserAgentParserService {

    /**
     * Парсит User-Agent строку и возвращает информацию о браузере, ОС и устройстве
     */
    public Map<String, String> parseUserAgent(String userAgentString) {
        Map<String, String> result = new HashMap<>();

        if (userAgentString == null || userAgentString.isEmpty()) {
            result.put("browser", "Unknown");
            result.put("browserVersion", "Unknown");
            result.put("operatingSystem", "Unknown");
            result.put("deviceType", "Unknown");
            return result;
        }

        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);

            Browser browser = userAgent.getBrowser();
            result.put("browser", browser.getName());
            result.put("browserVersion", userAgent.getBrowserVersion() != null 
                ? userAgent.getBrowserVersion().getVersion() 
                : "Unknown");

            OperatingSystem os = userAgent.getOperatingSystem();
            result.put("operatingSystem", os.getName());

            DeviceType deviceType = os.getDeviceType();
            result.put("deviceType", mapDeviceType(deviceType));

        } catch (Exception e) {
            result.put("browser", "Unknown");
            result.put("browserVersion", "Unknown");
            result.put("operatingSystem", "Unknown");
            result.put("deviceType", "Unknown");
        }

        return result;
    }

    /**
     * Маппинг типа устройства в удобный формат
     */
    private String mapDeviceType(DeviceType deviceType) {
        if (deviceType == null) {
            return "Other";
        }

        return switch (deviceType) {
            case COMPUTER -> "Desktop";
            case MOBILE -> "Mobile";
            case TABLET -> "Tablet";
            case DMR -> "Media Renderer";
            case GAME_CONSOLE -> "Game Console";
            case WEARABLE -> "Wearable";
            default -> "Other";
        };
    }
}
