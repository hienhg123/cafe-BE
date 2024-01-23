package com.inn.cafe.com.inn.cafe.dao;

import com.inn.cafe.com.inn.cafe.model.Role;
import com.inn.cafe.com.inn.cafe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    @Query(value = "select u.email from User u where u.role ='ADMIN'")
    List<String> findAllAdminEmail();

    @Query(value = "update User u set u.status = :status where u.id  = :id")
    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") boolean status, @Param("id") Integer id);
}
