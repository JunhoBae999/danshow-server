package com.danshow.danshowserver.domain.user;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "DTYPE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="user_id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(length = 20, nullable = false)
    private String name;

    @Column
    private String dType;

    public User(String email, String nickname, String name, String dType) {
       this.email = email;
       this.nickname = nickname;
       this.name = name;
       this.dType = dType;
    }

    @OneToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Dancer dancer;


}
