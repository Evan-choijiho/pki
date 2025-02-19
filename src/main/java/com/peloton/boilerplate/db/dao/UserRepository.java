package com.peloton.boilerplate.db.dao;

import com.peloton.boilerplate.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findBySidAndDeleteTimeIsNull(Long userSid);
}
