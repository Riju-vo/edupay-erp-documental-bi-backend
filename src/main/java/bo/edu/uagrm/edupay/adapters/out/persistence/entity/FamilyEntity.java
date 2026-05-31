package bo.edu.uagrm.edupay.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_family")
@Getter
@Setter
public class FamilyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(name = "tutor_name", nullable = false)
    private String tutorName;

    @Column(name = "tutor_email", nullable = false)
    private String tutorEmail;

    @Column(nullable = false)
    private boolean active = true;
}
