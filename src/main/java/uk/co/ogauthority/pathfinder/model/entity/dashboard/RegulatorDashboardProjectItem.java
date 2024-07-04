package uk.co.ogauthority.pathfinder.model.entity.dashboard;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "regulator_dashboard_items")
@Immutable
public class RegulatorDashboardProjectItem extends DashboardProjectItem {
}
