package com.springboot.sns_community.service;

import com.springboot.sns_community.exception.ErrorCode;
import com.springboot.sns_community.exception.SnsApplicationException;
import com.springboot.sns_community.model.Alarm;
import com.springboot.sns_community.model.User;
import com.springboot.sns_community.model.entity.UserEntity;
import com.springboot.sns_community.repository.AlarmEntityRepository;
import com.springboot.sns_community.repository.UserCacheRepository;
import com.springboot.sns_community.repository.UserEntityRepository;
import com.springboot.sns_community.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService{

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;
    private final AlarmEntityRepository alarmRepository;
    private final UserCacheRepository userCacheRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName) {
        return userCacheRepository.getUser(userName).orElseGet(()->
                userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(()->
                        new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded",userName)))
        );
    }

    @Transactional
    public User join(String userName, String password){
        // 회원가입하려는 userName 체크
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated",userName));
        });

        // 회원가입 진행 -> 유저 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    public String login(String userName, String password){
        //회원가입 여부
        User user = loadUserByUserName(userName);
        userCacheRepository.setUser(user);

        //비밀번호 체크
        if (!encoder.matches(password,user.getPassword())){
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        //토큰 생성
        return JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);

    }

    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
            return alarmRepository.findALLByUserId(userId,pageable).map(Alarm::fromEntity);
    }
}
