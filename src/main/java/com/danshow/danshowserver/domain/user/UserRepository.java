package com.danshow.danshowserver.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository<T extends User> extends JpaRepository<T, Long> {

    User findByEmail(String email);

}