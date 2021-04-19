package com.danshow.danshowserver.domain.video;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<AttachFile,Long> {
}
