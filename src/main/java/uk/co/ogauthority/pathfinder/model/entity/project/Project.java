package uk.co.ogauthority.pathfinder.model.entity.project;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "projects")
public class Project implements Serializable {

  private static final long serialVersionUID = -3615381727699431309L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "created_datetime")
  private Instant createdInstant;

  public Project() {
    this.createdInstant = Instant.now();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Instant getCreatedInstant() {
    return createdInstant;
  }

  public void setCreatedInstant(Instant createdDatetime) {
    this.createdInstant = createdDatetime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Project project = (Project) o;
    return Objects.equals(id, project.id)
        && Objects.equals(createdInstant, project.createdInstant);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, createdInstant);
  }
}
