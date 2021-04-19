package com.danshow.danshowserver.domain.video;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository<T extends Video> extends JpaRepository<T,Long> {
}
