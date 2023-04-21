package com.springboot.sns_community.repository;

import com.springboot.sns_community.model.entity.PostEntity;
import com.springboot.sns_community.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity,Integer> {
    Page<PostEntity> findAllByUser(UserEntity user, Pageable pageable);
}
