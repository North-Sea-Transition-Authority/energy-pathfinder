package uk.co.ogauthority.pathfinder.model.entity.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "projects")
public class Project implements Serializable {

  private static final long serialVersionUID = -3615381727699431309L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "created_datetime")
  private Instant createdInstant;

  /**
   * One-to-many relationships like this should generally be avoided, this relationship exists purely to allow the
   * Criteria API to be able to find associated data, and should not be used anywhere else.
   */
  @OneToMany(mappedBy = "project")
  private List<ProjectDetail> projectDetails;

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
