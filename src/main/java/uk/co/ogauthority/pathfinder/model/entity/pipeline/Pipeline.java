package uk.co.ogauthority.pathfinder.model.entity.pipeline;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity
@Table(name = "pipelines")
@Immutable
public class Pipeline implements SearchSelectable {

  @Id
  private Integer id;

  private String name;

  private Boolean historicStatus;

  public Pipeline() {}

  @VisibleForTesting
  public Pipeline(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Boolean getIsHistoricStatus() {
    return historicStatus;
  }

  public void setIsHistoricStatus(Boolean historicStatus) {
    this.historicStatus = historicStatus;
  }

  @Override
  public String getSelectionId() {
    return String.valueOf(getId());
  }

  @Override
  public String getSelectionText() {
    return getName();
  }
}
