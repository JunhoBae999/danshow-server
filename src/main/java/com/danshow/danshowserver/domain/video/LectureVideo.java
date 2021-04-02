package com.danshow.danshowserver.domain.video;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Getter
@Table(name = "lecture_video")
@Entity
@DiscriminatorValue("lecture")
public class LectureVideo extends Video{


}
