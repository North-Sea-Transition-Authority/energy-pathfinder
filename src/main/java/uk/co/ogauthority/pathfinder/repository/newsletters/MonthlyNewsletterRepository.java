package uk.co.ogauthority.pathfinder.repository.newsletters;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.newsletters.MonthlyNewsletter;

@Repository
public interface MonthlyNewsletterRepository extends CrudRepository<MonthlyNewsletter, Integer> {
}
