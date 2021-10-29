package uk.co.ogauthority.pathfinder.repository.feedback;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.feedback.Feedback;

@Repository
public interface FeedbackRepository extends CrudRepository<Feedback, Integer> {
}
