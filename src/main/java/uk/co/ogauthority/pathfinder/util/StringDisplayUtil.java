package uk.co.ogauthority.pathfinder.util;

public class StringDisplayUtil {

  private StringDisplayUtil() {
    throw new IllegalStateException("StringDisplayUtil is a utility class and should not be instantiated");
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
    return isConsonant(str.toLowerCase().charAt(0)) ? "a " : "an ";
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
