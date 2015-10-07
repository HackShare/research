package HADS.Generator;

import java.util.*;

public class Statics {
  private static final long startTime = System.currentTimeMillis();

  public static final long getStartTime() { return startTime; }

  public static final long age() {
    return System.currentTimeMillis() - startTime;
  }

  public static int[] arrayCopyOf(int[] a) {
    return java.util.Arrays.copyOf(a, a.length);
  }

  public static int[][] arrayCopyOf(int[][] a) {
    int[][] theCopy = new int[a.length][];
    for (int i = 0; i < a.length; i++) {
      theCopy[i] = java.util.Arrays.copyOf(a[i], a[i].length);
    }
    return theCopy;
  }

  public static String[] arrayCopyOf(String[] a) {
    return java.util.Arrays.copyOf(a, a.length);
  }

  public static String[][] arrayCopyOf(String[][] a) {
    String[][] theCopy = new String[a.length][];
    for (int i = 0; i < a.length; i++) {
      theCopy[i] = java.util.Arrays.copyOf(a[i], a[i].length);
    }
    return theCopy;
  }

  public static int[] aZeroFilledArray(int n) {
    int[] result = new int[n];
    java.util.Arrays.fill(result, 0);
    return result;
  }

  public static boolean allNonZero(int[] anArray) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] == 0) return false;
    }
    return true;
  }

  public static boolean allZeros(int[] anArray) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] != 0) return false;
    }
    return true;
  }

  public static boolean allPositive(int[] anArray) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] <= 0) return false;
    }
    return true;
  }

  public static boolean allNegative(int[] anArray) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] >= 0) return false;
    }
    return true;
  }

  public static boolean allGrEqZero(int[] anArray) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] < 0) return false;
    }
    return true;
  }

  public static boolean allLeEqZero(int[] anArray) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] > 0) return false;
    }
    return true;
  }

  public static boolean anyNegative(int[] anArray) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] < 0) return true;
    }
    return false;
  }

  public static boolean anyPositive(int[] anArray) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] > 0) return true;
    }
    return false;
  }

  public static boolean allHundredOrMore(int[] anArray) {
    return allThreshholdOrMore(anArray, 100);
//  for (int i = 0; i < anArray.length; i++) {
//    if (anArray[i] < 100) return false;
//  }
//  return true;
  }

  public static boolean allThreshholdOrMore(int[] anArray, int threshhold) {
    for (int i = 0; i < anArray.length; i++) {
      if (anArray[i] < threshhold) return false;
    }
    return true;
  }

  public static String arrayToString(int[] a) {
    StringBuffer b = new StringBuffer();
    b.append('{');
    for (int i = 0; i < a.length; i++) {
      b.append(" " + a[i]);
    }
    b.append(' ');
    b.append('}');
    return b.toString();
  }

  public static String arrayToString(int[][] a) {
    StringBuffer b = new StringBuffer();
    b.append('{');
    b.append(' ');
    for (int i = 0; i < a.length; i++) {
      b.append('{');
      for (int j = 0; j < a[i].length; j++) {
        b.append(" " + a[i][j]);
      }
      b.append(' ');
      b.append('}');
    }
    b.append(' ');
    b.append('}');
    return b.toString();
  }

  public static String arrayToString(String[] a) {
    StringBuffer b = new StringBuffer();
    b.append('{');
    for (int i = 0; i < a.length; i++) {
      b.append(" " + a[i]);
    }
    b.append(' ');
    b.append('}');
    return b.toString();
  }

  public static <T> String listToString(List<T> a) {
    StringBuffer b = new StringBuffer();
    b.append('{');
    for (int i = 0; i < a.size(); i++) {
      b.append(" " + a.get(i));
    }
    b.append(' ');
    b.append('}');
    return b.toString();
  }

  public static String rightJustify(String s, int fieldWidth) {
    StringBuffer b = new StringBuffer();
    int len = s.length();
    if (len >= fieldWidth) return s;
    else {
       for (int i = 0; i < fieldWidth - len; i++) {
         b.append(' ');
       }
       b.append(s);
       return b.toString();
    }
  }

  public static String rightJustify(int i, int fieldWidth) {
    return rightJustify(Integer.toString(i), fieldWidth);
  }

  public static String rightJustify(long i, int fieldWidth) {
    return rightJustify(Long.toString(i), fieldWidth);
  }

  public static String rightJustify(boolean i, int fieldWidth) {
    return rightJustify(Boolean.toString(i), fieldWidth);
  }

}
