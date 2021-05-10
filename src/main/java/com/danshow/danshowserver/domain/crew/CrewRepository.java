package com.danshow.danshowserver.domain.crew;

import com.danshow.danshowserver.domain.user.Dancer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewRepository extends JpaRepository<Crew,Long> {
    Crew findByDancer(Dancer dancer);
}
