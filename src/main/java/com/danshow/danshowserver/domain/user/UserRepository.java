package com.danshow.danshowserver.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    //TODO :유저 리퍼지토리를 멤버, 댄서가 상속받게 만들기

    User findByEmail(String email);
}