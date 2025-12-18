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

       long countByAlias(String alias);

       @Query("SELECT COUNT(DISTINCT lc.ipHash) FROM LinkClick lc WHERE lc.alias = :alias")
       long countUniqueClicksByAlias(@Param("alias") String alias);

       @Query("SELECT lc FROM LinkClick lc WHERE lc.alias = :alias " +
              "AND lc.clickedAt BETWEEN :startDate AND :endDate " +
              "ORDER BY lc.clickedAt DESC")
       List<LinkClick> findClicksByAliasAndDateRange(
              @Param("alias") String alias,
              @Param("startDate") LocalDateTime startDate,
              @Param("endDate") LocalDateTime endDate
       );

       @Query("SELECT lc.browser as browser, COUNT(lc) as count " +
              "FROM LinkClick lc WHERE lc.alias = :alias " +
              "GROUP BY lc.browser ORDER BY count DESC")
       List<Map<String, Object>> getBrowserStats(@Param("alias") String alias);

       @Query("SELECT lc.deviceType as deviceType, COUNT(lc) as count " +
              "FROM LinkClick lc WHERE lc.alias = :alias " +
              "GROUP BY lc.deviceType ORDER BY count DESC")
       List<Map<String, Object>> getDeviceStats(@Param("alias") String alias);

       @Query("SELECT FUNCTION('DATE_TRUNC', 'month', lc.clickedAt) as month, COUNT(lc) as count " +
              "FROM LinkClick lc WHERE lc.alias = :alias " +
              "GROUP BY FUNCTION('DATE_TRUNC', 'month', lc.clickedAt) " +
              "ORDER BY month DESC")
       List<Map<String, Object>> getClicksByMonth(@Param("alias") String alias);

       @Query("SELECT FUNCTION('DATE', lc.clickedAt) as date, COUNT(lc) as count " +
              "FROM LinkClick lc WHERE lc.alias = :alias " +
              "AND lc.clickedAt >= :startDate " +
              "GROUP BY FUNCTION('DATE', lc.clickedAt) " +
              "ORDER BY date DESC")
       List<Map<String, Object>> getClicksByDay(
              @Param("alias") String alias,
              @Param("startDate") LocalDateTime startDate
       );

       @Query("SELECT lc.referer as referer, COUNT(lc) as count " +
              "FROM LinkClick lc WHERE lc.alias = :alias AND lc.referer IS NOT NULL " +
              "GROUP BY lc.referer ORDER BY count DESC")
       List<Map<String, Object>> getTopReferrers(@Param("alias") String alias);


       @Query("SELECT COUNT(lc) FROM LinkClick lc WHERE lc.alias = :alias AND lc.clickedAt BETWEEN :start AND :end")
       long countByAliasInRange(@Param("alias") String alias, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
       
       @Query("SELECT COUNT(DISTINCT lc.ipHash) FROM LinkClick lc WHERE lc.alias = :alias AND lc.clickedAt BETWEEN :start AND :end")
       long countUniqueByAliasInRange(@Param("alias") String alias, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

       @Query("SELECT lc.browser AS browser, COUNT(lc) AS count FROM LinkClick lc WHERE lc.alias = :alias AND lc.clickedAt BETWEEN :start AND :end GROUP BY lc.browser ORDER BY count DESC")
       List<Map<String, Object>> getBrowserStatsInRange(@Param("alias") String alias, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

       @Query("SELECT lc.deviceType AS deviceType, COUNT(lc) AS count FROM LinkClick lc WHERE lc.alias = :alias AND lc.clickedAt BETWEEN :start AND :end GROUP BY lc.deviceType ORDER BY count DESC")
       List<Map<String, Object>> getDeviceStatsInRange(@Param("alias") String alias, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

       @Query("SELECT COALESCE(lc.referer, 'direct') AS referer, COUNT(lc) AS count FROM LinkClick lc WHERE lc.alias = :alias AND lc.clickedAt BETWEEN :start AND :end GROUP BY COALESCE(lc.referer, 'direct') ORDER BY count DESC LIMIT 10")
       List<Map<String, Object>> getTopReferrersInRange(@Param("alias") String alias, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

       @Query("SELECT FUNCTION('DATE_TRUNC', 'hour', lc.clickedAt) AS hour, COUNT(lc) AS count FROM LinkClick lc WHERE lc.alias = :alias AND lc.clickedAt BETWEEN :start AND :end GROUP BY hour ORDER BY hour")
       List<Map<String, Object>> getClicksByHour(@Param("alias") String alias, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

       @Query("SELECT FUNCTION('DATE', lc.clickedAt) AS date, COUNT(lc) AS count FROM LinkClick lc WHERE lc.alias = :alias AND lc.clickedAt BETWEEN :start AND :end GROUP BY date ORDER BY date")
       List<Map<String, Object>> getClicksByDay(@Param("alias") String alias, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

       @Query("SELECT FUNCTION('DATE_TRUNC', 'month', lc.clickedAt) AS month, COUNT(lc) AS count FROM LinkClick lc WHERE lc.alias = :alias AND lc.clickedAt BETWEEN :start AND :end GROUP BY month ORDER BY month")
       List<Map<String, Object>> getClicksByMonthInRange(@Param("alias") String alias, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
