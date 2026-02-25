package com.linkbharat.linkbharatbackend.repository;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthor(AuthUser author, Pageable pageable);
    Page<Post> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByAuthorAndIsDeletedFalseOrderByCreatedAtDesc(AuthUser author, Pageable pageable);
}
