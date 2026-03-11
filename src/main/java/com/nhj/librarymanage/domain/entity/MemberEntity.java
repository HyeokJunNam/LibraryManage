package com.nhj.librarymanage.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity(name = "member")
public class MemberEntity extends BaseEntity {

    @Id
    @Tsid
    private Long id;

    private String name;

    public void changeName(String name) {
        this.name = name;
    }

}
