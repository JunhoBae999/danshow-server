package com.danshow.danshowserver.domain.video.repository;

import com.danshow.danshowserver.domain.video.post.VideoPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoPostRepository<T extends VideoPost> extends JpaRepository<T,Long> {

}
