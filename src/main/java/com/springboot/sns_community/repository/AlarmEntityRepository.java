
package com.springboot.sns_community.repository;

import com.springboot.sns_community.model.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface AlarmEntityRepository extends JpaRepository<AlarmEntity,Integer>{
    Page<AlarmEntity> findALLByUserId(Integer userId,Pageable pageable);
}
