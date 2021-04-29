package com.danshow.danshowserver.domain.composite;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class MemberCrewId implements Serializable {
    private String member;
    private String crew;

    public MemberCrewId(String member, String crew) {
        this.member = member;
        this.crew = crew;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        MemberCrewId memberCrewId = (MemberCrewId) obj;
        return (this.getMember().equals(memberCrewId.getMember()) && this.getCrew().equals(memberCrewId.getCrew()));
    }

    @Override
    public int hashCode() {
        int h = 0;
        String concat = member.concat(getCrew());
        if(concat.length() > 0) {
            for(char ch : concat.toCharArray()) {
                h = 31 * h + ch;
            }
        }
        return h;
    }
}
