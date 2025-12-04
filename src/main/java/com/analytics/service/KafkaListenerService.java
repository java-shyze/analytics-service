package com.analytics.service;

import com.analytics.dto.LinkClickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaListenerService.class);

    private final AnalyticsService analyticsService;

    public KafkaListenerService(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @KafkaListener(
            topics = "${kafka.topic.link-clicks}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(LinkClickEvent event) {
        log.info("Received Kafka click event: alias={}, ip={}", event.getAlias(), event.getIpAddress());

        analyticsService.saveClick(
                event.getLinkId(),
                event.getAlias(),
                event.getOriginalUrl(),
                event.getIpAddress(),
                event.getUserAgent(),
                event.getReferer()
        );
    }
}
