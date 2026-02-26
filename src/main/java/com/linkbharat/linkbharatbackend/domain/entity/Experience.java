package com.linkbharat.linkbharatbackend.domain.entity;

import com.linkbharat.linkbharatbackend.audit.BaseEntity;
import com.linkbharat.linkbharatbackend.domain.enums.EmploymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Experience extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long experienceId;
    private String title;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String locationType;
    private String description;

    @ElementCollection
    private Set<String> top5Skills = new HashSet<>();
    private String mediaUrl;
    private boolean isCurrentJob;

    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;
}
