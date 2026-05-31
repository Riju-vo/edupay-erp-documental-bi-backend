package bo.edu.uagrm.edupay.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "erp_employee")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String erpCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role; // e.g., "ADMIN", "SECRETARY"

    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        if (erpCode == null) {
            erpCode = "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getErpCode() { return erpCode; }
    public void setErpCode(String erpCode) { this.erpCode = erpCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
