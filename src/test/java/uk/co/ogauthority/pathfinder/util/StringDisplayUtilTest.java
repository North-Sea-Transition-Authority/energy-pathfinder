package uk.co.ogauthority.pathfinder.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StringDisplayUtilTest {
  private static final String TEST_STRING = "apple";

  @Test
  public void yesNoFromBoolean_nullIsEmptyString() {
    assertThat(StringDisplayUtil.yesNoFromBoolean(null)).isEqualTo("");
  }

  @Test
  public void yesNoFromBoolean_correctStringReturned_yes() {
    assertThat(StringDisplayUtil.yesNoFromBoolean(true)).isEqualTo(StringDisplayUtil.YES);
  }

  @Test
  public void yesNoFromBoolean_correctStringReturned_no() {
    assertThat(StringDisplayUtil.yesNoFromBoolean(false)).isEqualTo(StringDisplayUtil.NO);
  }

  @Test
  public void pluralise_singleItemNotPluralised() {
    assertThat(StringDisplayUtil.pluralise(TEST_STRING, 1)).isEqualTo(TEST_STRING);
  }

  @Test
  public void pluralise_multipleItemsPluralised() {
    assertThat(StringDisplayUtil.pluralise(TEST_STRING, 2)).isEqualTo(TEST_STRING+"s");
  }

  @Test
  public void getPrefixForVowelOrConsonant_startsWithVowel() {
    assertThat(StringDisplayUtil.getPrefixForVowelOrConsonant(TEST_STRING)).isEqualTo(StringDisplayUtil.AN);
  }

  @Test
  public void getPrefixForVowelOrConsonant_startsWithConsonant() {
    assertThat(StringDisplayUtil.getPrefixForVowelOrConsonant("Book")).isEqualTo(StringDisplayUtil.A);
  }
}
