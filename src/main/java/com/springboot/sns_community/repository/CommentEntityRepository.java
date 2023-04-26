
package com.springboot.sns_community.repository;

import com.springboot.sns_community.model.entity.CommentEntity;
import com.springboot.sns_community.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity,Integer> {
    Page<CommentEntity> findAllByPost(PostEntity postEntity , Pageable pageable);
}
