package uk.co.ogauthority.pathfinder.model.auditrevisions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "audit_revisions")
@RevisionEntity
public class AuditRevision {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @RevisionNumber
  @Column(name = "rev")
  private Integer rev;

  @RevisionTimestamp
  @Column(name = "created_date")
  private Date createdDate;

  public Integer getId() {
    return rev;
  }

  public void setId(Integer rev) {
    this.rev = rev;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }
}
