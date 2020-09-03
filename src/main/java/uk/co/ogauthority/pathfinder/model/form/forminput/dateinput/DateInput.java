package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput;

import java.time.LocalDate;
import java.util.Optional;

public interface DateInput {

  DateInputType getType();

  String getDay();

  String getMonth();

  String getYear();

  Optional<LocalDate> createDate();

  LocalDate createDateOrNull();

  boolean isAfter(LocalDate testDate);

  boolean isBefore(LocalDate testDate);

  boolean isEqualTo(LocalDate testDate);
}
