package uk.co.ogauthority.pathfinder.model.entity.project;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "projects")
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private LocalDateTime createdDatetime;

  public Project() {
    this.createdDatetime = LocalDateTime.now();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public LocalDateTime getCreatedDatetime() {
    return createdDatetime;
  }

  public void setCreatedDatetime(LocalDateTime createdDatetime) {
    this.createdDatetime = createdDatetime;
  }
}
