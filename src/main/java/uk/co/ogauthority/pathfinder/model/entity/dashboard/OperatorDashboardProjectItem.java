package uk.co.ogauthority.pathfinder.model.entity.dashboard;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "operator_dashboard_items")
@Immutable
public class OperatorDashboardProjectItem extends DashboardProjectItem {
}
