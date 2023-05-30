package com.springboot.sns_community.consumer;

import com.springboot.sns_community.model.event.AlarmEvent;
import com.springboot.sns_community.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmConsumer {

    private final AlarmService alarmService;

    public void consumeAlarm(AlarmEvent event, Acknowledgment ack){
        log.info("Consume the event {}", event);
        alarmService.send(event.getAlarmType(),event.getArgs(),event.getReceiveUserId());
        ack.acknowledge();
    }
}
