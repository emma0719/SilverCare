package com.silvercare.silvercarebackend.repository;

import com.silvercare.silvercarebackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 按用户名查询用户
    Optional<User> findByUsername(String username);

    // 按邮箱查询用户
    Optional<User> findByEmail(String email);

    // 检查用户名是否存在（注册时验证）
    boolean existsByUsername(String username);

    // 检查邮箱是否存在（注册时验证）
    boolean existsByEmail(String email);
}
