package com.example.springboottests.functional.order.service;

import com.example.springboottests.functional.net.GeneralSender;
import com.example.springboottests.functional.order.model.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WmstkNotificationSender {

    private final GeneralSender generalSender;

    @Value("${url.wmstk-service}")
    private String wmstkUrl;

    /**
     * Asynchronously sends a notification to WMSTK-Service.
     *
     * @param orderDto The order DTO to be included in the notification.
     */
    @Async
    public void sendNotification(OrderDto orderDto) {
        generalSender.send(
                wmstkUrl,
                HttpMethod.POST,
                orderDto,
                httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON),
                Void.class
        );

        log.info("Notification to WMSTK has been sent successfully");
    }
}
