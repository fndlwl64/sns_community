package com.springboot.sns_community.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmArgs {
    //알람을 발생시키는 사람
    private Integer fromUserId;
    private Integer targetId;
}

