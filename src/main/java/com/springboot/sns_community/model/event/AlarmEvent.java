package com.springboot.sns_community.model.event;

import com.springboot.sns_community.model.AlarmArgs;
import com.springboot.sns_community.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmEvent {
    private Integer receiveUserId;
    private AlarmType alarmType;
    private AlarmArgs args;
}
