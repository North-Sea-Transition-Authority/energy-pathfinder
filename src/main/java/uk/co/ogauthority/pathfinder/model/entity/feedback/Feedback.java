package uk.co.ogauthority.pathfinder.model.entity.feedback;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.enums.feedback.ServiceFeedbackRating;

@Entity
@Table(name = "service_feedback")
public class Feedback {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  private ServiceFeedbackRating rating;

  @Lob
  @Column(name = "feedback", columnDefinition = "CLOB")
  private String serviceFeedback;

  private Integer projectDetailId;

  private String submitterName;

  private String submitterEmailAddress;

  @Column(name = "submitted_datetime")
  private Instant submittedDate;

  public Integer getId() {
    return id;
  }

  public ServiceFeedbackRating getRating() {
    return rating;
  }

  public void setRating(ServiceFeedbackRating rating) {
    this.rating = rating;
  }

  public String getServiceFeedback() {
    return serviceFeedback;
  }

  public void setServiceFeedback(String feedback) {
    this.serviceFeedback = feedback;
  }

  public Integer getProjectDetailId() {
    return projectDetailId;
  }

  public void setProjectDetailId(Integer projectDetailId) {
    this.projectDetailId = projectDetailId;
  }

  public String getSubmitterName() {
    return submitterName;
  }

  public void setSubmitterName(String submitterName) {
    this.submitterName = submitterName;
  }

  public String getSubmitterEmailAddress() {
    return submitterEmailAddress;
  }

  public void setSubmitterEmailAddress(String submitterEmailAddress) {
    this.submitterEmailAddress = submitterEmailAddress;
  }

  public Instant getSubmittedDate() {
    return submittedDate;
  }

  public void setSubmittedDate(Instant submittedDate) {
    this.submittedDate = submittedDate;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Feedback)) {
      return false;
    }
    Feedback that = (Feedback) o;
    return Objects.equals(id, that.id)
        && rating == that.rating
        && Objects.equals(serviceFeedback, that.serviceFeedback)
        && Objects.equals(projectDetailId, that.projectDetailId)
        && Objects.equals(submitterName, that.submitterName)
        && Objects.equals(submitterEmailAddress, that.submitterEmailAddress)
        && Objects.equals(submittedDate, that.submittedDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        rating,
        serviceFeedback,
        projectDetailId,
        submitterName,
        submitterEmailAddress,
        submittedDate
    );
  }

  @Override
  public String toString() {
    return "Feedback {" +
        "id=" + id +
        ", rating=" + rating +
        ", serviceFeedback='" + serviceFeedback +
        ", projectDetailId=" + projectDetailId +
        ", submitterName=" + submitterName +
        ", submitterEmailAddress=" + submitterEmailAddress +
        ", submittedDate=" + submittedDate +
        '}';
  }
}
