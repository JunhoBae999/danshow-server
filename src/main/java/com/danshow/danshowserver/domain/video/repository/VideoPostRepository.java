package com.danshow.danshowserver.domain.video.repository;

import com.danshow.danshowserver.domain.video.post.VideoPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoPostRepository<T extends VideoPost> extends JpaRepository<T,Long> {

    @Query("select v from VideoPost v where v.video.id =:id")
    VideoPost findByFileId(@Param("id") Long id);


}
