package com.linkbharat.linkbharatbackend.domain.entity;

import com.linkbharat.linkbharatbackend.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_admins", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"company_id", "user_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAdmin extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUser user;

    @Column(nullable = false)
    private String role = "OWNER"; // OWNER, ADMIN, EDITOR
}
