package com.danshow.danshowserver.domain.crew;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.composite.MemberCrew;
import com.danshow.danshowserver.domain.user.Dancer;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
    private Long id;

    private String title;
    private String description;
    private String crew_profile_image;

    @OneToOne
    @JoinColumn(name ="user_id")
    private Dancer dancer;

    @OneToMany(mappedBy = "crew")
    private List<MemberCrew> memberList;

    public void addMember(MemberCrew member) {
        this.memberList.add(member);
        if(member.getCrew() != this) {
            member.setCrew(this);
        }
    }

    @Builder
    public Crew(String title, String description,String crew_profile_image, Dancer dancer) {
        this.title = title;
        this.description = description;
        this.crew_profile_image = crew_profile_image;
        this.dancer = dancer;
    }

}
