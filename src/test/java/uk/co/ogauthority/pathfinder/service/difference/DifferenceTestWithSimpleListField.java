package uk.co.ogauthority.pathfinder.service.difference;

import java.util.Collections;
import java.util.List;

public class DifferenceTestWithSimpleListField {

  private List<String> stringList;

  private List<Integer> integerList;

  public DifferenceTestWithSimpleListField() {
    stringList = Collections.emptyList();
    integerList = Collections.emptyList();
  }

  public DifferenceTestWithSimpleListField(List<String> stringList, List<Integer> integerList) {
    this.stringList = stringList;
    this.integerList = integerList;
  }

  public List<String> getStringList() {
    return stringList;
  }

  public void setStringList(List<String> stringList) {
    this.stringList = stringList;
  }

  public List<Integer> getIntegerList() {
    return integerList;
  }

  public void setIntegerList(List<Integer> integerList) {
    this.integerList = integerList;
  }
}
