package com.linkbharat.linkbharatbackend.domain.entity;

import com.linkbharat.linkbharatbackend.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProfileId;
    private String name;
    private String headLine;
    private String avatarUrl;
    private String coverageImageUrl;
    private String location;
    private int connections;
    private String about;
    @ElementCollection
    private Set<String> skills = new HashSet<>();
    private boolean isConnected;
    private boolean isPending;

    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Education> educations = new HashSet<>();

    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Experience> experiences = new HashSet<>();
}
