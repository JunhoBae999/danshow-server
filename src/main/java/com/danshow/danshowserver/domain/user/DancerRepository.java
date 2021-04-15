package com.danshow.danshowserver.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DancerRepository extends JpaRepository<Dancer, Long> {
    Dancer findByEmail(String email);
}
