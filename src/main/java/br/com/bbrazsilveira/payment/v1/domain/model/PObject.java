package br.com.bbrazsilveira.payment.v1.domain.model;

import br.com.bbrazsilveira.payment.v1.configuration.security.UserContext;
import br.com.bbrazsilveira.payment.v1.domain.model.conta.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class PObject {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Version
    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer version;

    @Column(columnDefinition = "timestamp default now()", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "timestamp default now()", nullable = false)
    private LocalDateTime modifiedAt;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", columnDefinition = "uuid default '9e5127c3-4219-44f0-805a-d132fe5cf461'", updatable = false, nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by", columnDefinition = "uuid default '9e5127c3-4219-44f0-805a-d132fe5cf461'", nullable = false)
    private User modifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    @Transient
    public boolean isDeleted() {
        return getDeletedBy() != null;
    }

    @PrePersist
    private void prePersist() {
        User currentUser = UserContext.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        modifiedAt = now;
        createdBy = createdBy == null ? currentUser : createdBy;
        modifiedBy = modifiedBy == null ? currentUser : modifiedBy;
        id = id == null ? UUID.randomUUID() : id;
    }

    @PreUpdate
    private void preUpdate() {
        modifiedAt = LocalDateTime.now();
        modifiedBy = modifiedBy == null ? UserContext.getCurrentUser() : modifiedBy;
    }
}
