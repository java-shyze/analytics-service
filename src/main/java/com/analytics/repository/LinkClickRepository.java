package com.analytics.repository;

import com.analytics.model.LinkClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface LinkClickRepository extends JpaRepository<LinkClick, Long> {

    // Общее количество кликов по alias
    long countByAlias(String alias);

    // Уникальные клики (по IP hash)
    @Query("SELECT COUNT(DISTINCT lc.ipHash) FROM LinkClick lc WHERE lc.alias = :alias")
    long countUniqueClicksByAlias(@Param("alias") String alias);

    // Клики за период
    @Query("SELECT lc FROM LinkClick lc WHERE lc.alias = :alias " +
           "AND lc.clickedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY lc.clickedAt DESC")
    List<LinkClick> findClicksByAliasAndDateRange(
        @Param("alias") String alias,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // Статистика по браузерам
    @Query("SELECT lc.browser as browser, COUNT(lc) as count " +
           "FROM LinkClick lc WHERE lc.alias = :alias " +
           "GROUP BY lc.browser ORDER BY count DESC")
    List<Map<String, Object>> getBrowserStats(@Param("alias") String alias);

    // Статистика по устройствам
    @Query("SELECT lc.deviceType as deviceType, COUNT(lc) as count " +
           "FROM LinkClick lc WHERE lc.alias = :alias " +
           "GROUP BY lc.deviceType ORDER BY count DESC")
    List<Map<String, Object>> getDeviceStats(@Param("alias") String alias);

    // Статистика по странам
    @Query("SELECT lc.country as country, lc.countryCode as countryCode, COUNT(lc) as count " +
           "FROM LinkClick lc WHERE lc.alias = :alias AND lc.country IS NOT NULL " +
           "GROUP BY lc.country, lc.countryCode ORDER BY count DESC")
    List<Map<String, Object>> getCountryStats(@Param("alias") String alias);

    // Статистика кликов по месяцам
    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', lc.clickedAt) as month, COUNT(lc) as count " +
           "FROM LinkClick lc WHERE lc.alias = :alias " +
           "GROUP BY FUNCTION('DATE_TRUNC', 'month', lc.clickedAt) " +
           "ORDER BY month DESC")
    List<Map<String, Object>> getClicksByMonth(@Param("alias") String alias);

    // Статистика кликов по дням (последние N дней)
    @Query("SELECT FUNCTION('DATE', lc.clickedAt) as date, COUNT(lc) as count " +
           "FROM LinkClick lc WHERE lc.alias = :alias " +
           "AND lc.clickedAt >= :startDate " +
           "GROUP BY FUNCTION('DATE', lc.clickedAt) " +
           "ORDER BY date DESC")
    List<Map<String, Object>> getClicksByDay(
        @Param("alias") String alias,
        @Param("startDate") LocalDateTime startDate
    );

    // Топ referrers
    @Query("SELECT lc.referer as referer, COUNT(lc) as count " +
           "FROM LinkClick lc WHERE lc.alias = :alias AND lc.referer IS NOT NULL " +
           "GROUP BY lc.referer ORDER BY count DESC")
    List<Map<String, Object>> getTopReferrers(@Param("alias") String alias);
}
