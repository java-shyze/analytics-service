package com.analytics.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeoLocationService {

    private static final Logger log = LoggerFactory.getLogger(GeoLocationService.class);
    private DatabaseReader dbReader;

    @PostConstruct
    public void init() {
        try {
            // Путь к GeoLite2-City.mmdb базе данных
            // Скачать можно с https://dev.maxmind.com/geoip/geolite2-free-geolocation-data
            // Положить в src/main/resources/geoip/GeoLite2-City.mmdb
            
            File database = new File("src/main/resources/geoip/GeoLite2-City.mmdb");
            
            if (database.exists()) {
                dbReader = new DatabaseReader.Builder(database).build();
                log.info("GeoIP database loaded successfully");
            } else {
                log.warn("GeoIP database not found. Geolocation will return 'Unknown'");
            }
        } catch (Exception e) {
            log.error("Failed to initialize GeoIP database", e);
        }
    }

    /**
     * Определяет страну и город по IP адресу
     */
    public Map<String, String> getGeoLocation(String ipAddress) {
        Map<String, String> result = new HashMap<>();
        result.put("country", "Unknown");
        result.put("countryCode", "Unknown");
        result.put("city", "Unknown");

        if (ipAddress == null || ipAddress.isEmpty() || 
            ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
            result.put("country", "Local");
            result.put("countryCode", "LOCAL");
            result.put("city", "Local");
            return result;
        }

        if (dbReader == null) {
            return result;
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            CityResponse response = dbReader.city(inetAddress);

            Country country = response.getCountry();
            if (country != null) {
                result.put("country", country.getName() != null ? country.getName() : "Unknown");
                result.put("countryCode", country.getIsoCode() != null ? country.getIsoCode() : "Unknown");
            }

            City city = response.getCity();
            if (city != null && city.getName() != null) {
                result.put("city", city.getName());
            }

        } catch (Exception e) {
            log.warn("Failed to get geolocation for IP: {}", ipAddress);
        }

        return result;
    }

    /**
     * Простая версия - только страна
     */
    public String getCountryByIp(String ipAddress) {
        return getGeoLocation(ipAddress).get("country");
    }
}
