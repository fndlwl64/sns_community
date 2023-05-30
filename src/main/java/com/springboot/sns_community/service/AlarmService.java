package com.springboot.sns_community.service;

import com.springboot.sns_community.exception.ErrorCode;
import com.springboot.sns_community.exception.SnsApplicationException;
import com.springboot.sns_community.model.AlarmArgs;
import com.springboot.sns_community.model.AlarmType;
import com.springboot.sns_community.model.entity.AlarmEntity;
import com.springboot.sns_community.model.entity.UserEntity;
import com.springboot.sns_community.model.event.AlarmEvent;
import com.springboot.sns_community.repository.AlarmEntityRepository;
import com.springboot.sns_community.repository.EmitterRepository;
import com.springboot.sns_community.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public void send(AlarmType type, AlarmArgs arg, Integer receiverUserId){
        UserEntity user = userEntityRepository.findById(receiverUserId).orElseThrow(()-> new SnsApplicationException(ErrorCode.USER_NOT_FOUND));
        //alarm save
        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(user,type,arg));

        emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
            try{
                sseEmitter.send(SseEmitter.event().id(alarmEntity.getId().toString()).name(ALARM_NAME).data("new alarm"));
            }catch (IOException e){
                emitterRepository.delete(receiverUserId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }

    /*public void send(Integer alarmId,Integer userId){
        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try{
                sseEmitter.send(SseEmitter.event().id(alarmId.toString()).name(ALARM_NAME).data("new alarm"));
            }catch (IOException e){
                emitterRepository.delete(userId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }*/

    public SseEmitter connectAlarm(Integer userId){
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId,sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            sseEmitter.send((sseEmitter.event().id("").name(ALARM_NAME).data("connect completed")));
        } catch (IOException exception) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }
}
