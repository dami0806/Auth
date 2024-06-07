package com.sparta.springauth.repository;

import com.sparta.springauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository를 상속 받는 UserRepository
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // email로 User을 찾기
    User findByEmail(String email);
    User findByUsername(String name);
}
