package uk.co.ogauthority.pathfinder.util;

import java.util.concurrent.Callable;

public class StringDisplayUtil {

  public static final String YES = "Yes";
  public static final String NO = "No";
  public static final String A = "a ";
  public static final String AN = "an ";
  public static final String NOT_SET_TEXT = "Not set";

  private StringDisplayUtil() {
    throw new IllegalStateException("StringDisplayUtil is a utility class and should not be instantiated");
  }

  /**
   * Get the result of the displayFunction or return the NOT_SET_TEXT string.
   * @param obj the object which displayFunction depends on
   * @param displayFunction the function of obj which returns the string we want to display
   * @return The result of displayFunction or NOT_SET_TEXT
   */
  public static String getValueOrDefault(Object obj, Callable<String> displayFunction) {
    try {
      return obj != null
          ? displayFunction.call()
          : NOT_SET_TEXT;
    } catch (Exception e) {
      return NOT_SET_TEXT;
    }
  }

  public static String yesNoFromBoolean(Boolean b) {
    if (b != null) {
      return b ? YES : NO;
    }
    return "";
  }

  /**
   * Appends "s" to the end of a string if count is not equal to 1.
   *
   * @param str   The string to pluralise.
   * @param count The number of occurrences.
   * @return The pluralised string.
   */
  public static String pluralise(String str, int count) {
    return count != 1 ? str + "s" : str;
  }

  /**
   * Get 'a ' or 'an ' depending on whether str begins with a vowel or a consonant.
   * @param str the String to find the prefix for
   * @return 'a ' or 'an ' depending on the first letter of str
   */
  public static String getPrefixForVowelOrConsonant(String str) {
    return isConsonant(str.toLowerCase().charAt(0)) ? A : AN;
  }

  public static String getValueAsStringOrNull(Integer value) {
    return (value != null) ? String.valueOf(value) : null;
  }

  private static boolean isConsonant(char c) {
    switch (c) {
      case 'a':
      case 'e':
      case 'i':
      case 'o':
      case 'u':
        return false;
      default:
        return true;
    }
  }
}
