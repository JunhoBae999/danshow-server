package com.danshow.danshowserver.domain.video.repository;

import com.danshow.danshowserver.domain.video.Video;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoPostRepository<T extends Video> extends JpaRepository<VideoPost,Long> {
}
