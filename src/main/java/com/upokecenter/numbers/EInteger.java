package com.upokecenter.numbers;
/*
Written in 2013-2015 by Peter O.

Parts of the code were adapted by Peter O. from
the public-domain code from the library
CryptoPP by Wei Dai.

Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain.Divide(ValueZero)/1.0/
If you like this, you should donate to Peter O.
at: http://upokecenter.dreamhosters.com/articles/donate-now-2/
 */

    /**
     * An arbitrary-precision integer. <p>Instances of this class are immutable, so
     * they are inherently safe for use by multiple threads. Multiple
     * instances of this object with the same value are interchangeable, but
     * they should be compared using the "Equals" method rather than the
     * "==" operator.</p>
     */
  public final class EInteger implements Comparable<EInteger> {
    private static final String Digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int RecursionLimit = 10;

    private static final int ShortMask = 0xffff;

    private static final EInteger ValueOne = new EInteger(
      1, new short[] { 1, 0 }, false);

    private static final EInteger ValueTen = new EInteger(
      1, new short[] { 10, 0 }, false);

    private static final int[] ValueCharToDigit = { 36, 36, 36, 36, 36, 36,
      36,
      36,
      36, 36, 36, 36, 36, 36, 36, 36,
      36, 36, 36, 36, 36, 36, 36, 36,
      36, 36, 36, 36, 36, 36, 36, 36,
      36, 36, 36, 36, 36, 36, 36, 36,
      36, 36, 36, 36, 36, 36, 36, 36,
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 36, 36, 36, 36, 36, 36,
      36, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
      25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 36, 36, 36, 36,
      36, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
      25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 36, 36, 36, 36 };

    private static final int[] ValueMaxSafeInts = { 1073741823, 715827881,
      536870911, 429496728, 357913940, 306783377, 268435455, 238609293,
      214748363, 195225785, 178956969, 165191048, 153391688, 143165575,
      134217727, 126322566, 119304646, 113025454, 107374181, 102261125,
      97612892, 93368853, 89478484, 85899344, 82595523, 79536430, 76695843,
      74051159, 71582787, 69273665, 67108863, 65075261, 63161282, 61356674,
      59652322 };

    private static final EInteger ValueZero = new EInteger(
      0, new short[] { 0, 0 }, false);

    private final boolean negative;
    private final int wordCount;
    private final short[] words;

    private EInteger(int wordCount, short[] reg, boolean negative) {
      this.wordCount = wordCount;
      this.words = reg;
      this.negative = negative;
    }

    /**
     * Gets a value not documented yet.
     * @return A value not documented yet.
     */
    public static EInteger getOne() {
        return ValueOne;
      }

    /**
     * Gets the number 10 as an arbitrary-precision integer.
     * @return A value not documented yet.
     */
    public static EInteger getTen() {
        return ValueTen;
      }

    /**
     * Gets a value not documented yet.
     * @return A value not documented yet.
     */
    public static EInteger getZero() {
        return ValueZero;
      }

    /**
     * Gets a value indicating whether this value is even.
     * @return True if this value is even; otherwise, false.
     */
    public final boolean isEven() {
        return !this.GetUnsignedBit(0);
      }

    /**
     * Gets a value indicating whether this value is 0.
     * @return True if this value is 0; otherwise, false.
     */
    public final boolean isZero() {
        return this.wordCount == 0;
      }

    /**
     * Gets the sign of this object's value.
     * @return 0 if this value is zero; -1 if this value is negative, or 1 if this
     * value is positive.
     */
    public final int signum() {
        return (this.wordCount == 0) ? 0 : (this.negative ? -1 : 1);
      }

    /**
     * Initializes an arbitrary-precision integer from an array of bytes.
     * @param bytes A byte array consisting of the two's-complement integer
     * representation of the arbitrary-precision integer to create. The byte
     * array is encoded using the following rules:<ul> <li>Positive numbers
     * have the first byte's highest bit cleared, and negative numbers have
     * the bit set.</li> <li>The last byte contains the lowest 8-bits, the
     * next-to-last contains the next lowest 8 bits, and so on. For example,
     * the number 300 can be encoded as {@code 0x01, 0x2c} and 200 as {@code
     * 0x00, 0xc8}. (Note that the second example contains a set high bit in
     * {@code 0xC8}, so an additional 0 is added at the start to ensure it's
     * interpreted as positive.)</li> <li>To encode negative numbers, take
     * the absolute value of the number, subtract by 1, encode the number
     * into bytes, and toggle each bit of each byte. Any further bits that
     * appear beyond the most significant bit of the number will be all
     * ones. For example, the number -450 can be encoded as {@code 0xfe,
     * 0x70} and -52869 as {@code 0xff, 0x31, 0x7b}. (Note that the second
     * example contains a cleared high bit in {@code 0x31, 0x7b}, so an
     * additional 0xFF is added at the start to ensure it's interpreted as
     * negative.)</li></ul> <p>For little-endian, the byte order is reversed
     * from the byte order just discussed.</p>
     * @param littleEndian If true, the byte order is little-endian, or
     * least-significant-byte first. If false, the byte order is big-endian,
     * or most-significant-byte first.
     * @return An arbitrary-precision integer. Returns 0 if the byte array's length
     * is 0.
     * @throws java.lang.NullPointerException The parameter {@code bytes} is null.
     */
    public static EInteger FromBytes(byte[] bytes, boolean littleEndian) {
      if (bytes == null) {
        throw new NullPointerException("bytes");
      }
      if (bytes.length == 0) {
        return EInteger.FromInt64(0);
      }
      int len = bytes.length;
      int wordLength = ((int)len + 1) >> 1;
      wordLength = RoundupSize(wordLength);
      short[] newreg = new short[wordLength];
      int valueJIndex = littleEndian ? len - 1 : 0;
      boolean numIsNegative = (bytes[valueJIndex] & 0x80) != 0;
      boolean newnegative = numIsNegative;
      int j = 0;
      if (!numIsNegative) {
        for (int i = 0; i < len; i += 2, j++) {
          int index = littleEndian ? i : len - 1 - i;
          int index2 = littleEndian ? i + 1 : len - 2 - i;
          int nrj = ((int)bytes[index]) & 0xff;
          if (index2 >= 0 && index2 < len) {
            nrj |= ((int)bytes[index2]) << 8;
          }
          newreg[j] = ((short)nrj);
        }
      } else {
        for (int i = 0; i < len; i += 2, j++) {
          int index = littleEndian ? i : len - 1 - i;
          int index2 = littleEndian ? i + 1 : len - 2 - i;
          int nrj = ((int)bytes[index]) & 0xff;
          if (index2 >= 0 && index2 < len) {
            nrj |= ((int)bytes[index2]) << 8;
          } else {
            // sign extend the last byte
            nrj |= 0xff00;
          }
          newreg[j] = ((short)nrj);
        }
        for (; j < newreg.length; ++j) {
          newreg[j] = ((short)0xffff);  // sign extend remaining words
        }
        TwosComplement(newreg, 0, (int)newreg.length);
      }
      int newwordCount = newreg.length;
      while (newwordCount != 0 && newreg[newwordCount - 1] == 0) {
        --newwordCount;
      }
      return (newwordCount == 0) ? EInteger.FromInt64(0) : (new
                    EInteger(
                    newwordCount,
                    newreg,
                    newnegative));
    }

    /**
     * Not documented yet.
     * @param intValue The parameter {@code intValue} is not documented yet.
     * @return An EInteger object.
     */
    public static EInteger FromInt32(int intValue) {
      if (intValue == 0) {
        return ValueZero;
      }
      if (intValue == 1) {
        return ValueOne;
      }
      if (intValue == 10) {
        return ValueTen;
      }
      short[] retreg;
      boolean retnegative;
      int retwordcount;
      {
        retnegative = intValue < 0;
        if ((intValue >> 15) == 0) {
          retreg = new short[2];
          if (retnegative) {
            intValue = -intValue;
          }
          retreg[0] = (short)(intValue & 0xffff);
          retwordcount = 1;
        } else if (intValue == Integer.MIN_VALUE) {
          retreg = new short[2];
          retreg[0] = 0;
          retreg[1] = ((short)0x8000);
          retwordcount = 2;
        } else {
          retreg = new short[2];
          if (retnegative) {
            intValue = -intValue;
          }
          retreg[0] = (short)(intValue & 0xffff);
          intValue >>= 16;
          retreg[1] = (short)(intValue & 0xffff);
          retwordcount = (retreg[1] == 0) ? 1 : 2;
        }
      }
      return new EInteger(retwordcount, retreg, retnegative);
    }

    /**
     * Converts a 64-bit signed integer to a big integer.
     * @param longerValue The parameter {@code longerValue} is not documented yet.
     * @return An arbitrary-precision integer with the same value as the 64-bit
     * number.
     */
    public static EInteger FromInt64(long longerValue) {
      if (longerValue == 0) {
        return ValueZero;
      }
      if (longerValue == 1) {
        return ValueOne;
      }
      if (longerValue == 10) {
        return ValueTen;
      }
      short[] retreg;
      boolean retnegative;
      int retwordcount;
      {
        retnegative = longerValue < 0;
        if ((longerValue >> 15) == 0) {
          retreg = new short[2];
          long intValue = (int)longerValue;
          if (retnegative) {
            intValue = -intValue;
          }
          retreg[0] = (short)(intValue & 0xffff);
          retwordcount = 1;
        } else if (longerValue == Long.MIN_VALUE) {
          retreg = new short[4];
          retreg[0] = 0;
          retreg[1] = 0;
          retreg[2] = 0;
          retreg[3] = ((short)0x8000);
          retwordcount = 4;
        } else {
          retreg = new short[4];
          long ut = longerValue;
          if (retnegative) {
            ut = -ut;
          }
          retreg[0] = (short)(ut & 0xffff);
          ut >>= 16;
          retreg[1] = (short)(ut & 0xffff);
          ut >>= 16;
          retreg[2] = (short)(ut & 0xffff);
          ut >>= 16;
          retreg[3] = (short)(ut & 0xffff);
          // at this point, the word count can't
          // be 0 (the check for 0 was already done above)
          retwordcount = 4;
          while (retwordcount != 0 &&
                 retreg[retwordcount - 1] == 0) {
            --retwordcount;
          }
        }
      }
      return new EInteger(retwordcount, retreg, retnegative);
    }

    /**
     * Converts a string to an arbitrary-precision integer.
     * @param str A text string. The string must contain only characters allowed by
     * the given radix, except that it may start with a minus sign ("-",
     * U + 002D) to indicate a negative number. The string is not allowed to
     * contain white space characters, including spaces.
     * @param radix A base from 2 to 36. Depending on the radix, the string can use
     * the basic digits 0 to 9 (U + 0030 to U + 0039) and then the basic letters
     * A to Z (U + 0041 to U + 005A). For example, 0-9 in radix 10, and 0-9,
     * then A-F in radix 16.
     * @return An arbitrary-precision integer with the same value as given in the
     * string.
     * @throws java.lang.NullPointerException The parameter {@code str} is null.
     * @throws IllegalArgumentException The parameter {@code radix} is less than 2
     * or greater than 36.
     * @throws java.lang.NumberFormatException The string is empty or in an invalid format.
     */
    public static EInteger FromRadixString(String str, int radix) {
      if (str == null) {
        throw new NullPointerException("str");
      }
      return FromRadixSubstring(str, radix, 0, str.length());
    }

    /**
     * Converts a portion of a string to an arbitrary-precision integer in a given
     * radix.
     * @param str A text string. The desired portion of the string must contain
     * only characters allowed by the given radix, except that it may start
     * with a minus sign ("-", U+002D) to indicate a negative number. The
     * desired portion is not allowed to contain white space characters,
     * including spaces.
     * @param radix A base from 2 to 36. Depending on the radix, the string can use
     * the basic digits 0 to 9 (U + 0030 to U + 0039) and then the basic letters
     * A to Z (U + 0041 to U + 005A). For example, 0-9 in radix 10, and 0-9,
     * then A-F in radix 16.
     * @param index The index of the string that starts the string portion.
     * @param endIndex The index of the string that ends the string portion. The
     * length will be index + endIndex - 1.
     * @return An arbitrary-precision integer with the same value as given in the
     * string portion.
     * @throws java.lang.NullPointerException The parameter {@code str} is null.
     * @throws IllegalArgumentException The parameter {@code index} is less than 0,
     * {@code endIndex} is less than 0, or either is greater than the
     * string's length, or {@code endIndex} is less than {@code index}.
     * @throws java.lang.NumberFormatException The string portion is empty or in an invalid
     * format.
     */
    public static EInteger FromRadixSubstring(
      String str,
      int radix,
      int index,
      int endIndex) {
      if (str == null) {
        throw new NullPointerException("str");
      }
      if (radix < 2) {
        throw new IllegalArgumentException("radix (" + radix +
                    ") is less than 2");
      }
      if (radix > 36) {
        throw new IllegalArgumentException("radix (" + radix +
                    ") is more than 36");
      }
      if (index < 0) {
        throw new IllegalArgumentException("index (" + index + ") is less than " +
                    "0");
      }
      if (index > str.length()) {
        throw new IllegalArgumentException("index (" + index + ") is more than " +
                    str.length());
      }
      if (endIndex < 0) {
        throw new IllegalArgumentException("endIndex (" + endIndex +
                    ") is less than 0");
      }
      if (endIndex > str.length()) {
        throw new IllegalArgumentException("endIndex (" + endIndex +
                    ") is more than " + str.length());
      }
      if (endIndex < index) {
        throw new IllegalArgumentException("endIndex (" + endIndex +
                    ") is less than " + index);
      }
      if (index == endIndex) {
        throw new NumberFormatException("No digits");
      }
      boolean negative = false;
      if (str.charAt(index) == '-') {
        ++index;
        if (index == endIndex) {
          throw new NumberFormatException("No digits");
        }
        negative = true;
      }
      // Skip leading zeros
      for (; index < endIndex; ++index) {
        char c = str.charAt(index);
        if (c != 0x30) {
          break;
        }
      }
      int effectiveLength = endIndex - index;
      if (effectiveLength == 0) {
        return EInteger.FromInt64(0);
      }
      short[] bigint;
      if (radix == 16) {
        // Special case for hexadecimal radix
        int leftover = effectiveLength & 3;
        int wordCount = effectiveLength >> 2;
        if (leftover != 0) {
          ++wordCount;
        }
        bigint = new short[wordCount + (wordCount & 1)];
        int currentDigit = wordCount - 1;
        // Get most significant digits if effective
        // length is not divisible by 4
        if (leftover != 0) {
          int extraWord = 0;
          for (int i = 0; i < leftover; ++i) {
            extraWord <<= 4;
            char c = str.charAt(index + i);
            int digit = (c >= 0x80) ? 36 : ValueCharToDigit[(int)c];
            if (digit >= 16) {
              throw new NumberFormatException("Illegal character found");
            }
            extraWord |= digit;
          }
          bigint[currentDigit] = ((short)extraWord);
          --currentDigit;
          index += leftover;
        }

        while (index < endIndex) {
          char c = str.charAt(index + 3);
          int digit = (c >= 0x80) ? 36 : ValueCharToDigit[(int)c];
          if (digit >= 16) {
            throw new NumberFormatException("Illegal character found");
          }
          int word = digit;
          c = str.charAt(index + 2);
          digit = (c >= 0x80) ? 36 : ValueCharToDigit[(int)c];
          if (digit >= 16) {
            throw new NumberFormatException("Illegal character found");
          }

          word |= digit << 4;
          c = str.charAt(index + 1);
          digit = (c >= 0x80) ? 36 : ValueCharToDigit[(int)c];
          if (digit >= 16) {
            throw new NumberFormatException("Illegal character found");
          }

          word |= digit << 8;
          c = str.charAt(index);
          digit = (c >= 0x80) ? 36 : ValueCharToDigit[(int)c];
          if (digit >= 16) {
            throw new NumberFormatException("Illegal character found");
          }
          word |= digit << 12;
          index += 4;
          bigint[currentDigit] = ((short)word);
          --currentDigit;
        }
      } else {
        bigint = new short[4];
        boolean haveSmallInt = true;
        int maxSafeInt = ValueMaxSafeInts[radix - 2];
        int maxShortPlusOneMinusRadix = 65536 - radix;
        int smallInt = 0;
        for (int i = index; i < endIndex; ++i) {
          char c = str.charAt(i);
          int digit = (c >= 0x80) ? 36 : ValueCharToDigit[(int)c];
          if (digit >= radix) {
            throw new NumberFormatException("Illegal character found");
          }
          if (haveSmallInt && smallInt < maxSafeInt) {
            smallInt *= radix;
            smallInt += digit;
          } else {
            if (haveSmallInt) {
              bigint[0] = ((short)(smallInt & 0xffff));
              bigint[1] = ((short)((smallInt >> 16) & 0xffff));
              haveSmallInt = false;
            }
            // Multiply by the radix
            short carry = 0;
            int n = bigint.length;
            for (int j = 0; j < n; ++j) {
              int p;
              p = ((((int)bigint[j]) & 0xffff) * radix);
              int p2 = ((int)carry) & 0xffff;
              p = (p + p2);
              bigint[j] = ((short)p);
              carry = ((short)(p >> 16));
            }
            if (carry != 0) {
              bigint = GrowForCarry(bigint, carry);
            }
            // Add the parsed digit
            if (digit != 0) {
              int d = bigint[0] & 0xffff;
              if (d <= maxShortPlusOneMinusRadix) {
                bigint[0] = ((short)(d + digit));
              } else if (Increment(bigint, 0, bigint.length, (short)digit) !=
                    0) {
                bigint = GrowForCarry(bigint, (short)1);
              }
            }
          }
        }
        if (haveSmallInt) {
          bigint[0] = ((short)(smallInt & 0xffff));
          bigint[1] = ((short)((smallInt >> 16) & 0xffff));
        }
      }
      int count = CountWords(bigint, bigint.length);
      return (count == 0) ? EInteger.FromInt64(0) : new EInteger(
        count,
        bigint,
        negative);
    }

    /**
     * Converts a string to an arbitrary-precision integer.
     * @param str A text string. The string must contain only basic digits 0 to 9
     * (U+0030 to U+0039), except that it may start with a minus sign ("-",
     * U + 002D) to indicate a negative number. The string is not allowed to
     * contain white space characters, including spaces.
     * @return An arbitrary-precision integer with the same value as given in the
     * string.
     * @throws java.lang.NullPointerException The parameter {@code str} is null.
     * @throws java.lang.NumberFormatException The parameter {@code str} is in an invalid
     * format.
     */
    public static EInteger FromString(String str) {
      if (str == null) {
        throw new NullPointerException("str");
      }
      return FromRadixSubstring(str, 10, 0, str.length());
    }

    /**
     * Converts a portion of a string to an arbitrary-precision integer.
     * @param str A text string. The desired portion of the string must contain
     * only basic digits 0 to 9 (U + 0030 to U + 0039), except that it may start
     * with a minus sign ("-", U+002D) to indicate a negative number. The
     * desired portion is not allowed to contain white space characters,
     * including spaces.
     * @param index The index of the string that starts the string portion.
     * @param endIndex The index of the string that ends the string portion. The
     * length will be index + endIndex - 1.
     * @return An arbitrary-precision integer with the same value as given in the
     * string portion.
     * @throws java.lang.NullPointerException The parameter {@code str} is null.
     * @throws IllegalArgumentException The parameter {@code index} is less than 0,
     * {@code endIndex} is less than 0, or either is greater than the
     * string's length, or {@code endIndex} is less than {@code index}.
     * @throws java.lang.NumberFormatException The string portion is empty or in an invalid
     * format.
     */
    public static EInteger FromSubstring(
      String str,
      int index,
      int endIndex) {
      if (str == null) {
        throw new NullPointerException("str");
      }
      return FromRadixSubstring(str, 10, index, endIndex);
    }

    /**
     * Returns the absolute value of this object's value.
     * @return This object's value with the sign removed.
     */
    public EInteger Abs() {
      return (this.wordCount == 0 || !this.negative) ? this : new
        EInteger(this.wordCount, this.words, false);
    }

    /**
     * Adds this object and another object.
     * @param bigintAugend Another arbitrary-precision integer.
     * @return The sum of the two objects.
     * @throws java.lang.NullPointerException The parameter {@code bigintAugend} is
     * null.
     */
    public EInteger Add(EInteger bigintAugend) {
      if (bigintAugend == null) {
        throw new NullPointerException("bigintAugend");
      }
      if (this.wordCount == 0) {
        return bigintAugend;
      }
      if (bigintAugend.wordCount == 0) {
        return this;
      }
      short[] sumreg;
      if (bigintAugend.wordCount == 1 && this.wordCount == 1) {
        if (this.negative == bigintAugend.negative) {
          int intSum = (((int)this.words[0]) & 0xffff) +
            (((int)bigintAugend.words[0]) & 0xffff);
          sumreg = new short[2];
          sumreg[0] = ((short)intSum);
          sumreg[1] = ((short)(intSum >> 16));
          return new EInteger(
            ((intSum >> 16) == 0) ? 1 : 2,
            sumreg,
            this.negative);
        } else {
          int a = ((int)this.words[0]) & 0xffff;
          int b = ((int)bigintAugend.words[0]) & 0xffff;
          if (a == b) {
            return EInteger.FromInt64(0);
          }
          if (a > b) {
            a -= b;
            sumreg = new short[2];
            sumreg[0] = ((short)a);
            return new EInteger(1, sumreg, this.negative);
          }
          b -= a;
          sumreg = new short[2];
          sumreg[0] = ((short)b);
          return new EInteger(1, sumreg, !this.negative);
        }
      }
      if ((!this.negative) == (!bigintAugend.negative)) {
        // both nonnegative or both negative
        int addendCount = this.wordCount;
        int augendCount = bigintAugend.wordCount;
        if (augendCount <= 2 && addendCount <= 2 &&
           (this.wordCount < 2 || (this.words[1] >> 15) == 0) &&
           (bigintAugend.wordCount < 2 || (bigintAugend.words[1] >> 15) == 0)) {
          int a = ((int)this.words[0]) & 0xffff;
          a |= (((int)this.words[1]) & 0xffff) << 16;
          int b = ((int)bigintAugend.words[0]) & 0xffff;
          b |= (((int)bigintAugend.words[1]) & 0xffff) << 16;
          a = ((int)(a + b));
          sumreg = new short[2];
          sumreg[0] = ((short)(a & 0xffff));
          sumreg[1] = ((short)((a >> 16) & 0xffff));
          int wcount = (sumreg[1] == 0) ? 1 : 2;
          return new EInteger(wcount, sumreg, this.negative);
        }
        sumreg = new short[(
          int)Math.max(
                    this.words.length,
                    bigintAugend.words.length)];
        int carry;
        int desiredLength = Math.max(addendCount, augendCount);
        if (addendCount == augendCount) {
          carry = AddOneByOne(
            sumreg,
            0,
            this.words,
            0,
            bigintAugend.words,
            0,
            addendCount);
        } else if (addendCount > augendCount) {
          // Addend is bigger
          carry = AddOneByOne(
            sumreg,
            0,
            this.words,
            0,
            bigintAugend.words,
            0,
            augendCount);
          System.arraycopy(
            this.words,
            augendCount,
            sumreg,
            augendCount,
            addendCount - augendCount);
          if (carry != 0) {
            carry = Increment(
              sumreg,
              augendCount,
              addendCount - augendCount,
              (short)carry);
          }
        } else {
          // Augend is bigger
          carry = AddOneByOne(
            sumreg,
            0,
            this.words,
            0,
            bigintAugend.words,
            0,
            (int)addendCount);
          System.arraycopy(
            bigintAugend.words,
            addendCount,
            sumreg,
            addendCount,
            augendCount - addendCount);
          if (carry != 0) {
            carry = Increment(
              sumreg,
              addendCount,
              (int)(augendCount - addendCount),
              (short)carry);
          }
        }
        boolean needShorten = true;
        if (carry != 0) {
          int nextIndex = desiredLength;
          int len = RoundupSize(nextIndex + 1);
          sumreg = CleanGrow(sumreg, len);
          sumreg[nextIndex] = (short)carry;
          needShorten = false;
        }
        int sumwordCount = CountWords(sumreg, sumreg.length);
        if (sumwordCount == 0) {
          return EInteger.FromInt64(0);
        }
        if (needShorten) {
          sumreg = ShortenArray(sumreg, sumwordCount);
        }
        return new EInteger(sumwordCount, sumreg, this.negative);
      }
      EInteger minuend = this;
      EInteger subtrahend = bigintAugend;
      if (this.negative) {
        // this is negative, b is nonnegative
        minuend = bigintAugend;
        subtrahend = this;
      }
      // Do a subtraction
      int words1Size = minuend.wordCount;
      words1Size += words1Size & 1;
      int words2Size = subtrahend.wordCount;
      words2Size += words2Size & 1;
      boolean diffNeg = false;
      short borrow;
      short[] diffReg = new short[(
        int)Math.max(
                    minuend.words.length,
                    subtrahend.words.length)];
      if (words1Size == words2Size) {
        if (Compare(minuend.words, 0, subtrahend.words, 0, (int)words1Size) >=
            0) {
          // words1 is at least as high as words2
          Subtract(
            diffReg,
            0,
            minuend.words,
            0,
            subtrahend.words,
            0,
            words1Size);
        } else {
          // words1 is less than words2
          Subtract(
            diffReg,
            0,
            subtrahend.words,
            0,
            minuend.words,
            0,
            words1Size);
          diffNeg = true;  // difference will be negative
        }
      } else if (words1Size > words2Size) {
        // words1 is greater than words2
        borrow = (
          short)Subtract(
          diffReg,
          0,
          minuend.words,
          0,
          subtrahend.words,
          0,
          words2Size);
        System.arraycopy(
          minuend.words,
          words2Size,
          diffReg,
          words2Size,
          words1Size - words2Size);
        Decrement(diffReg, words2Size, (int)(words1Size - words2Size), borrow);
      } else {
        // words1 is less than words2
        borrow = (
          short)Subtract(
          diffReg,
          0,
          subtrahend.words,
          0,
          minuend.words,
          0,
          words1Size);
        System.arraycopy(
          subtrahend.words,
          words1Size,
          diffReg,
          words1Size,
          words2Size - words1Size);
        Decrement(diffReg, words1Size, (int)(words2Size - words1Size), borrow);
        diffNeg = true;
      }
      int count = CountWords(diffReg, diffReg.length);
      if (count == 0) {
        return EInteger.FromInt64(0);
      }
      diffReg = ShortenArray(diffReg, count);
      return new EInteger(count, diffReg, diffNeg);
    }

    /**
     * Converts this object's value to a 32-bit signed integer, throwing an
     * exception if it can't fit.
     * @return A 32-bit signed integer.
     * @throws java.lang.ArithmeticException This object's value is too big to fit a
     * 32-bit signed integer.
     */
    public int AsInt32Checked() {
      int count = this.wordCount;
      if (count == 0) {
        return 0;
      }
      if (count > 2) {
        throw new ArithmeticException();
      }
      if (count == 2 && (this.words[1] & 0x8000) != 0) {
        if (this.negative && this.words[1] == ((short)0x8000) &&
            this.words[0] == 0) {
          return Integer.MIN_VALUE;
        }
        throw new ArithmeticException();
      }
      return this.AsInt32Unchecked();
    }

    /**
     * Converts this object's value to a 32-bit signed integer. If the value can't
     * fit in a 32-bit integer, returns the lower 32 bits of this object's
     * two's complement representation (in which case the return value might
     * have a different sign than this object's value).
     * @return A 32-bit signed integer.
     */
    public int AsInt32Unchecked() {
      int c = (int)this.wordCount;
      if (c == 0) {
        return 0;
      }
      int intRetValue = ((int)this.words[0]) & 0xffff;
      if (c > 1) {
        intRetValue |= (((int)this.words[1]) & 0xffff) << 16;
      }
      if (this.negative) {
        intRetValue = (intRetValue - 1);
        intRetValue = (~intRetValue);
      }
      return intRetValue;
    }

    /**
     * Finds the minimum number of bits needed to represent this object&#x27;s
     * value, except for its sign. If the value is negative, finds the
     * number of bits in a value equal to this object's absolute value minus
     * 1.
     * @return The number of bits in this object's value. Returns 0 if this
     * object's value is 0 or negative 1.
     */
    public int GetSignedBitLength() {
      int wc = this.wordCount;
      if (wc != 0) {
        if (this.negative) {
          return this.Abs().Subtract(EInteger.FromInt64(1)).GetSignedBitLength();
        }
        int numberValue = ((int)this.words[wc - 1]) & 0xffff;
        wc = (wc - 1) << 4;
        if (numberValue == 0) {
          return wc;
        }
        wc += 16;
        {
          if ((numberValue >> 8) == 0) {
            numberValue <<= 8;
            wc -= 8;
          }
          if ((numberValue >> 12) == 0) {
            numberValue <<= 4;
            wc -= 4;
          }
          if ((numberValue >> 14) == 0) {
            numberValue <<= 2;
            wc -= 2;
          }
          return ((numberValue >> 15) == 0) ? wc - 1 : wc;
        }
      }
      return 0;
    }

    /**
     * Returns whether this object's value can fit in a 32-bit signed integer.
     * @return True if this object's value is MinValue or greater, and MaxValue or
     * less; otherwise, false.
     */
    public boolean CanFitInInt32() {
      int c = (int)this.wordCount;
      if (c > 2) {
        return false;
      }
      if (c == 2 && (this.words[1] & 0x8000) != 0) {
        return this.negative && this.words[1] == ((short)0x8000) &&
          this.words[0] == 0;
      }
      return true;
    }

    /**
     * Compares an arbitrary-precision integer with this instance.
     * @param other The parameter {@code other} is not documented yet.
     * @return Zero if the values are equal; a negative number if this instance is
     * less, or a positive number if this instance is greater.
     */
    public int compareTo(EInteger other) {
      if (other == null) {
        return 1;
      }
      if (this == other) {
        return 0;
      }
      int size = this.wordCount, tempSize = other.wordCount;
      int sa = size == 0 ? 0 : (this.negative ? -1 : 1);
      int sb = tempSize == 0 ? 0 : (other.negative ? -1 : 1);
      if (sa != sb) {
        return (sa < sb) ? -1 : 1;
      }
      if (sa == 0) {
        return 0;
      }
      if (size == tempSize) {
        if (size == 1 && this.words[0] == other.words[0]) {
          return 0;
        } else {
          short[] words1 = this.words;
          short[] words2 = other.words;
          while ((size--) != 0) {
            int an = ((int)words1[size]) & 0xffff;
            int bn = ((int)words2[size]) & 0xffff;
            if (an > bn) {
              return (sa > 0) ? 1 : -1;
            }
            if (an < bn) {
              return (sa > 0) ? -1 : 1;
            }
          }
          return 0;
        }
      }
      return ((size > tempSize) ^ (sa <= 0)) ? 1 : -1;
    }

    /**
     * Gets a value indicating whether this object&#x27;s value is a power of two.
     * @return True if this object's value is a power of two; otherwise, false.
     */
    public final boolean isPowerOfTwo() {
        if (this.negative) {
          return false;
        }
        return (this.wordCount == 0) ? false : (this.GetUnsignedBitLength()
          - 1 == this.GetLowBit());
      }

    /**
     * Divides this instance by the value of an arbitrary-precision integer. The
     * result is rounded down (the fractional part is discarded). Except if
     * the result is 0, it will be negative if this object is positive and
     * the other is negative, or vice versa, and will be positive if both
     * are positive or both are negative.
     * @param bigintDivisor Another arbitrary-precision integer.
     * @return The quotient of the two objects.
     * @throws ArithmeticException The divisor is zero.
     * @throws java.lang.NullPointerException The parameter {@code bigintDivisor} is
     * null.
     * @throws ArithmeticException Attempted to divide by zero.
     */
    public EInteger Divide(EInteger bigintDivisor) {
      if (bigintDivisor == null) {
        throw new NullPointerException("bigintDivisor");
      }
      int words1Size = this.wordCount;
      int words2Size = bigintDivisor.wordCount;
      // ---- Special cases
      if (words2Size == 0) {
        throw new ArithmeticException();
      }
      if (words1Size < words2Size) {
        // dividend is less than divisor (includes case
        // where dividend is 0)
        return EInteger.FromInt64(0);
      }
      if (words1Size <= 2 && words2Size <= 2 && this.CanFitInInt32() &&
          bigintDivisor.CanFitInInt32()) {
        int valueASmall = this.AsInt32Checked();
        int valueBSmall = bigintDivisor.AsInt32Checked();
        if (valueASmall != Integer.MIN_VALUE || valueBSmall != -1) {
          int result = valueASmall / valueBSmall;
          return EInteger.FromInt64(result);
        }
      }
      short[] quotReg;
      int quotwordCount;
      if (words2Size == 1) {
        // divisor is small, use a fast path
        quotReg = new short[this.words.length];
        quotwordCount = this.wordCount;
        FastDivide(quotReg, this.words, words1Size, bigintDivisor.words[0]);
        while (quotwordCount != 0 && quotReg[quotwordCount - 1] == 0) {
          --quotwordCount;
        }
        return (
          quotwordCount != 0) ? (
          new EInteger(
            quotwordCount,
            quotReg,
            this.negative ^ bigintDivisor.negative)) : EInteger.FromInt64(0);
      }
      // ---- General case
      words1Size += words1Size & 1;
      words2Size += words2Size & 1;
      quotReg = new short[RoundupSize((int)(words1Size - words2Size + 2))];
      short[] tempbuf = new short[words1Size + (3 * (words2Size + 2))];
      Divide(
        null,
        0,
        quotReg,
        0,
        tempbuf,
        0,
        this.words,
        0,
        words1Size,
        bigintDivisor.words,
        0,
        words2Size);
      quotwordCount = CountWords(quotReg, quotReg.length);
      quotReg = ShortenArray(quotReg, quotwordCount);
      return (
        quotwordCount != 0) ? (
        new EInteger(
          quotwordCount,
          quotReg,
          this.negative ^ bigintDivisor.negative)) : EInteger.FromInt64(0);
    }

    /**
     * Divides this object by another arbitrary-precision integer and returns the
     * quotient and remainder.
     * @param divisor The parameter {@code divisor} is not documented yet.
     * @return An array with two arbitrary-precision integers: the first is the
     * quotient, and the second is the remainder.
     * @throws java.lang.NullPointerException The parameter divisor is null.
     * @throws ArithmeticException The parameter divisor is 0.
     * @throws ArithmeticException Attempted to divide by zero.
     */
    public EInteger[] DivRem(EInteger divisor) {
      if (divisor == null) {
        throw new NullPointerException("divisor");
      }
      int words1Size = this.wordCount;
      int words2Size = divisor.wordCount;
      if (words2Size == 0) {
        throw new ArithmeticException();
      }

      if (words1Size < words2Size) {
        // dividend is less than divisor (includes case
        // where dividend is 0)
        return new EInteger[] { EInteger.FromInt64(0), this };
      }
      if (words2Size == 1) {
        // divisor is small, use a fast path
        short[] quotient = new short[this.words.length];
        int smallRemainder = ((int)FastDivideAndRemainder(
          quotient,
          0,
          this.words,
          0,
          words1Size,
          divisor.words[0])) & 0xffff;
        int count = this.wordCount;
        while (count != 0 &&
               quotient[count - 1] == 0) {
          --count;
        }
        if (count == 0) {
          return new EInteger[] { EInteger.FromInt64(0), this };
        }
        quotient = ShortenArray(quotient, count);
        EInteger bigquo = new EInteger(
          count,
          quotient,
          this.negative ^ divisor.negative);
        if (this.negative) {
          smallRemainder = -smallRemainder;
        }
        return new EInteger[] { bigquo, EInteger.FromInt64(smallRemainder) };
      }
      words1Size += words1Size & 1;
      words2Size += words2Size & 1;
      short[] bigRemainderreg = new short[RoundupSize((int)words2Size)];
      short[] quotientreg = new short[RoundupSize((int)(words1Size - words2Size +
                    2))];
      short[] tempbuf = new short[words1Size + (3 * (words2Size + 2))];
      Divide(
        bigRemainderreg,
        0,
        quotientreg,
        0,
        tempbuf,
        0,
        this.words,
        0,
        words1Size,
        divisor.words,
        0,
        words2Size);
      int remCount = CountWords(bigRemainderreg, bigRemainderreg.length);
      int quoCount = CountWords(quotientreg, quotientreg.length);
      bigRemainderreg = ShortenArray(bigRemainderreg, remCount);
      quotientreg = ShortenArray(quotientreg, quoCount);
      EInteger bigrem = (remCount == 0) ? EInteger.FromInt64(0) : new
        EInteger(remCount, bigRemainderreg, this.negative);
      EInteger bigquo2 = (quoCount == 0) ? EInteger.FromInt64(0) : new
        EInteger(quoCount, quotientreg, this.negative ^ divisor.negative);
      return new EInteger[] { bigquo2, bigrem };
    }

    /**
     * Determines whether this object and another object are equal.
     * @param obj An arbitrary object.
     * @return True if this object and another object are equal; otherwise, false.
     */
    @Override public boolean equals(Object obj) {
      EInteger other = ((obj instanceof EInteger) ? (EInteger)obj : null);
      if (other == null) {
        return false;
      }
      if (this.wordCount == other.wordCount) {
        if (this.negative != other.negative) {
          return false;
        }
        for (int i = 0; i < this.wordCount; ++i) {
          if (this.words[i] != other.words[i]) {
            return false;
          }
        }
        return true;
      }
      return false;
    }

    /**
     *
     */
    public EInteger Gcd(EInteger bigintSecond) {
      if (bigintSecond == null) {
        throw new NullPointerException("bigintSecond");
      }
      if (this.isZero()) {
        return bigintSecond.Abs();
      }
      EInteger thisValue = this.Abs();
      if (bigintSecond.isZero()) {
        return thisValue;
      }
      bigintSecond = bigintSecond.Abs();
      if (bigintSecond.equals(EInteger.FromInt64(1)) ||
          thisValue.equals(bigintSecond)) {
        return bigintSecond;
      }
      if (thisValue.equals(EInteger.FromInt64(1))) {
        return thisValue;
      }
      if (thisValue.wordCount <= 10 && bigintSecond.wordCount <= 10) {
        int expOfTwo = Math.min(
          thisValue.GetLowBit(),
          bigintSecond.GetLowBit());
        while (true) {
          EInteger bigintA = (thisValue.Subtract(bigintSecond)).Abs();
          if (bigintA.isZero()) {
            if (expOfTwo != 0) {
              thisValue = thisValue.ShiftLeft(expOfTwo);
            }
            return thisValue;
          }
          int setbit = bigintA.GetLowBit();
          bigintA = bigintA.ShiftRight(setbit);
          bigintSecond = (thisValue.compareTo(bigintSecond) < 0) ? thisValue :
            bigintSecond;
          thisValue = bigintA;
        }
      } else {
        EInteger temp;
        while (!thisValue.isZero()) {
          if (thisValue.compareTo(bigintSecond) < 0) {
            temp = thisValue;
            thisValue = bigintSecond;
            bigintSecond = temp;
          }
          thisValue = thisValue.Remainder(bigintSecond);
        }
        return bigintSecond;
      }
    }

    /**
     *
     */
    public int GetDigitCount() {
      if (this.isZero()) {
        return 1;
      }
      if (this.HasSmallValue()) {
        long value = this.AsInt64Checked();
        if (value == Long.MIN_VALUE) {
          return 19;
        }
        if (value < 0) {
          value = -value;
        }
        if (value >= 1000000000L) {
          return (value >= 1000000000000000000L) ? 19 : ((value >=
                   100000000000000000L) ? 18 : ((value >= 10000000000000000L) ?
                    17 : ((value >= 1000000000000000L) ? 16 :
                    ((value >= 100000000000000L) ? 15 : ((value
                    >= 10000000000000L) ?
                    14 : ((value >= 1000000000000L) ? 13 : ((value
                    >= 100000000000L) ? 12 : ((value >= 10000000000L) ?
                    11 : ((value >= 1000000000L) ? 10 : 9)))))))));
        } else {
          int v2 = (int)value;
          return (v2 >= 100000000) ? 9 : ((v2 >= 10000000) ? 8 : ((v2 >=
                    1000000) ? 7 : ((v2 >= 100000) ? 6 : ((v2
                    >= 10000) ? 5 : ((v2 >= 1000) ? 4 : ((v2 >= 100) ?
                    3 : ((v2 >= 10) ? 2 : 1)))))));
        }
      }
      int bitlen = this.GetUnsignedBitLength();
      if (bitlen <= 2135) {
        // (x*631305) >> 21 is an approximation
        // to trunc(x*log10(2)) that is correct up
        // to x = 2135; the multiplication would require
        // up to 31 bits in all cases up to 2135
        // (cases up to 64 are already handled above)
        int minDigits = 1 + (((bitlen - 1) * 631305) >> 21);
        int maxDigits = 1 + ((bitlen * 631305) >> 21);
        if (minDigits == maxDigits) {
          // Number of digits is the same for
          // all numbers with this bit length
          return minDigits;
        }
      } else if (bitlen <= 6432162) {
        // Much more accurate approximation
        int minDigits = ApproxLogTenOfTwo(bitlen - 1);
        int maxDigits = ApproxLogTenOfTwo(bitlen);
        if (minDigits == maxDigits) {
          // Number of digits is the same for
          // all numbers with this bit length
          return 1 + minDigits;
        }
      }
      short[] tempReg = null;
      int currentCount = this.wordCount;
      int i = 0;
      while (currentCount != 0) {
        if (currentCount == 1 || (currentCount == 2 && tempReg[1] == 0)) {
          int rest = ((int)tempReg[0]) & 0xffff;
          if (rest >= 10000) {
            i += 5;
          } else if (rest >= 1000) {
            i += 4;
          } else if (rest >= 100) {
            i += 3;
          } else if (rest >= 10) {
            i += 2;
          } else {
            ++i;
          }
          break;
        }
        if (currentCount == 2 && tempReg[1] > 0 && tempReg[1] <= 0x7fff) {
          int rest = ((int)tempReg[0]) & 0xffff;
          rest |= (((int)tempReg[1]) & 0xffff) << 16;
          if (rest >= 1000000000) {
            i += 10;
          } else if (rest >= 100000000) {
            i += 9;
          } else if (rest >= 10000000) {
            i += 8;
          } else if (rest >= 1000000) {
            i += 7;
          } else if (rest >= 100000) {
            i += 6;
          } else if (rest >= 10000) {
            i += 5;
          } else if (rest >= 1000) {
            i += 4;
          } else if (rest >= 100) {
            i += 3;
          } else if (rest >= 10) {
            i += 2;
          } else {
            ++i;
          }
          break;
        } else {
          int wci = currentCount;
          short remainderShort = 0;
          int quo, rem;
          boolean firstdigit = false;
          short[] dividend = (tempReg == null) ? (this.words) : tempReg;
          // Divide by 10000
          while ((wci--) > 0) {
            int curValue = ((int)dividend[wci]) & 0xffff;
            int currentDividend = ((int)(curValue |
                    ((int)remainderShort << 16)));
            quo = currentDividend / 10000;
            if (!firstdigit && quo != 0) {
              firstdigit = true;
              // Since we are dividing from left to right, the first
              // nonzero result is the first part of the
              // new quotient
              bitlen = getUnsignedBitLengthEx(quo, wci + 1);
              if (bitlen <= 2135) {
                // (x*631305) >> 21 is an approximation
                // to trunc(x*log10(2)) that is correct up
                // to x = 2135; the multiplication would require
                // up to 31 bits in all cases up to 2135
                // (cases up to 64 are already handled above)
                int minDigits = 1 + (((bitlen - 1) * 631305) >> 21);
                int maxDigits = 1 + ((bitlen * 631305) >> 21);
                if (minDigits == maxDigits) {
                  // Number of digits is the same for
                  // all numbers with this bit length
                  return i + minDigits + 4;
                }
              } else if (bitlen <= 6432162) {
                // Much more accurate approximation
                int minDigits = ApproxLogTenOfTwo(bitlen - 1);
                int maxDigits = ApproxLogTenOfTwo(bitlen);
                if (minDigits == maxDigits) {
                  // Number of digits is the same for
                  // all numbers with this bit length
                  return i + 1 + minDigits + 4;
                }
              }
            }
            if (tempReg == null) {
              if (quo != 0) {
                tempReg = new short[this.wordCount];
                System.arraycopy(this.words, 0, tempReg, 0, tempReg.length);
                // Use the calculated word count during division;
                // zeros that may have occurred in division
                // are not incorporated in the tempReg
                currentCount = wci + 1;
                tempReg[wci] = ((short)quo);
              }
            } else {
              tempReg[wci] = ((short)quo);
            }
            rem = currentDividend - (10000 * quo);
            remainderShort = ((short)rem);
          }
          // Recalculate word count
          while (currentCount != 0 && tempReg[currentCount - 1] == 0) {
            --currentCount;
          }
          i += 4;
        }
      }
      return i;
    }

    /**
     * Returns the hash code for this instance.
     * @return A 32-bit signed integer.
     */
    @Override public int hashCode() {
      int hashCodeValue = 0;
      {
        hashCodeValue += 1000000007 * this.signum();
        if (this.words != null) {
          for (int i = 0; i < this.wordCount; ++i) {
            hashCodeValue += 1000000013 * this.words[i];
          }
        }
      }
      return hashCodeValue;
    }

    /**
     * Gets the lowest set bit in this number's absolute value. (This will also be
     * the lowest set bit in the number's two's-complement representation.)
     * @return The lowest bit set in the number, starting at 0. Returns -1 if this
     * value is 0 or odd.
     */
    public int GetLowBit() {
      int retSetBit = 0;
      for (int i = 0; i < this.wordCount; ++i) {
        int c = ((int)this.words[i]) & 0xffff;
        if (c == 0) {
          retSetBit += 16;
        } else {
          return (((c << 15) & 0xffff) != 0) ? (retSetBit + 0) : ((((c <<
                    14) & 0xffff) != 0) ? (retSetBit + 1) : ((((c <<
                    13) & 0xffff) != 0) ? (retSetBit + 2) : ((((c <<
                    12) & 0xffff) != 0) ? (retSetBit + 3) : ((((c << 11) &
                    0xffff) != 0) ? (retSetBit +
                    4) : ((((c << 10) & 0xffff) != 0) ? (retSetBit +
                    5) : ((((c << 9) & 0xffff) != 0) ? (retSetBit + 6) :
                    ((((c <<
                8) & 0xffff) != 0) ? (retSetBit + 7) : ((((c << 7) & 0xffff) !=
                    0) ? (retSetBit + 8) : ((((c << 6) & 0xffff) !=
                    0) ? (retSetBit + 9) : ((((c <<
                    5) & 0xffff) != 0) ? (retSetBit + 10) : ((((c <<
                    4) & 0xffff) != 0) ? (retSetBit + 11) : ((((c << 3) &
                    0xffff) != 0) ? (retSetBit + 12) : ((((c << 2) & 0xffff) !=
                    0) ? (retSetBit + 13) : ((((c << 1) & 0xffff) !=
                    0) ? (retSetBit + 14) : (retSetBit + 15)))))))))))))));
        }
      }
      return -1;
    }

    /**
     * Finds the minimum number of bits needed to represent this object&#x27;s
     * absolute value.
     * @return The number of bits in this object's value. Returns 0 if this
     * object's value is 0, and returns 1 if the value is negative 1.
     */
    public int GetUnsignedBitLength() {
      int wc = this.wordCount;
      if (wc != 0) {
        int numberValue = ((int)this.words[wc - 1]) & 0xffff;
        wc = (wc - 1) << 4;
        if (numberValue == 0) {
          return wc;
        }
        wc += 16;
        {
          if ((numberValue >> 8) == 0) {
            numberValue <<= 8;
            wc -= 8;
          }
          if ((numberValue >> 12) == 0) {
            numberValue <<= 4;
            wc -= 4;
          }
          if ((numberValue >> 14) == 0) {
            numberValue <<= 2;
            wc -= 2;
          }
          if ((numberValue >> 15) == 0) {
            --wc;
          }
        }
        return wc;
      }
      return 0;
    }

    /**
     * Converts this object's value to a 64-bit signed integer, throwing an
     * exception if it can't fit.
     * @return A 64-bit signed integer.
     * @throws java.lang.ArithmeticException This object's value is too big to fit a
     * 64-bit signed integer.
     */
    public long AsInt64Checked() {
      int count = this.wordCount;
      if (count == 0) {
        return (long)0;
      }
      if (count > 4) {
        throw new ArithmeticException();
      }
      if (count == 4 && (this.words[3] & 0x8000) != 0) {
        if (this.negative && this.words[3] == ((short)0x8000) &&
            this.words[2] == 0 && this.words[1] == 0 &&
            this.words[0] == 0) {
          return Long.MIN_VALUE;
        }
        throw new ArithmeticException();
      }
      return this.AsInt64Unchecked();
    }

    /**
     * Converts this object's value to a 64-bit signed integer. If the value can't
     * fit in a 64-bit integer, returns the lower 64 bits of this object's
     * two's complement representation (in which case the return value might
     * have a different sign than this object's value).
     * @return A 64-bit signed integer.
     */
    public long AsInt64Unchecked() {
      int c = (int)this.wordCount;
      if (c == 0) {
        return (long)0;
      }
      long ivv = 0;
      int intRetValue = ((int)this.words[0]) & 0xffff;
      if (c > 1) {
        intRetValue |= (((int)this.words[1]) & 0xffff) << 16;
      }
      if (c > 2) {
        int intRetValue2 = ((int)this.words[2]) & 0xffff;
        if (c > 3) {
          intRetValue2 |= (((int)this.words[3]) & 0xffff) << 16;
        }
        if (this.negative) {
          if (intRetValue == 0) {
            intRetValue = (intRetValue - 1);
            intRetValue2 = (intRetValue2 - 1);
          } else {
            intRetValue = (intRetValue - 1);
          }
          intRetValue = (~intRetValue);
          intRetValue2 = (~intRetValue2);
        }
        ivv = ((long)intRetValue) & 0xFFFFFFFFL;
        ivv |= ((long)intRetValue2) << 32;
        return ivv;
      }
      ivv = ((long)intRetValue) & 0xFFFFFFFFL;
      if (this.negative) {
        ivv = -ivv;
      }
      return ivv;
    }

    /**
     * Finds the modulus remainder that results when this instance is divided by
     * the value of an arbitrary-precision integer. The modulus remainder is
     * the same as the normal remainder if the normal remainder is positive,
     * and equals divisor plus normal remainder if the normal remainder is
     * negative.
     * @param divisor A divisor greater than 0 (the modulus).
     * @return An arbitrary-precision integer.
     * @throws ArithmeticException The parameter {@code divisor} is negative.
     * @throws java.lang.NullPointerException The parameter {@code divisor} is null.
     */
    public EInteger Mod(EInteger divisor) {
      if (divisor == null) {
        throw new NullPointerException("divisor");
      }
      if (divisor.signum() < 0) {
        throw new ArithmeticException("Divisor is negative");
      }
      EInteger rem = this.Remainder(divisor);
      if (rem.signum() < 0) {
        rem = divisor.Add(rem);
      }
      return rem;
    }

    /**
     * Calculates the remainder when an arbitrary-precision integer raised to a
     * certain power is divided by another arbitrary-precision integer.
     * @param pow Another arbitrary-precision integer.
     * @param mod An arbitrary-precision integer. (3).
     * @return An arbitrary-precision integer.
     * @throws java.lang.NullPointerException The parameter {@code pow} or {@code
     * mod} is null.
     */
    public EInteger ModPow(EInteger pow, EInteger mod) {
      if (pow == null) {
        throw new NullPointerException("pow");
      }
      if (mod == null) {
        throw new NullPointerException("mod");
      }
      if (pow.signum() < 0) {
        throw new IllegalArgumentException("pow (" + pow + ") is less than 0");
      }
      if (mod.signum() <= 0) {
        throw new IllegalArgumentException("mod (" + mod + ") is not greater than 0");
      }
      EInteger r = EInteger.FromInt64(1);
      EInteger v = this;
      while (!pow.isZero()) {
        if (!pow.isEven()) {
          r = (r.Multiply(v)).Mod(mod);
        }
        pow = pow.ShiftRight(1);
        if (!pow.isZero()) {
          v = (v.Multiply(v)).Mod(mod);
        }
      }
      return r;
    }

    /**
     * Multiplies this instance by the value of an arbitrary-precision integer
     * object.
     * @param bigintMult Another arbitrary-precision integer.
     * @return The product of the two numbers.
     * @throws java.lang.NullPointerException The parameter {@code bigintMult} is
     * null.
     */
    public EInteger Multiply(EInteger bigintMult) {
      if (bigintMult == null) {
        throw new NullPointerException("bigintMult");
      }
      if (this.wordCount == 0 || bigintMult.wordCount == 0) {
        return EInteger.FromInt64(0);
      }
      if (this.wordCount == 1 && this.words[0] == 1) {
        return this.negative ? bigintMult.Negate() : bigintMult;
      }
      if (bigintMult.wordCount == 1 && bigintMult.words[0] == 1) {
        return bigintMult.negative ? this.Negate() : this;
      }
      short[] productreg;
      int productwordCount;
      boolean needShorten = true;
      if (this.wordCount == 1) {
        int wc;
        if (bigintMult.wordCount == 1) {
          // NOTE: Result can't be 0 here, since checks
          // for 0 were already made earlier in this function
          productreg = new short[2];
          int ba = ((int)this.words[0]) & 0xffff;
          int bb = ((int)bigintMult.words[0]) & 0xffff;
          ba = (ba * bb);
          productreg[0 ] = ((short)(ba & 0xffff));
          productreg[1 ] = ((short)((ba >> 16) & 0xffff));
          short preg = productreg[1];
          wc = (preg == 0) ? 1 : 2;
          return new EInteger(
wc,
productreg,
this.negative ^ bigintMult.negative);
        }
        wc = bigintMult.wordCount;
        int regLength = RoundupSize(wc + 1);
        productreg = new short[regLength];
        productreg[wc] = LinearMultiply(
          productreg,
          0,
          bigintMult.words,
          0,
          this.words[0],
          wc);
        productwordCount = productreg.length;
        needShorten = false;
      } else if (bigintMult.wordCount == 1) {
        int wc = this.wordCount;
        int regLength = RoundupSize(wc + 1);
        productreg = new short[regLength];
        productreg[wc] = LinearMultiply(
          productreg,
          0,
          this.words,
          0,
          bigintMult.words[0],
          wc);
        productwordCount = productreg.length;
        needShorten = false;
      } else if (this.equals(bigintMult)) {
        int words1Size = RoundupSize(this.wordCount);
        productreg = new short[words1Size + words1Size];
        productwordCount = productreg.length;
        short[] workspace = new short[words1Size + words1Size];
        RecursiveSquare(
          productreg,
          0,
          workspace,
          0,
          this.words,
          0,
          words1Size);
      } else if (this.wordCount <= 10 && bigintMult.wordCount <= 10) {
        int wc = this.wordCount + bigintMult.wordCount;
        wc = RoundupSize(wc);
        productreg = new short[wc];
        productwordCount = productreg.length;
        SchoolbookMultiply(
          productreg,
          0,
          this.words,
          0,
          this.wordCount,
          bigintMult.words,
          0,
          bigintMult.wordCount);
        needShorten = false;
      } else {
        int words1Size = this.wordCount;
        int words2Size = bigintMult.wordCount;
        words1Size = RoundupSize(words1Size);
        words2Size = RoundupSize(words2Size);
        productreg = new short[RoundupSize(words1Size + words2Size)];
        short[] workspace = new short[words1Size + words2Size];
        productwordCount = productreg.length;
        AsymmetricMultiply(
          productreg,
          0,
          workspace,
          0,
          this.words,
          0,
          words1Size,
          bigintMult.words,
          0,
          words2Size);
      }
      // Recalculate word count
      while (productwordCount != 0 && productreg[productwordCount - 1] == 0) {
        --productwordCount;
      }
      if (needShorten) {
        productreg = ShortenArray(productreg, productwordCount);
      }
      return new EInteger(
        productwordCount,
        productreg,
        this.negative ^ bigintMult.negative);
    }

    /**
     * Gets the value of this object with the sign reversed.
     * @return This object's value with the sign reversed.
     */
    public EInteger Negate() {
      return this.wordCount == 0 ? this : new EInteger(
        this.wordCount,
        this.words,
        !this.negative);
    }

    /**
     * Raises an arbitrary-precision integer to a power.
     * @param powerSmall The exponent to raise to.
     * @return The result. Returns 1 if {@code powerSmall} is 0.
     * @throws IllegalArgumentException The parameter {@code powerSmall} is less
     * than 0.
     */
    public EInteger Pow(int powerSmall) {
      if (powerSmall < 0) {
        throw new IllegalArgumentException("powerSmall (" + powerSmall +
                    ") is less than 0");
      }
      EInteger thisVar = this;
      if (powerSmall == 0) {
        // however 0 to the power of 0 is undefined
        return EInteger.FromInt64(1);
      }
      if (powerSmall == 1) {
        return this;
      }
      if (powerSmall == 2) {
        return thisVar.Multiply(thisVar);
      }
      if (powerSmall == 3) {
        return (thisVar.Multiply(thisVar)).Multiply(thisVar);
      }
      EInteger r = EInteger.FromInt64(1);
      while (powerSmall != 0) {
        if ((powerSmall & 1) != 0) {
          r = r.Multiply(thisVar);
        }
        powerSmall >>= 1;
        if (powerSmall != 0) {
          thisVar = thisVar.Multiply(thisVar);
        }
      }
      return r;
    }

    /**
     * Raises an arbitrary-precision integer to a power, which is given as another
     * arbitrary-precision integer.
     * @param power The exponent to raise to.
     * @return The result. Returns 1 if {@code power} is 0.
     * @throws java.lang.NullPointerException The parameter {@code power} is null.
     * @throws IllegalArgumentException The parameter {@code power} is less than 0.
     */
    public EInteger PowBigIntVar(EInteger power) {
      if (power == null) {
        throw new NullPointerException("power");
      }
      int sign = power.signum();
      if (sign < 0) {
        throw new IllegalArgumentException(
          "sign (" + sign + ") is less than 0");
      }
      EInteger thisVar = this;
      if (sign == 0) {
        return EInteger.FromInt64(1);
      }
      if (power.equals(EInteger.FromInt64(1))) {
        return this;
      }
      if (power.wordCount == 1 && power.words[0] == 2) {
        return thisVar.Multiply(thisVar);
      }
      if (power.wordCount == 1 && power.words[0] == 3) {
        return (thisVar.Multiply(thisVar)).Multiply(thisVar);
      }
      EInteger r = EInteger.FromInt64(1);
      while (!power.isZero()) {
        if (!power.isEven()) {
          r = r.Multiply(thisVar);
        }
        power = power.ShiftRight(1);
        if (!power.isZero()) {
          thisVar = thisVar.Multiply(thisVar);
        }
      }
      return r;
    }

    /**
     * Finds the remainder that results when this instance is divided by the value
     * of an arbitrary-precision integer. The remainder is the value that
     * remains when the absolute value of this object is divided by the
     * absolute value of the other object; the remainder has the same sign
     * (positive or negative) as this object.
     * @param divisor Another arbitrary-precision integer.
     * @return The remainder of the two objects.
     * @throws java.lang.NullPointerException The parameter {@code divisor} is null.
     * @throws ArithmeticException Attempted to divide by zero.
     */
    public EInteger Remainder(EInteger divisor) {
      if (divisor == null) {
        throw new NullPointerException("divisor");
      }
      int words1Size = this.wordCount;
      int words2Size = divisor.wordCount;
      if (words2Size == 0) {
        throw new ArithmeticException();
      }
      if (words1Size < words2Size) {
        // dividend is less than divisor
        return this;
      }
      if (words2Size == 1) {
        short shortRemainder = FastRemainder(
          this.words,
          this.wordCount,
          divisor.words[0]);
        int smallRemainder = ((int)shortRemainder) & 0xffff;
        if (this.negative) {
          smallRemainder = -smallRemainder;
        }
        return EInteger.FromInt64(smallRemainder);
      }
      if (this.PositiveCompare(divisor) < 0) {
        return this;
      }
      words1Size += words1Size & 1;
      words2Size += words2Size & 1;
      short[] remainderReg = new short[RoundupSize((int)words2Size)];
      short[] tempbuf = new short[words1Size + (3 * (words2Size + 2))];
      Divide(
        remainderReg,
        0,
        null,
        0,
        tempbuf,
        0,
        this.words,
        0,
        words1Size,
        divisor.words,
        0,
        words2Size);
      int count = CountWords(remainderReg, remainderReg.length);
      if (count == 0) {
        return EInteger.FromInt64(0);
      }
      remainderReg = ShortenArray(remainderReg, count);
      return new EInteger(count, remainderReg, this.negative);
    }

    /**
     * Returns an arbitrary-precision integer with the bits shifted to the left by
     * a number of bits. A value of 1 doubles this value, a value of 2
     * multiplies it by 4, a value of 3 by 8, a value of 4 by 16, and so on.
     * @param numberBits The number of bits to shift. Can be negative, in which
     * case this is the same as shiftRight with the absolute value of
     * numberBits.
     * @return An arbitrary-precision integer.
     */
    public EInteger ShiftLeft(int numberBits) {
      if (numberBits == 0 || this.wordCount == 0) {
        return this;
      }
      if (numberBits < 0) {
        return (numberBits == Integer.MIN_VALUE) ?
          this.ShiftRight(1).ShiftRight(Integer.MAX_VALUE) :
          this.ShiftRight(-numberBits);
      }
      int numWords = (int)this.wordCount;
      int shiftWords = (int)(numberBits >> 4);
      int shiftBits = (int)(numberBits & 15);
      if (!this.negative) {
        short[] ret = new short[RoundupSize(numWords +
                    BitsToWords((int)numberBits))];
        System.arraycopy(this.words, 0, ret, shiftWords, numWords);
        ShiftWordsLeftByBits(
          ret,
          (int)shiftWords,
          numWords + BitsToWords(shiftBits),
          shiftBits);
        return new EInteger(CountWords(ret, ret.length), ret, false);
      } else {
        short[] ret = new short[RoundupSize(numWords +
                    BitsToWords((int)numberBits))];
        System.arraycopy(this.words, 0, ret, 0, numWords);
        TwosComplement(ret, 0, (int)ret.length);
        ShiftWordsLeftByWords(ret, 0, numWords + shiftWords, shiftWords);
        ShiftWordsLeftByBits(
          ret,
          (int)shiftWords,
          numWords + BitsToWords(shiftBits),
          shiftBits);
        TwosComplement(ret, 0, (int)ret.length);
        return new EInteger(CountWords(ret, ret.length), ret, true);
      }
    }

    /**
     * Returns an arbitrary-precision integer with the bits shifted to the right.
     * For this operation, the arbitrary-precision integer is treated as a
     * two's complement representation. Thus, for negative values, the
     * arbitrary-precision integer is sign-extended.
     * @param numberBits Number of bits to shift right.
     * @return An arbitrary-precision integer.
     */
    public EInteger ShiftRight(int numberBits) {
      if (numberBits == 0 || this.wordCount == 0) {
        return this;
      }
      if (numberBits < 0) {
        return (numberBits == Integer.MIN_VALUE) ?
          this.ShiftLeft(1).ShiftLeft(Integer.MAX_VALUE) :
          this.ShiftLeft(-numberBits);
      }
      int numWords = (int)this.wordCount;
      int shiftWords = (int)(numberBits >> 4);
      int shiftBits = (int)(numberBits & 15);
      short[] ret;
      int retWordCount;
      if (this.negative) {
        ret = new short[this.words.length];
        System.arraycopy(this.words, 0, ret, 0, numWords);
        TwosComplement(ret, 0, (int)ret.length);
        ShiftWordsRightByWordsSignExtend(ret, 0, numWords, shiftWords);
        if (numWords > shiftWords) {
          ShiftWordsRightByBitsSignExtend(
            ret,
            0,
            numWords - shiftWords,
            shiftBits);
        }
        TwosComplement(ret, 0, (int)ret.length);
        retWordCount = ret.length;
      } else {
        if (shiftWords >= numWords) {
          return EInteger.FromInt64(0);
        }
        ret = new short[this.words.length];
        System.arraycopy(this.words, shiftWords, ret, 0, numWords - shiftWords);
        if (shiftBits != 0) {
          ShiftWordsRightByBits(ret, 0, numWords - shiftWords, shiftBits);
        }
        retWordCount = numWords - shiftWords;
      }
      while (retWordCount != 0 &&
             ret[retWordCount - 1] == 0) {
        --retWordCount;
      }
      if (retWordCount == 0) {
        return EInteger.FromInt64(0);
      }
      if (shiftWords > 2) {
        ret = ShortenArray(ret, retWordCount);
      }
      return new EInteger(retWordCount, ret, this.negative);
    }

    /**
     * Finds the square root of this instance&#x27;s value, rounded down.
     * @return The square root of this object's value. Returns 0 if this value is 0
     * or less.
     */
    public EInteger Sqrt() {
      EInteger[] srrem = this.SqrtRemInternal(false);
      return srrem[0];
    }

    /**
     * Calculates the square root and the remainder.
     * @return An array of two arbitrary-precision integers: the first integer is
     * the square root, and the second is the difference between this value
     * and the square of the first integer. Returns two zeros if this value
     * is 0 or less, or one and zero if this value equals 1.
     */
    public EInteger[] SqrtRem() {
      return this.SqrtRemInternal(true);
    }

    /**
     * Subtracts an arbitrary-precision integer from this arbitrary-precision
     * integer.
     * @param subtrahend Another arbitrary-precision integer.
     * @return The difference of the two objects.
     * @throws java.lang.NullPointerException The parameter {@code subtrahend} is
     * null.
     */
    public EInteger Subtract(EInteger subtrahend) {
      if (subtrahend == null) {
        throw new NullPointerException("subtrahend");
      }
      return (this.wordCount == 0) ? subtrahend.Negate() :
        ((subtrahend.wordCount == 0) ? this : this.Add(subtrahend.Negate()));
    }

    /**
     * Returns whether a bit is set in the two's-complement representation of this
     * object's value.
     * @param index Zero based index of the bit to test. 0 means the least
     * significant bit.
     * @return True if a bit is set in the two's-complement representation of this
     * object's value; otherwise, false.
     */
    public boolean GetSignedBit(int index) {
      if (index < 0) {
        throw new IllegalArgumentException("index");
      }
      if (this.wordCount == 0) {
        return false;
      }
      if (this.negative) {
        int tcindex = 0;
        int wordpos = index / 16;
        if (wordpos >= this.words.length) {
          return true;
        }
        while (tcindex < wordpos && this.words[tcindex] == 0) {
          ++tcindex;
        }
        short tc;
        {
          tc = this.words[wordpos];
          if (tcindex == wordpos) {
            --tc;
          }
          tc = (short)~tc;
        }
        return (boolean)(((tc >> (int)(index & 15)) & 1) != 0);
      }
      return this.GetUnsignedBit(index);
    }

    /**
     * Returns a byte array of this integer&#x27;s value. The byte array will take
     * the form of the number's two's-complement representation, using the
     * fewest bytes necessary to store its value unambiguously. If this
     * value is negative, the bits that appear beyond the most significant
     * bit of the number will be all ones. The resulting byte array can be
     * passed to the <code>FromBytes()</code> method (with the same byte order) to
     * reconstruct this integer's value.
     * @param littleEndian If true, the byte order is little-endian, or
     * least-significant-byte first. If false, the byte order is big-endian,
     * or most-significant-byte first.
     * @return A byte array. If this value is 0, returns a byte array with the
     * single element 0.
     */
    public byte[] ToBytes(boolean littleEndian) {
      int sign = this.signum();
      if (sign == 0) {
        return new byte[] { (byte)0  };
      }
      if (sign > 0) {
        int byteCount = this.ByteCount();
        int byteArrayLength = byteCount;
        if (this.GetUnsignedBit((byteCount * 8) - 1)) {
          ++byteArrayLength;
        }
        byte[] bytes = new byte[byteArrayLength];
        int j = 0;
        for (int i = 0; i < byteCount; i += 2, j++) {
          int index = littleEndian ? i : bytes.length - 1 - i;
          int index2 = littleEndian ? i + 1 : bytes.length - 2 - i;
          bytes[index] = (byte)(this.words[j] & 0xff);
          if (index2 >= 0 && index2 < byteArrayLength) {
            bytes[index2] = (byte)((this.words[j] >> 8) & 0xff);
          }
        }
        return bytes;
      } else {
        short[] regdata = new short[this.words.length];
        System.arraycopy(this.words, 0, regdata, 0, this.words.length);
        TwosComplement(regdata, 0, (int)regdata.length);
        int byteCount = regdata.length * 2;
        for (int i = regdata.length - 1; i >= 0; --i) {
          if (regdata[i] == ((short)0xffff)) {
            byteCount -= 2;
          } else if ((regdata[i] & 0xff80) == 0xff80) {
            // signed first byte, 0xff second
            --byteCount;
            break;
          } else if ((regdata[i] & 0x8000) == 0x8000) {
            // signed second byte
            break;
          } else {
            // unsigned second byte
            ++byteCount;
            break;
          }
        }
        if (byteCount == 0) {
          byteCount = 1;
        }
        byte[] bytes = new byte[byteCount];
        bytes[littleEndian ? bytes.length - 1 : 0] = (byte)0xff;
        byteCount = Math.min(byteCount, regdata.length * 2);
        int j = 0;
        for (int i = 0; i < byteCount; i += 2, j++) {
          int index = littleEndian ? i : bytes.length - 1 - i;
          int index2 = littleEndian ? i + 1 : bytes.length - 2 - i;
          bytes[index] = (byte)(regdata[j] & 0xff);
          if (index2 >= 0 && index2 < byteCount) {
            bytes[index2] = (byte)((regdata[j] >> 8) & 0xff);
          }
        }
        return bytes;
      }
    }

    /**
     * Generates a string representing the value of this object, in the given
     * radix.
     * @param radix A radix from 2 through 36. For example, to generate a
     * hexadecimal (base-16) string, specify 16. To generate a decimal
     * (base-10) string, specify 10.
     * @return A string representing the value of this object. If this value is 0,
     * returns "0". If negative, the string will begin with a hyphen/minus
     * ("-"). Depending on the radix, the string will use the basic digits 0
     * to 9 (U + 0030 to U + 0039) and then the basic letters A to Z (U + 0041 to
     * U + 005A). For example, 0-9 in radix 10, and 0-9, then A-F in radix 16.
     * @throws IllegalArgumentException The parameter "index" is less than 0,
     * "endIndex" is less than 0, or either is greater than the string's
     * length, or "endIndex" is less than "index" ; or radix is less than 2
     * or greater than 36.
     */
    public String ToRadixString(int radix) {
      if (radix < 2) {
        throw new IllegalArgumentException("radix (" + radix +
                    ") is less than 2");
      }
      if (radix > 36) {
        throw new IllegalArgumentException("radix (" + radix +
                    ") is more than 36");
      }
      if (this.wordCount == 0) {
        return "0";
      }
      if (radix == 10) {
        // Decimal
        if (this.HasSmallValue()) {
          return this.SmallValueToString();
        }
        short[] tempReg = new short[this.wordCount];
        System.arraycopy(this.words, 0, tempReg, 0, tempReg.length);
        int numWordCount = tempReg.length;
        while (numWordCount != 0 && tempReg[numWordCount - 1] == 0) {
          --numWordCount;
        }
        int i = 0;
        char[] s = new char[(numWordCount << 4) + 1];
        while (numWordCount != 0) {
          if (numWordCount == 1 && tempReg[0] > 0 && tempReg[0] <= 0x7fff) {
            int rest = tempReg[0];
            while (rest != 0) {
              // accurate approximation to rest/10 up to 43698,
              // and rest can go up to 32767
              int newrest = (rest * 26215) >> 18;
              s[i++] = Digits.charAt(rest - (newrest * 10));
              rest = newrest;
            }
            break;
          }
          if (numWordCount == 2 && tempReg[1] > 0 && tempReg[1] <= 0x7fff) {
            int rest = ((int)tempReg[0]) & 0xffff;
            rest |= (((int)tempReg[1]) & 0xffff) << 16;
            while (rest != 0) {
              int newrest = rest / 10;
              s[i++] = Digits.charAt(rest - (newrest * 10));
              rest = newrest;
            }
            break;
          } else {
            int wci = numWordCount;
            short remainderShort = 0;
            int quo, rem;
            // Divide by 10000
            while ((wci--) > 0) {
              int currentDividend = ((int)((((int)tempReg[wci]) &
                    0xffff) | ((int)remainderShort << 16)));
              quo = currentDividend / 10000;
              tempReg[wci] = ((short)quo);
              rem = currentDividend - (10000 * quo);
              remainderShort = ((short)rem);
            }
            int remainderSmall = remainderShort;
            // Recalculate word count
            while (numWordCount != 0 && tempReg[numWordCount - 1] == 0) {
              --numWordCount;
            }
            // accurate approximation to rest/10 up to 16388,
            // and rest can go up to 9999
            int newrest = (remainderSmall * 3277) >> 15;
            s[i++] = Digits.charAt((int)(remainderSmall - (newrest * 10)));
            remainderSmall = newrest;
            newrest = (remainderSmall * 3277) >> 15;
            s[i++] = Digits.charAt((int)(remainderSmall - (newrest * 10)));
            remainderSmall = newrest;
            newrest = (remainderSmall * 3277) >> 15;
            s[i++] = Digits.charAt((int)(remainderSmall - (newrest * 10)));
            remainderSmall = newrest;
            s[i++] = Digits.charAt(remainderSmall);
          }
        }
        ReverseChars(s, 0, i);
        if (this.negative) {
          StringBuilder sb = new StringBuilder(i + 1);
          sb.append('-');
          for (int j = 0; j < i; ++j) {
            sb.append(s[j]);
          }
          return sb.toString();
        }
        return new String(s, 0, i);
      }
      if (radix == 16) {
        // Hex
        StringBuilder sb = new StringBuilder();
        if (this.negative) {
          sb.append('-');
        }
        boolean firstBit = true;
        int word = this.words[this.wordCount - 1];
        for (int i = 0; i < 4; ++i) {
          if (!firstBit || (word & 0xf000) != 0) {
            sb.append(Digits.charAt((word >> 12) & 0x0f));
            firstBit = false;
          }
          word <<= 4;
        }
        for (int j = this.wordCount - 2; j >= 0; --j) {
          word = this.words[j];
          for (int i = 0; i < 4; ++i) {
            sb.append(Digits.charAt((word >> 12) & 0x0f));
            word <<= 4;
          }
        }
        return sb.toString();
      }
      if (radix == 2) {
        // Binary
        StringBuilder sb = new StringBuilder();
        if (this.negative) {
          sb.append('-');
        }
        boolean firstBit = true;
        int word = this.words[this.wordCount - 1];
        for (int i = 0; i < 16; ++i) {
          if (!firstBit || (word & 0x8000) != 0) {
            sb.append((word & 0x8000) == 0 ? '0' : '1');
            firstBit = false;
          }
          word <<= 1;
        }
        for (int j = this.wordCount - 2; j >= 0; --j) {
          word = this.words[j];
          for (int i = 0; i < 16; ++i) {
            sb.append((word & 0x8000) == 0 ? '0' : '1');
            word <<= 1;
          }
        }
        return sb.toString();
      } else {
        // Other radixes
        short[] tempReg = new short[this.wordCount];
        System.arraycopy(this.words, 0, tempReg, 0, tempReg.length);
        int numWordCount = tempReg.length;
        while (numWordCount != 0 && tempReg[numWordCount - 1] == 0) {
          --numWordCount;
        }
        int i = 0;
        char[] s = new char[(numWordCount << 4) + 1];
        while (numWordCount != 0) {
          if (numWordCount == 1 && tempReg[0] > 0 && tempReg[0] <= 0x7fff) {
            int rest = tempReg[0];
            while (rest != 0) {
              int newrest = rest / radix;
              s[i++] = Digits.charAt(rest - (newrest * radix));
              rest = newrest;
            }
            break;
          }
          if (numWordCount == 2 && tempReg[1] > 0 && tempReg[1] <= 0x7fff) {
            int rest = ((int)tempReg[0]) & 0xffff;
            rest |= (((int)tempReg[1]) & 0xffff) << 16;
            while (rest != 0) {
              int newrest = rest / radix;
              s[i++] = Digits.charAt(rest - (newrest * radix));
              rest = newrest;
            }
            break;
          } else {
            int wci = numWordCount;
            short remainderShort = 0;
            int quo, rem;
            // Divide by radix
            while ((wci--) > 0) {
              int currentDividend = ((int)((((int)tempReg[wci]) &
                    0xffff) | ((int)remainderShort << 16)));
              quo = currentDividend / radix;
              tempReg[wci] = ((short)quo);
              rem = currentDividend - (radix * quo);
              remainderShort = ((short)rem);
            }
            int remainderSmall = remainderShort;
            // Recalculate word count
            while (numWordCount != 0 && tempReg[numWordCount - 1] == 0) {
              --numWordCount;
            }
            s[i++] = Digits.charAt(remainderSmall);
          }
        }
        ReverseChars(s, 0, i);
        if (this.negative) {
          StringBuilder sb = new StringBuilder(i + 1);
          sb.append('-');
          for (int j = 0; j < i; ++j) {
            sb.append(s[j]);
          }
          return sb.toString();
        }
        return new String(s, 0, i);
      }
    }

    /**
     * Converts this object to a text string in base 10.
     * @return A string representation of this object. If negative, the string will
     * begin with a minus sign ("-", U+002D). The string will use the basic
     * digits 0 to 9 (U + 0030 to U + 0039).
     */
    @Override public String toString() {
      if (this.isZero()) {
        return "0";
      }
      return this.HasSmallValue() ? this.SmallValueToString() :
        this.ToRadixString(10);
    }

    private static int Add(
      short[] c,
      int cstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart,
      int n) {
      {
        int u;
        u = 0;
        for (int i = 0; i < n; i += 2) {
          u = (((int)words1[astart + i]) & 0xffff) + (((int)words2[bstart +
                    i]) & 0xffff) + (short)(u >> 16);
          c[cstart + i] = (short)u;
          u = (((int)words1[astart + i + 1]) & 0xffff) +
            (((int)words2[bstart + i + 1]) & 0xffff) + (short)(u >> 16);
          c[cstart + i + 1] = (short)u;
        }
        return ((int)u >> 16) & 0xffff;
      }
    }

    private static int AddOneByOne(
      short[] c,
      int cstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart,
      int n) {
      {
        int u;
        u = 0;
        for (int i = 0; i < n; i += 1) {
          u = (((int)words1[astart + i]) & 0xffff) + (((int)words2[bstart +
                    i]) & 0xffff) + (short)(u >> 16);
          c[cstart + i] = (short)u;
        }
        return ((int)u >> 16) & 0xffff;
      }
    }

    private static int AddUnevenSize(
      short[] c,
      int cstart,
      short[] wordsBigger,
      int astart,
      int acount,
      short[] wordsSmaller,
      int bstart,
      int bcount) {
      {
        int u;
        u = 0;
        for (int i = 0; i < bcount; i += 1) {
          u = (((int)wordsBigger[astart + i]) & 0xffff) +
            (((int)wordsSmaller[bstart + i]) & 0xffff) + (short)(u >> 16);
          c[cstart + i] = (short)u;
        }
        for (int i = bcount; i < acount; i += 1) {
          u = (((int)wordsBigger[astart + i]) & 0xffff) + (short)(u >> 16);
          c[cstart + i] = (short)u;
        }
        return ((int)u >> 16) & 0xffff;
      }
    }

    private static int ApproxLogTenOfTwo(int bitlen) {
      int bitlenLow = bitlen & 0xffff;
      int bitlenHigh = (bitlen >> 16) & 0xffff;
      short resultLow = 0;
      short resultHigh = 0;
      {
        int p; short c; int d;
        p = bitlenLow * 0x84fb; d = ((int)p >> 16) & 0xffff; c = (short)d; d
          = ((int)d >> 16) & 0xffff;
        p = bitlenLow * 0x209a;
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = bitlenHigh * 0x84fb;
        p += ((int)c) & 0xffff;
        d += ((int)p >> 16) & 0xffff; c = (short)d; d = ((int)d >> 16) & 0xffff;
        p = bitlenLow * 0x9a;
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = bitlenHigh * 0x209a;
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = ((int)c) & 0xffff; c = (short)p; resultLow = c; c = (short)d; d
          = ((int)d >> 16) & 0xffff;
        p = bitlenHigh * 0x9a;
        p += ((int)c) & 0xffff;
        resultHigh = (short)p;
        int result = ((int)resultLow) & 0xffff;
        result |= (((int)resultHigh) & 0xffff) << 16;
        return (result & 0x7fffffff) >> 9;
      }
    }

    // Multiplies two operands of different sizes
    private static void AsymmetricMultiply(
      short[] resultArr,
      int resultStart,  // uses words1Count + words2Count
      short[] tempArr,
      int tempStart,  // uses words1Count + words2Count
      short[] words1,
      int words1Start,
      int words1Count,
      short[] words2,
      int words2Start,
      int words2Count) {
      // System.out.println("AsymmetricMultiply " + words1Count + " " +
      // words2Count + " [r=" + resultStart + " t=" + tempStart + " a=" +
      // words1Start + " b=" + words2Start + "]");

      if (words1Count == words2Count) {
        if (words1Start == words2Start && words1 == words2) {
          // Both operands have the same value and the same word count
          RecursiveSquare(
            resultArr,
            resultStart,
            tempArr,
            tempStart,
            words1,
            words1Start,
            words1Count);
        } else if (words1Count == 2) {
          // Both operands have a word count of 2
          BaselineMultiply2(
            resultArr,
            resultStart,
            words1,
            words1Start,
            words2,
            words2Start);
        } else {
          // Other cases where both operands have the same word count
          SameSizeMultiply(
            resultArr,
            resultStart,
            tempArr,
            tempStart,
            words1,
            words1Start,
            words2,
            words2Start,
            words1Count);
        }

        return;
      }
      if (words1Count > words2Count) {
        // Ensure that words1 is smaller by swapping if necessary
        short[] tmp1 = words1;
        words1 = words2;
        words2 = tmp1;
        int tmp3 = words1Start;
        words1Start = words2Start;
        words2Start = tmp3;
        int tmp2 = words1Count;
        words1Count = words2Count;
        words2Count = tmp2;
      }

      if (words1Count == 1 || (words1Count == 2 && words1[words1Start + 1] ==
                    0)) {
        switch (words1[words1Start]) {
          case 0:
            // words1 is ValueZero, so result is 0
            java.util.Arrays.fill(resultArr, resultStart, (resultStart)+(words2Count + 2), (short)0);
            return;
          case 1:
            System.arraycopy(
              words2,
              words2Start,
              resultArr,
              resultStart,
              (int)words2Count);
            resultArr[resultStart + words2Count] = (short)0;
            resultArr[resultStart + words2Count + 1] = (short)0;
            return;
          default:
            resultArr[resultStart + words2Count] = LinearMultiply(
              resultArr,
              resultStart,
              words2,
              words2Start,
              words1[words1Start],
              words2Count);
            resultArr[resultStart + words2Count + 1] = (short)0;
            return;
        }
      }
      if (words1Count == 2 && (words2Count & 1) == 0) {
        int a0 = ((int)words1[words1Start]) & 0xffff;
        int a1 = ((int)words1[words1Start + 1]) & 0xffff;
        resultArr[resultStart + words2Count] = (short)0;
        resultArr[resultStart + words2Count + 1] = (short)0;
        AtomicMultiplyOpt(
          resultArr,
          resultStart,
          a0,
          a1,
          words2,
          words2Start,
          0,
          words2Count);
        AtomicMultiplyAddOpt(
          resultArr,
          resultStart,
          a0,
          a1,
          words2,
          words2Start,
          2,
          words2Count);
        return;
      }
      if (words1Count <= 10 && words2Count <= 10) {
        SchoolbookMultiply(
          resultArr,
          resultStart,
          words1,
          words1Start,
          words1Count,
          words2,
          words2Start,
          words2Count);
      } else {
        int wordsRem = words2Count % words1Count;
        int evenmult = (words2Count / words1Count) & 1;
        int i;
        // System.out.println("counts=" + words1Count + "," + words2Count +
        // " res=" + (resultStart + words1Count) + " temp=" + (tempStart +
        // (words1Count << 1)) + " rem=" + wordsRem + " evenwc=" + evenmult);
        if (wordsRem == 0) {
          // words2Count is divisible by words1count
          if (evenmult == 0) {
            SameSizeMultiply(
              resultArr,
              resultStart,
              tempArr,
              tempStart,
              words1,
              words1Start,
              words2,
              words2Start,
              words1Count);
            System.arraycopy(
              resultArr,
              resultStart + words1Count,
              tempArr,
              (int)(tempStart + (words1Count << 1)),
              words1Count);
            for (i = words1Count << 1; i < words2Count; i += words1Count << 1) {
              SameSizeMultiply(
                tempArr,
                tempStart + words1Count + i,
                tempArr,
                tempStart,
                words1,
                words1Start,
                words2,
                words2Start + i,
                words1Count);
            }
            for (i = words1Count; i < words2Count; i += words1Count << 1) {
              SameSizeMultiply(
                resultArr,
                resultStart + i,
                tempArr,
                tempStart,
                words1,
                words1Start,
                words2,
                words2Start + i,
                words1Count);
            }
          } else {
            for (i = 0; i < words2Count; i += words1Count << 1) {
              SameSizeMultiply(
                resultArr,
                resultStart + i,
                tempArr,
                tempStart,
                words1,
                words1Start,
                words2,
                words2Start + i,
                words1Count);
            }
            for (i = words1Count; i < words2Count; i += words1Count << 1) {
              SameSizeMultiply(
                tempArr,
                tempStart + words1Count + i,
                tempArr,
                tempStart,
                words1,
                words1Start,
                words2,
                words2Start + i,
                words1Count);
            }
          }
          if (
            Add(
              resultArr,
              resultStart + words1Count,
              resultArr,
              resultStart + words1Count,
              tempArr,
              tempStart + (words1Count << 1),
              words2Count - words1Count) != 0) {
            Increment(
              resultArr,
              (int)(resultStart + words2Count),
              words1Count,
              (short)1);
          }
        } else if ((words1Count + words2Count) >= (words1Count << 2)) {
          // System.out.println("Chunked Linear Multiply long");
          ChunkedLinearMultiply(
            resultArr,
            resultStart,
            tempArr,
            tempStart,
            words2,
            words2Start,
            words2Count,
            words1,
            words1Start,
            words1Count);
        } else if (words1Count + 1 == words2Count ||
                   (words1Count + 2 == words2Count && words2[words2Start +
                    words2Count - 1] == 0)) {
          java.util.Arrays.fill(resultArr, resultStart, (resultStart)+(words1Count + words2Count), (short)0);
          // Multiply the low parts of each operand
          SameSizeMultiply(
            resultArr,
            resultStart,
            tempArr,
            tempStart,
            words1,
            words1Start,
            words2,
            words2Start,
            words1Count);
          // Multiply the high parts
          // while adding carry from the high part of the product
          short carry = LinearMultiplyAdd(
            resultArr,
            resultStart + words1Count,
            words1,
            words1Start,
            words2[words2Start + words1Count],
            words1Count);
          resultArr[resultStart + words1Count + words1Count] = carry;
        } else {
          short[] t2 = new short[words1Count << 2];
          // System.out.println("Chunked Linear Multiply Short");
          ChunkedLinearMultiply(
            resultArr,
            resultStart,
            t2,
            0,
            words2,
            words2Start,
            words2Count,
            words1,
            words1Start,
            words1Count);
        }
      }
    }

    private static void AtomicMultiplyAddOpt(
      short[] c,
      int valueCstart,
      int valueA0,
      int valueA1,
      short[] words2,
      int words2Start,
      int istart,
      int iend) {
      short s;
      int d;
      int first1MinusFirst0 = ((int)valueA1 - valueA0) & 0xffff;
      valueA1 &= 0xffff;
      valueA0 &= 0xffff;
      {
        if (valueA1 >= valueA0) {
          for (int i = istart; i < iend; i += 4) {
            int b0 = ((int)words2[words2Start + i]) & 0xffff;
            int b1 = ((int)words2[words2Start + i + 1]) & 0xffff;
            int csi = valueCstart + i;
            if (b0 >= b1) {
              s = (short)0;
              d = first1MinusFirst0 * (((int)b0 - b1) & 0xffff);
            } else {
              s = (short)first1MinusFirst0;
              d = (((int)s) & 0xffff) * (((int)b0 - b1) & 0xffff);
            }
            int valueA0B0 = valueA0 * b0;
            int a0b0high = (valueA0B0 >> 16) & 0xffff;
            int tempInt;
            tempInt = valueA0B0 + (((int)c[csi]) & 0xffff);
            c[csi] = (short)(((int)tempInt) & 0xffff);

            int valueA1B1 = valueA1 * b1;
            int a1b1low = valueA1B1 & 0xffff;
            int a1b1high = ((int)(valueA1B1 >> 16)) & 0xffff;
            tempInt = (((int)(tempInt >> 16)) & 0xffff) + (((int)valueA0B0) &
                    0xffff) + (((int)d) & 0xffff) + a1b1low +
              (((int)c[csi + 1]) & 0xffff);
            c[csi + 1] = (short)(((int)tempInt) & 0xffff);

            tempInt = (((int)(tempInt >> 16)) & 0xffff) + a1b1low + a0b0high +
              (((int)(d >> 16)) & 0xffff) +
              a1b1high - (((int)s) & 0xffff) + (((int)c[csi + 2]) & 0xffff);
            c[csi + 2] = (short)(((int)tempInt) & 0xffff);

            tempInt = (((int)(tempInt >> 16)) & 0xffff) + a1b1high +
              (((int)c[csi + 3]) & 0xffff);
            c[csi + 3] = (short)(((int)tempInt) & 0xffff);
            if ((tempInt >> 16) != 0) {
              ++c[csi + 4];
              c[csi + 5] += (short)((c[csi + 4] == 0) ? 1 : 0);
            }
          }
        } else {
          for (int i = istart; i < iend; i += 4) {
            int valueB0 = ((int)words2[words2Start + i]) & 0xffff;
            int valueB1 = ((int)words2[words2Start + i + 1]) & 0xffff;
            int csi = valueCstart + i;
            if (valueB0 > valueB1) {
              s = (short)(((int)valueB0 - valueB1) & 0xffff);
              d = first1MinusFirst0 * (((int)s) & 0xffff);
            } else {
              s = (short)0;
              d = (((int)valueA0 - valueA1) & 0xffff) * (((int)valueB1 -
                    valueB0) & 0xffff);
            }
            int valueA0B0 = valueA0 * valueB0;
            int a0b0high = (valueA0B0 >> 16) & 0xffff;
            int tempInt;
            tempInt = valueA0B0 + (((int)c[csi]) & 0xffff);
            c[csi] = (short)(((int)tempInt) & 0xffff);

            int valueA1B1 = valueA1 * valueB1;
            int a1b1low = valueA1B1 & 0xffff;
            int a1b1high = (valueA1B1 >> 16) & 0xffff;
            tempInt = (((int)(tempInt >> 16)) & 0xffff) + (((int)valueA0B0) &
                    0xffff) + (((int)d) & 0xffff) + a1b1low +
              (((int)c[csi + 1]) & 0xffff);
            c[csi + 1] = (short)(((int)tempInt) & 0xffff);

            tempInt = (((int)(tempInt >> 16)) & 0xffff) + a1b1low + a0b0high +
              (((int)(d >> 16)) & 0xffff) +
              a1b1high - (((int)s) & 0xffff) + (((int)c[csi + 2]) & 0xffff);
            c[csi + 2] = (short)(((int)tempInt) & 0xffff);

            tempInt = (((int)(tempInt >> 16)) & 0xffff) + a1b1high +
              (((int)c[csi + 3]) & 0xffff);
            c[csi + 3] = (short)(((int)tempInt) & 0xffff);
            if ((tempInt >> 16) != 0) {
              ++c[csi + 4];
              c[csi + 5] += (short)((c[csi + 4] == 0) ? 1 : 0);
            }
          }
        }
      }
    }

    private static void AtomicMultiplyOpt(
      short[] c,
      int valueCstart,
      int valueA0,
      int valueA1,
      short[] words2,
      int words2Start,
      int istart,
      int iend) {
      short s;
      int d;
      int first1MinusFirst0 = ((int)valueA1 - valueA0) & 0xffff;
      valueA1 &= 0xffff;
      valueA0 &= 0xffff;
      {
        if (valueA1 >= valueA0) {
          for (int i = istart; i < iend; i += 4) {
            int valueB0 = ((int)words2[words2Start + i]) & 0xffff;
            int valueB1 = ((int)words2[words2Start + i + 1]) & 0xffff;
            int csi = valueCstart + i;
            if (valueB0 >= valueB1) {
              s = (short)0;
              d = first1MinusFirst0 * (((int)valueB0 - valueB1) & 0xffff);
            } else {
              s = (short)first1MinusFirst0;
              d = (((int)s) & 0xffff) * (((int)valueB0 - valueB1) & 0xffff);
            }
            int valueA0B0 = valueA0 * valueB0;
            c[csi] = (short)(((int)valueA0B0) & 0xffff);
            int a0b0high = (valueA0B0 >> 16) & 0xffff;
            int valueA1B1 = valueA1 * valueB1;
            int tempInt;
            tempInt = a0b0high + (((int)valueA0B0) & 0xffff) + (((int)d) &
                    0xffff) + (((int)valueA1B1) & 0xffff);
            c[csi + 1] = (short)(((int)tempInt) & 0xffff);

            tempInt = valueA1B1 + (((int)(tempInt >> 16)) & 0xffff) +
              a0b0high + (((int)(d >> 16)) & 0xffff) + (((int)(valueA1B1 >>
                    16)) & 0xffff) - (((int)s) & 0xffff);

            c[csi + 2] = (short)(((int)tempInt) & 0xffff);
            c[csi + 3] = (short)(((int)(tempInt >> 16)) & 0xffff);
          }
        } else {
          for (int i = istart; i < iend; i += 4) {
            int valueB0 = ((int)words2[words2Start + i]) & 0xffff;
            int valueB1 = ((int)words2[words2Start + i + 1]) & 0xffff;
            int csi = valueCstart + i;
            if (valueB0 > valueB1) {
              s = (short)(((int)valueB0 - valueB1) & 0xffff);
              d = first1MinusFirst0 * (((int)s) & 0xffff);
            } else {
              s = (short)0;
              d = (((int)valueA0 - valueA1) & 0xffff) * (((int)valueB1 -
                    valueB0) & 0xffff);
            }
            int valueA0B0 = valueA0 * valueB0;
            int a0b0high = (valueA0B0 >> 16) & 0xffff;
            c[csi] = (short)(((int)valueA0B0) & 0xffff);

            int valueA1B1 = valueA1 * valueB1;
            int tempInt;
            tempInt = a0b0high + (((int)valueA0B0) & 0xffff) + (((int)d) &
                    0xffff) + (((int)valueA1B1) & 0xffff);
            c[csi + 1] = (short)(((int)tempInt) & 0xffff);

            tempInt = valueA1B1 + (((int)(tempInt >> 16)) & 0xffff) +
              a0b0high + (((int)(d >> 16)) & 0xffff) + (((int)(valueA1B1 >>
                    16)) & 0xffff) - (((int)s) & 0xffff);

            c[csi + 2] = (short)(((int)tempInt) & 0xffff);
            c[csi + 3] = (short)(((int)(tempInt >> 16)) & 0xffff);
          }
        }
      }
    }
    //---------------------
    // Baseline multiply
    //---------------------
    private static void BaselineMultiply2(
      short[] result,
      int rstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart) {
      {
        int p; short c; int d;
        int a0 = ((int)words1[astart]) & 0xffff;
        int a1 = ((int)words1[astart + 1]) & 0xffff;
        int b0 = ((int)words2[bstart]) & 0xffff;
        int b1 = ((int)words2[bstart + 1]) & 0xffff;
        p = a0 * b0; c = (short)p; d = ((int)p >> 16) & 0xffff;
        result[rstart] = c; c = (short)d; d = ((int)d >> 16) & 0xffff;
        p = a0 * b1;
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = a1 * b0;
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; result[rstart + 1] = c;
        p = a1 * b1;
        p += d; result[rstart + 2] = (short)p; result[rstart + 3] = (short)(p >>
                    16);
      }
    }

    private static void BaselineMultiply4(
      short[] result,
      int rstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart) {
      {
        int SMask = ShortMask;
        int p; short c; int d;
        int a0 = ((int)words1[astart]) & SMask;
        int b0 = ((int)words2[bstart]) & SMask;
        p = a0 * b0; c = (short)p; d = ((int)p >> 16) & SMask;
        result[rstart] = c; c = (short)d; d = ((int)d >> 16) & SMask;
        p = a0 * (((int)words2[bstart + 1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * b0;
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 1] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = a0 * (((int)words2[bstart + 2]) & SMask);

        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * b0;
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 2] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = a0 * (((int)words2[bstart + 3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;

        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * b0;
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 3] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 4] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 5] = c;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += d; result[rstart + 6] = (short)p; result[rstart + 7] = (short)(p >>
                    16);
      }
    }

    private static void BaselineMultiply8(
      short[] result,
      int rstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart) {
      {
        int p; short c; int d;
        int SMask = ShortMask;
        p = (((int)words1[astart]) & SMask) * (((int)words2[bstart]) &
                    SMask); c = (short)p; d = ((int)p >> 16) &
          SMask;
        result[rstart] = c; c = (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart]) & SMask) * (((int)words2[bstart + 1]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 1] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart]) & SMask) * (((int)words2[bstart + 2]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 2] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart]) & SMask) * (((int)words2[bstart + 3]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 3] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart]) & SMask) * (((int)words2[bstart + 4]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 4]) & SMask) * (((int)words2[bstart]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 4] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart]) & SMask) * (((int)words2[bstart + 5]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    4]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 4]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 5]) & SMask) * (((int)words2[bstart]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 5] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart]) & SMask) * (((int)words2[bstart + 6]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    5]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    4]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 4]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 5]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 6]) & SMask) * (((int)words2[bstart]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 6] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart]) & SMask) * (((int)words2[bstart + 7]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    6]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    5]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    4]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 4]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 5]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 6]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 7]) & SMask) * (((int)words2[bstart]) &
                    SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 7] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart + 1]) & SMask) * (((int)words2[bstart +
                    7]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    6]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    5]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 4]) & SMask) * (((int)words2[bstart +
                    4]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 5]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 6]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 7]) & SMask) * (((int)words2[bstart +
                    1]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 8] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart + 2]) & SMask) * (((int)words2[bstart +
                    7]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    6]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 4]) & SMask) * (((int)words2[bstart +
                    5]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 5]) & SMask) * (((int)words2[bstart +
                    4]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 6]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 7]) & SMask) * (((int)words2[bstart +
                    2]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 9] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart + 3]) & SMask) * (((int)words2[bstart +
                    7]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 4]) & SMask) * (((int)words2[bstart +
                    6]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 5]) & SMask) * (((int)words2[bstart +
                    5]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 6]) & SMask) * (((int)words2[bstart +
                    4]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 7]) & SMask) * (((int)words2[bstart +
                    3]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 10] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart + 4]) & SMask) * (((int)words2[bstart +
                    7]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 5]) & SMask) * (((int)words2[bstart +
                    6]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 6]) & SMask) * (((int)words2[bstart +
                    5]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 7]) & SMask) * (((int)words2[bstart +
                    4]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 11] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart + 5]) & SMask) * (((int)words2[bstart +
                    7]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 6]) & SMask) * (((int)words2[bstart +
                    6]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 7]) & SMask) * (((int)words2[bstart +
                    5]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 12] = c; c =
          (short)d; d = ((int)d >> 16) & SMask;
        p = (((int)words1[astart + 6]) & SMask) * (((int)words2[bstart +
                    7]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask;
        p = (((int)words1[astart + 7]) & SMask) * (((int)words2[bstart +
                    6]) & SMask);
        p += ((int)c) & SMask; c = (short)p;
        d += ((int)p >> 16) & SMask; result[rstart + 13] = c;
        p = (((int)words1[astart + 7]) & SMask) * (((int)words2[bstart +
                    7]) & SMask);
        p += d; result[rstart + 14] = (short)p; result[rstart + 15] =
          (short)(p >> 16);
      }
    }
    //-----------------------------
    // Baseline Square
    //-----------------------------
    private static void BaselineSquare2(
      short[] result,
      int rstart,
      short[] words1,
      int astart) {
      {
        int p; short c; int d; int e;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart]) &
                    0xffff); result[rstart] = (short)p; e = ((int)p >>
                    16) & 0xffff;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 1]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<=
          1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 1] = c;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    1]) & 0xffff);
        p += e; result[rstart + 2] = (short)p; result[rstart + 3] = (short)(p >>
                    16);
      }
    }

    private static void BaselineSquare4(
      short[] result,
      int rstart,
      short[] words1,
      int astart) {
      {
        int p; short c; int d; int e;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart]) &
                    0xffff); result[rstart] = (short)p; e = ((int)p >>
                    16) & 0xffff;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 1]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<=
          1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 1] = c;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 2]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<=
          1;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    1]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 2] = c;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 3]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    2]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 3] = c;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    3]) & 0xffff); c = (short)p; d = ((int)p >>
                    16) & 0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<= 1;
        p = (((int)words1[astart + 2]) & 0xffff) * (((int)words1[astart +
                    2]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 4] = c;
        p = (((int)words1[astart + 2]) & 0xffff) * (((int)words1[astart +
                    3]) & 0xffff); c = (short)p; d = ((int)p >>
                    16) & 0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<= 1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + (2 * 4) - 3] = c;
        p = (((int)words1[astart + 3]) & 0xffff) * (((int)words1[astart +
                    3]) & 0xffff);
        p += e; result[rstart + 6] = (short)p; result[rstart + 7] = (short)(p >>
                    16);
      }
    }

    private static void BaselineSquare8(
      short[] result,
      int rstart,
      short[] words1,
      int astart) {
      {
        int p; short c; int d; int e;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart]) &
                    0xffff); result[rstart] = (short)p; e = ((int)p >>
                    16) & 0xffff;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 1]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<=
          1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 1] = c;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 2]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<=
          1;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    1]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 2] = c;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 3]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    2]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 3] = c;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 4]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    3]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        p = (((int)words1[astart + 2]) & 0xffff) * (((int)words1[astart +
                    2]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 4] = c;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 5]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    4]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = (((int)words1[astart + 2]) & 0xffff) * (((int)words1[astart +
                    3]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 5] = c;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 6]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    5]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = (((int)words1[astart + 2]) & 0xffff) * (((int)words1[astart +
                    4]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        p = (((int)words1[astart + 3]) & 0xffff) * (((int)words1[astart +
                    3]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 6] = c;
        p = (((int)words1[astart]) & 0xffff) * (((int)words1[astart + 7]) &
                    0xffff); c = (short)p; d = ((int)p >> 16) &
          0xffff;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    6]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = (((int)words1[astart + 2]) & 0xffff) * (((int)words1[astart +
                    5]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = (((int)words1[astart + 3]) & 0xffff) * (((int)words1[astart +
                    4]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 7] = c;
        p = (((int)words1[astart + 1]) & 0xffff) * (((int)words1[astart +
                    7]) & 0xffff); c = (short)p; d = ((int)p >>
                    16) & 0xffff;
        p = (((int)words1[astart + 2]) & 0xffff) * (((int)words1[astart +
                    6]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = (((int)words1[astart + 3]) & 0xffff) * (((int)words1[astart +
                    5]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        p = (((int)words1[astart + 4]) & 0xffff) * (((int)words1[astart +
                    4]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 8] = c;
        p = (((int)words1[astart + 2]) & 0xffff) * (((int)words1[astart +
                    7]) & 0xffff); c = (short)p; d = ((int)p >>
                    16) & 0xffff;
        p = (((int)words1[astart + 3]) & 0xffff) * (((int)words1[astart +
                    6]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        p = (((int)words1[astart + 4]) & 0xffff) * (((int)words1[astart +
                    5]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 9] = c;
        p = (((int)words1[astart + 3]) & 0xffff) * (((int)words1[astart +
                    7]) & 0xffff); c = (short)p; d = ((int)p >>
                    16) & 0xffff;
        p = (((int)words1[astart + 4]) & 0xffff) * (((int)words1[astart +
                    6]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        p = (((int)words1[astart + 5]) & 0xffff) * (((int)words1[astart +
                    5]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 10] = c;
        p = (((int)words1[astart + 4]) & 0xffff) * (((int)words1[astart +
                    7]) & 0xffff); c = (short)p; d = ((int)p >>
                    16) & 0xffff;
        p = (((int)words1[astart + 5]) & 0xffff) * (((int)words1[astart +
                    6]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff; d = (int)((d << 1) + (((int)c >> 15) &
                    1)); c <<= 1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 11] = c;
        p = (((int)words1[astart + 5]) & 0xffff) * (((int)words1[astart +
                    7]) & 0xffff); c = (short)p; d = ((int)p >>
                    16) & 0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<= 1;
        p = (((int)words1[astart + 6]) & 0xffff) * (((int)words1[astart +
                    6]) & 0xffff);
        p += ((int)c) & 0xffff; c = (short)p;
        d += ((int)p >> 16) & 0xffff;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 12] = c;
        p = (((int)words1[astart + 6]) & 0xffff) * (((int)words1[astart +
                    7]) & 0xffff); c = (short)p; d = ((int)p >>
                    16) & 0xffff;
        d = (int)((d << 1) + (((int)c >> 15) & 1)); c <<= 1;
        e += ((int)c) & 0xffff; c = (short)e; e = d + (((int)e >> 16) &
                    0xffff); result[rstart + 13] = c;
        p = (((int)words1[astart + 7]) & 0xffff) * (((int)words1[astart +
                    7]) & 0xffff);
        p += e; result[rstart + 14] = (short)p; result[rstart + 15] =
          (short)(p >> 16);
      }
    }

    private static int BitPrecision(short numberValue) {
      if (numberValue == 0) {
        return 0;
      }
      int i = 16;
      {
        if ((numberValue >> 8) == 0) {
          numberValue <<= 8;
          i -= 8;
        }

        if ((numberValue >> 12) == 0) {
          numberValue <<= 4;
          i -= 4;
        }

        if ((numberValue >> 14) == 0) {
          numberValue <<= 2;
          i -= 2;
        }

        if ((numberValue >> 15) == 0) {
          --i;
        }
      }
      return i;
    }

    private static int BitsToWords(int bitCount) {
      return (bitCount + 15) >> 4;
    }

    private static void ChunkedLinearMultiply(
      short[] productArr,
      int cstart,
      short[] tempArr,
      int tempStart,  // uses bcount*4 space
      short[] words1,
      int astart,
      int acount,  // Equal size or longer
      short[] words2,
      int bstart,
      int bcount) {
      {
        int carryPos = 0;
        // Set carry to ValueZero
        java.util.Arrays.fill(productArr, cstart, (cstart)+(bcount), (short)0);
        for (int i = 0; i < acount; i += bcount) {
          int diff = acount - i;
          if (diff > bcount) {
            SameSizeMultiply(
              tempArr,
              tempStart,
              tempArr,
              tempStart + bcount + bcount,
              words1,
              astart + i,
              words2,
              bstart,
              bcount);
            // Add carry
            AddUnevenSize(
              tempArr,
              tempStart,
              tempArr,
              tempStart,
              bcount + bcount,
              productArr,
              cstart + carryPos,
              bcount);
            // Copy product and carry
            System.arraycopy(
              tempArr,
              tempStart,
              productArr,
              cstart + i,
              bcount + bcount);
            carryPos += bcount;
          } else {
            AsymmetricMultiply(
              tempArr,
              tempStart,  // uses diff + bcount space
              tempArr,
              tempStart + diff + bcount,  // uses diff + bcount
              words1,
              astart + i,
              diff,
              words2,
              bstart,
              bcount);
            // Add carry
            AddUnevenSize(
              tempArr,
              tempStart,
              tempArr,
              tempStart,
              diff + bcount,
              productArr,
              cstart + carryPos,
              bcount);
            // Copy product without carry
            System.arraycopy(
              tempArr,
              tempStart,
              productArr,
              cstart + i,
              diff + bcount);
          }
        }
      }
    }

    private static short[] CleanGrow(short[] a, int size) {
      if (size > a.length) {
        short[] newa = new short[size];
        System.arraycopy(a, 0, newa, 0, a.length);
        return newa;
      }
      return a;
    }

    private static int Compare(
      short[] words1,
      int astart,
      short[] words2,
      int bstart,
      int n) {
      while ((n--) != 0) {
        int an = ((int)words1[astart + n]) & 0xffff;
        int bn = ((int)words2[bstart + n]) & 0xffff;
        if (an > bn) {
          return 1;
        }
        if (an < bn) {
          return -1;
        }
      }
      return 0;
    }

    /*
    private static int CompareUnevenSize(short[] words1,
      int astart, int acount, short[] words2, int bstart,
      int bcount) {
      int n = acount;
      if (acount > bcount) {
        while ((acount--) != bcount) {
          if (words1[astart + acount] != 0) {
            return 1;
          }
        }
        n = bcount;
      } else if (bcount > acount) {
        while ((bcount--) != acount) {
          if (words1[astart + acount] != 0) {
            return -1;
          }
        }
        n = acount;
      }
      while ((n--) != 0) {
        int an = ((int)words1[astart + n]) & 0xffff;
        int bn = ((int)words2[bstart + n]) & 0xffff;
        if (an > bn) {
          return 1;
        } else if (an < bn) {
          return -1;
        }
      }
      return 0;
    }
     */

    private static int CompareWithOneBiggerWords1(
      short[] words1,
      int astart,
      short[] words2,
      int bstart,
      int words1Count) {
      // NOTE: Assumes that words2's count is 1 less
      if (words1[astart + words1Count - 1] != 0) {
        return 1;
      }
      int w1c = words1Count;
      --w1c;
      while ((w1c--) != 0) {
        int an = ((int)words1[astart + w1c]) & 0xffff;
        int bn = ((int)words2[bstart + w1c]) & 0xffff;
        if (an > bn) {
          return 1;
        }
        if (an < bn) {
          return -1;
        }
      }
      return 0;
    }

    private static int CountWords(short[] array, int n) {
      while (n != 0 && array[n - 1] == 0) {
        --n;
      }
      return (int)n;
    }

    private static int Decrement(
      short[] words1,
      int words1Start,
      int n,
      short words2) {
      {
        short tmp = words1[words1Start];
        words1[words1Start] = (short)(tmp - words2);
        if ((((int)words1[words1Start]) & 0xffff) <= (((int)tmp) & 0xffff)) {
          return 0;
        }
        for (int i = 1; i < n; ++i) {
          tmp = words1[words1Start + i];
          --words1[words1Start + i];
          if (tmp != 0) {
            return 0;
          }
        }
        return 1;
      }
    }

    private static void Divide(
      short[] remainderArr,
      int remainderStart,  // remainder
      short[] quotientArr,
      int quotientStart,  // quotient
      short[] tempArr,
      int tempStart,  // scratch space
      short[] words1,
      int words1Start,
      int words1Count,  // dividend
      short[] words2,
      int words2Start,
      int words2Count) {
      // set up temporary work space

      if (words2Count == 0) {
        throw new ArithmeticException("division by ValueZero");
      }
      if (words2Count == 1) {
        if (words2[words2Start] == 0) {
          throw new ArithmeticException("division by ValueZero");
        }
        int smallRemainder = ((int)FastDivideAndRemainder(
          quotientArr,
          quotientStart,
          words1,
          words1Start,
          words1Count,
          words2[words2Start])) & 0xffff;
        remainderArr[remainderStart] = (short)smallRemainder;
        return;
      }

      short[] quot = quotientArr;
      if (quotientArr == null) {
        quot = new short[2];
      }
      int valueTBstart = (int)(tempStart + (words1Count + 2));
      int valueTPstart = (int)(tempStart + (words1Count + 2 + words2Count));
      {
        // copy words2 into TB and normalize it so that TB has highest bit
        // set to 1
        int shiftWords = (short)(words2[words2Start + words2Count - 1] == 0 ?
                    1 : 0);
        tempArr[valueTBstart] = (short)0;
        tempArr[valueTBstart + words2Count - 1] = (short)0;
        System.arraycopy(
          words2,
          words2Start,
          tempArr,
          (int)(valueTBstart + shiftWords),
          words2Count - shiftWords);
        short shiftBits = (short)((short)16 - BitPrecision(tempArr[valueTBstart +
                    words2Count - 1]));
        ShiftWordsLeftByBits(
          tempArr,
          valueTBstart,
          words2Count,
          shiftBits);
        // copy words1 into valueTA and normalize it
        tempArr[0] = (short)0;
        tempArr[words1Count] = (short)0;
        tempArr[words1Count + 1] = (short)0;
        System.arraycopy(
          words1,
          words1Start,
          tempArr,
          (int)(tempStart + shiftWords),
          words1Count);
        ShiftWordsLeftByBits(
          tempArr,
          tempStart,
          words1Count + 2,
          shiftBits);

        if (tempArr[tempStart + words1Count + 1] == 0 &&
            (((int)tempArr[tempStart + words1Count]) & 0xffff) <= 1) {
          if (quotientArr != null) {
            quotientArr[quotientStart + words1Count - words2Count + 1] =
              (short)0;
            quotientArr[quotientStart + words1Count - words2Count] = (short)0;
          }
          while (
            tempArr[words1Count] != 0 || Compare(
              tempArr,
              (int)(tempStart + words1Count - words2Count),
              tempArr,
              valueTBstart,
              words2Count) >= 0) {
            tempArr[words1Count] -= (
              short)Subtract(
              tempArr,
              tempStart + words1Count - words2Count,
              tempArr,
              tempStart + words1Count - words2Count,
              tempArr,
              valueTBstart,
              words2Count);
            if (quotientArr != null) {
              quotientArr[quotientStart + words1Count - words2Count] +=
                (short)1;
            }
          }
        } else {
          words1Count += 2;
        }

        short valueBT0 = (short)(tempArr[valueTBstart + words2Count - 2] +
                    (short)1);
        short valueBT1 = (short)(tempArr[valueTBstart + words2Count - 1] +
                    (short)(valueBT0 == (short)0 ? 1 : 0));

        // start reducing valueTA mod TB, 2 words at a time
        short[] valueTAtomic = new short[4];
        for (int i = words1Count - 2; i >= words2Count; i -= 2) {
          int qs = (quotientArr == null) ? 0 : quotientStart + i - words2Count;
          DivideFourWordsByTwo(
            quot,
            qs,
            tempArr,
            tempStart + i - 2,
            valueBT0,
            valueBT1,
            valueTAtomic);
          // now correct the underestimated quotient
          int valueRstart2 = tempStart + i - words2Count;
          int n = words2Count;
          {
            int quotient0 = quot[qs];
            int quotient1 = quot[qs + 1];
            if (quotient1 == 0) {
              short carry = LinearMultiply(
                tempArr,
                valueTPstart,
                tempArr,
                valueTBstart,
                (short)quotient0,
                n);
              tempArr[valueTPstart + n] = carry;
              tempArr[valueTPstart + n + 1] = 0;
            } else if (n == 2) {
              BaselineMultiply2(
                tempArr,
                valueTPstart,
                quot,
                qs,
                tempArr,
                valueTBstart);
            } else {
              tempArr[valueTPstart + n] = (short)0;
              tempArr[valueTPstart + n + 1] = (short)0;
              quotient0 &= 0xffff;
              quotient1 &= 0xffff;
              AtomicMultiplyOpt(
                tempArr,
                valueTPstart,
                quotient0,
                quotient1,
                tempArr,
                valueTBstart,
                0,
                n);
              AtomicMultiplyAddOpt(
                tempArr,
                valueTPstart,
                quotient0,
                quotient1,
                tempArr,
                valueTBstart,
                2,
                n);
            }
            Subtract(
              tempArr,
              valueRstart2,
              tempArr,
              valueRstart2,
              tempArr,
              valueTPstart,
              n + 2);
            while (tempArr[valueRstart2 + n] != 0 || Compare(
              tempArr,
              valueRstart2,
              tempArr,
              valueTBstart,
              n) >= 0) {
              tempArr[valueRstart2 + n] -= (
                short)Subtract(
                tempArr,
                valueRstart2,
                tempArr,
                valueRstart2,
                tempArr,
                valueTBstart,
                n);
              if (quotientArr != null) {
                ++quotientArr[qs];
                quotientArr[qs + 1] += (short)((quotientArr[qs] == 0) ? 1 : 0);
              }
            }
          }
        }
        if (remainderArr != null) {  // If the remainder is non-null
          // copy valueTA into result, and denormalize it
          System.arraycopy(
            tempArr,
            (int)(tempStart + shiftWords),
            remainderArr,
            remainderStart,
            words2Count);
          ShiftWordsRightByBits(
            remainderArr,
            remainderStart,
            words2Count,
            shiftBits);
        }
      }
    }

    private static short Divide32By16(
      int dividendLow,
      short divisorShort,
      boolean returnRemainder) {
      int tmpInt;
      int dividendHigh = 0;
      int intDivisor = ((int)divisorShort) & 0xffff;
      for (int i = 0; i < 32; ++i) {
        tmpInt = dividendHigh >> 31;
        dividendHigh <<= 1;
        dividendHigh = ((int)(dividendHigh | ((int)((dividendLow >>
                    31) & 1))));
        dividendLow <<= 1;
        tmpInt |= dividendHigh;
        // unsigned greater-than-or-equal check
        if (((tmpInt >> 31) != 0) || (tmpInt >= intDivisor)) {
          {
            dividendHigh -= intDivisor;
            ++dividendLow;
          }
        }
      }
      return returnRemainder ? ((short)(((int)dividendHigh) &
                    0xffff)) : ((short)(((int)dividendLow) &
                    0xffff));
    }

    private static void DivideFourWordsByTwo(
      short[] quotient,
      int quotientStart,
      short[] words1,
      int words1Start,
      short word2A,
      short word2B,
      short[] temp) {
      if (word2A == 0 && word2B == 0) {
        // if divisor is 0, we assume divisor.compareTo(EInteger.FromInt64(2)) == 0**32
        quotient[quotientStart] = words1[words1Start + 2];
        quotient[quotientStart + 1] = words1[words1Start + 3];
      } else {
        temp[0] = words1[words1Start];
        temp[1] = words1[words1Start + 1];
        temp[2] = words1[words1Start + 2];
        temp[3] = words1[words1Start + 3];
        short valueQ1 = DivideThreeWordsByTwo(temp, 1, word2A, word2B);
        short valueQ0 = DivideThreeWordsByTwo(temp, 0, word2A, word2B);
        quotient[quotientStart] = valueQ0;
        quotient[quotientStart + 1] = valueQ1;
      }
    }

    private static short DivideThreeWordsByTwo(
      short[] words1,
      int words1Start,
      short valueB0,
      short valueB1) {
      short valueQ;
      {
        valueQ = ((short)(valueB1 + 1) == 0) ? words1[words1Start + 2] :
          ((valueB1 != 0) ? DivideUnsigned(
            MakeUint(
              words1[words1Start + 1],
              words1[words1Start + 2]),
            (short)(((int)valueB1 + 1) & 0xffff)) : DivideUnsigned(
             MakeUint(words1[words1Start], words1[words1Start + 1]),
             valueB0));

        int valueQint = ((int)valueQ) & 0xffff;
        int valueB0int = ((int)valueB0) & 0xffff;
        int valueB1int = ((int)valueB1) & 0xffff;
        int p = valueB0int * valueQint;
        int u = (((int)words1[words1Start]) & 0xffff) - (p & 0xffff);
        words1[words1Start] = GetLowHalf(u);
        u = (((int)words1[words1Start + 1]) & 0xffff) - ((p >> 16) & 0xffff) -
          (((int)GetHighHalfAsBorrow(u)) & 0xffff) - (valueB1int * valueQint);
        words1[words1Start + 1] = GetLowHalf(u);
        words1[words1Start + 2] += GetHighHalf(u);
        while (words1[words1Start + 2] != 0 ||
               (((int)words1[words1Start + 1]) & 0xffff) > (((int)valueB1) &
                    0xffff) || (words1[words1Start + 1] == valueB1 &&
                    (((int)words1[words1Start]) & 0xffff) >=
                    (((int)valueB0) & 0xffff))) {
          u = (((int)words1[words1Start]) & 0xffff) - valueB0int;
          words1[words1Start] = GetLowHalf(u);
          u = (((int)words1[words1Start + 1]) & 0xffff) - valueB1int -
            (((int)GetHighHalfAsBorrow(u)) & 0xffff);
          words1[words1Start + 1] = GetLowHalf(u);
          words1[words1Start + 2] += GetHighHalf(u);
          ++valueQ;
        }
      }
      return valueQ;
    }

    private static short DivideUnsigned(int x, short y) {
      {
        if ((x >> 31) == 0) {
          // x is already nonnegative
          int iy = ((int)y) & 0xffff;
          return (short)(((int)x / iy) & 0xffff);
        }
        return Divide32By16(x, y, false);
      }
    }

    private static void FastDivide(
      short[] quotientReg,
      short[] dividendReg,
      int count,
      short divisorSmall) {
      int i = count;
      short remainderShort = 0;
      int idivisor = ((int)divisorSmall) & 0xffff;
      int quo, rem;
      while ((i--) > 0) {
        int currentDividend = ((int)((((int)dividendReg[i]) & 0xffff) |
                    ((int)remainderShort << 16)));
        if ((currentDividend >> 31) == 0) {
          quo = currentDividend / idivisor;
          quotientReg[i] = ((short)quo);
          if (i > 0) {
            rem = currentDividend - (idivisor * quo);
            remainderShort = ((short)rem);
          }
        } else {
          quotientReg[i] = DivideUnsigned(currentDividend, divisorSmall);
          if (i > 0) {
            remainderShort = RemainderUnsigned(currentDividend, divisorSmall);
          }
        }
      }
    }

    private static short FastDivideAndRemainder(
      short[] quotientReg,
      int quotientStart,
      short[] dividendReg,
      int dividendStart,
      int count,
      short divisorSmall) {
      int i = count;
      short remainderShort = 0;
      int idivisor = ((int)divisorSmall) & 0xffff;
      int quo, rem;
      while ((i--) > 0) {
        int currentDividend =
          ((int)((((int)dividendReg[dividendStart + i]) & 0xffff) |
                    ((int)remainderShort << 16)));
        if ((currentDividend >> 31) == 0) {
          quo = currentDividend / idivisor;
          quotientReg[quotientStart + i] = ((short)quo);
          rem = currentDividend - (idivisor * quo);
          remainderShort = ((short)rem);
        } else {
          quotientReg[quotientStart + i] = DivideUnsigned(
            currentDividend,
            divisorSmall);
          remainderShort = RemainderUnsigned(currentDividend, divisorSmall);
        }
      }
      return remainderShort;
    }

    private static short FastRemainder(
      short[] dividendReg,
      int count,
      short divisorSmall) {
      int i = count;
      short remainder = 0;
      while ((i--) > 0) {
        remainder = RemainderUnsigned(
          MakeUint(dividendReg[i], remainder),
          divisorSmall);
      }
      return remainder;
    }

    private static short GetHighHalf(int val) {
      return ((short)((val >> 16) & 0xffff));
    }

    private static short GetHighHalfAsBorrow(int val) {
      return ((short)(0 - ((val >> 16) & 0xffff)));
    }

    private static short GetLowHalf(int val) {
      return ((short)(val & 0xffff));
    }

    private static int getUnsignedBitLengthEx(int numberValue, int wordCount) {
      int wc = wordCount;
      if (wc != 0) {
        wc = (wc - 1) << 4;
        if (numberValue == 0) {
          return wc;
        }
        wc += 16;
        {
          if ((numberValue >> 8) == 0) {
            numberValue <<= 8;
            wc -= 8;
          }
          if ((numberValue >> 12) == 0) {
            numberValue <<= 4;
            wc -= 4;
          }
          if ((numberValue >> 14) == 0) {
            numberValue <<= 2;
            wc -= 2;
          }
          if ((numberValue >> 15) == 0) {
            --wc;
          }
        }
        return wc;
      }
      return 0;
    }

    private static short[] GrowForCarry(short[] a, short carry) {
      int oldLength = a.length;
      short[] ret = CleanGrow(a, RoundupSize(oldLength + 1));
      ret[oldLength] = carry;
      return ret;
    }

    private static int Increment(
      short[] words1,
      int words1Start,
      int n,
      short words2) {
      {
        short tmp = words1[words1Start];
        words1[words1Start] = (short)(tmp + words2);
        if ((((int)words1[words1Start]) & 0xffff) >= (((int)tmp) & 0xffff)) {
          return 0;
        }
        for (int i = 1; i < n; ++i) {
          ++words1[words1Start + i];
          if (words1[words1Start + i] != 0) {
            return 0;
          }
        }
        return 1;
      }
    }

    private static short LinearMultiply(
      short[] productArr,
      int cstart,
      short[] words1,
      int astart,
      short words2,
      int n) {
      {
        short carry = 0;
        int bint = ((int)words2) & 0xffff;
        for (int i = 0; i < n; ++i) {
          int p;
          p = (((int)words1[astart + i]) & 0xffff) * bint;
          p += ((int)carry) & 0xffff;
          productArr[cstart + i] = (short)p;
          carry = (short)(p >> 16);
        }
        return carry;
      }
    }

    private static short LinearMultiplyAdd(
      short[] productArr,
      int cstart,
      short[] words1,
      int astart,
      short words2,
      int n) {
      {
        short carry = 0;
        int bint = ((int)words2) & 0xffff;
        for (int i = 0; i < n; ++i) {
          int p;
          p = (((int)words1[astart + i]) & 0xffff) * bint;
          p += ((int)carry) & 0xffff;
          p += ((int)productArr[cstart + i]) & 0xffff;
          productArr[cstart + i] = (short)p;
          carry = (short)(p >> 16);
        }
        return carry;
      }
    }

    private static int MakeUint(short first, short second) {
      return ((int)((((int)first) & 0xffff) | ((int)second << 16)));
    }

    private static void RecursiveSquare(
      short[] resultArr,
      int resultStart,
      short[] tempArr,
      int tempStart,
      short[] words1,
      int words1Start,
      int count) {
      if (count <= RecursionLimit) {
        switch (count) {
          case 2:
            BaselineSquare2(resultArr, resultStart, words1, words1Start);
            break;
          case 4:
            BaselineSquare4(resultArr, resultStart, words1, words1Start);
            break;
          case 8:
            BaselineSquare8(resultArr, resultStart, words1, words1Start);
            break;
          default:
            SchoolbookSquare(
resultArr,
resultStart,
words1,
words1Start,
count);
            break;
        }
      } else if ((count & 1) == 0) {
        int count2 = count >> 1;
        RecursiveSquare(
          resultArr,
          resultStart,
          tempArr,
          tempStart + count,
          words1,
          words1Start,
          count2);
        RecursiveSquare(
          resultArr,
          resultStart + count,
          tempArr,
          tempStart + count,
          words1,
          words1Start + count2,
          count2);
        SameSizeMultiply(
          tempArr,
          tempStart,
          tempArr,
          tempStart + count,
          words1,
          words1Start,
          words1,
          words1Start + count2,
          count2);
        int carry = AddOneByOne(
          resultArr,
          resultStart + count2,
          resultArr,
          resultStart + count2,
          tempArr,
          tempStart,
          count);
        carry += AddOneByOne(
          resultArr,
          resultStart + count2,
          resultArr,
          resultStart + count2,
          tempArr,
          tempStart,
          count);
        Increment(
          resultArr,
          (int)(resultStart + count + count2),
          count2,
          (short)carry);
      } else {
        SameSizeMultiply(
          resultArr,
          resultStart,
          tempArr,
          tempStart,
          words1,
          words1Start,
          words1,
          words1Start,
          count);
      }
    }

    private static short RemainderUnsigned(int x, short y) {
      {
        int iy = ((int)y) & 0xffff;
        return ((x >> 31) == 0) ? ((short)(((int)x % iy) & 0xffff)) :
          Divide32By16(x, y, true);
      }
    }

    private static void ReverseChars(char[] chars, int offset, int length) {
      int half = length >> 1;
      int right = offset + length - 1;
      for (int i = 0; i < half; i++, right--) {
        char value = chars[offset + i];
        chars[offset + i] = chars[right];
        chars[right] = value;
      }
    }

    private static int RoundupSize(int n) {
      return n + (n & 1);
    }

    // NOTE: Renamed from RecursiveMultiply to better show that
    // this function only takes operands of the same size, as opposed
    // to AsymmetricMultiply.
    private static void SameSizeMultiply(
      short[] resultArr,  // size 2*count
      int resultStart,
      short[] tempArr,  // size 2*count
      int tempStart,
      short[] words1,
      int words1Start,  // size count
      short[] words2,
      int words2Start,  // size count
      int count) {
      // System.out.println("RecursiveMultiply " + count + " " + count +
      // " [r=" + resultStart + " t=" + tempStart + " a=" + words1Start +
      // " b=" + words2Start + "]");

      if (count <= RecursionLimit) {
        switch (count) {
          case 2:
            BaselineMultiply2(
resultArr,
resultStart,
words1,
words1Start,
words2,
words2Start);
            break;
          case 4:
            BaselineMultiply4(
  resultArr,
  resultStart,
  words1,
  words1Start,
  words2,
  words2Start);
            break;
          case 8:
            BaselineMultiply8(
  resultArr,
  resultStart,
  words1,
  words1Start,
  words2,
  words2Start);
            break;
          default: SchoolbookMultiply(
resultArr,
resultStart,
words1,
words1Start,
count,
words2,
words2Start,
count);
            break;
        }
      } else {
        int countA = count;
        while (countA != 0 && words1[words1Start + countA - 1] == 0) {
          --countA;
        }
        int countB = count;
        while (countB != 0 && words2[words2Start + countB - 1] == 0) {
          --countB;
        }
        int offset2For1 = 0;
        int offset2For2 = 0;
        if (countA == 0 || countB == 0) {
          // words1 or words2 is empty, so result is 0
          java.util.Arrays.fill(resultArr, resultStart, (resultStart)+(count << 1), (short)0);
          return;
        }
        // Split words1 and words2 in two parts each
        if ((count & 1) == 0) {
          int count2 = count >> 1;
          if (countA <= count2 && countB <= count2) {
            // System.out.println("Can be smaller: " + AN + "," + BN + "," +
            // (count2));
            java.util.Arrays.fill(resultArr, resultStart + count, (resultStart + count)+(count), (short)0);
            if (count2 == 8) {
              BaselineMultiply8(
                resultArr,
                resultStart,
                words1,
                words1Start,
                words2,
                words2Start);
            } else {
              SameSizeMultiply(
                resultArr,
                resultStart,
                tempArr,
                tempStart,
                words1,
                words1Start,
                words2,
                words2Start,
                count2);
            }
            return;
          }
          int resultMediumHigh = resultStart + count;
          int resultHigh = resultMediumHigh + count2;
          int resultMediumLow = resultStart + count2;
          int tsn = tempStart + count;
          offset2For1 = Compare(
            words1,
            words1Start,
            words1,
            words1Start + count2,
            count2) > 0 ? 0 : count2;
          // Absolute value of low part minus high part of words1
          int tmpvar = (int)(words1Start + (count2 ^
                    offset2For1));
          SubtractOneByOne(
            resultArr,
            resultStart,
            words1,
            words1Start + offset2For1,
            words1,
            tmpvar,
            count2);
          offset2For2 = Compare(
            words2,
            words2Start,
            words2,
            words2Start + count2,
            count2) > 0 ? 0 : count2;
          // Absolute value of low part minus high part of words2
          int tmp = words2Start + (count2 ^ offset2For2);
          SubtractOneByOne(
            resultArr,
            resultMediumLow,
            words2,
            words2Start + offset2For2,
            words2,
            tmp,
            count2);
          //---------
          // HighA * HighB
          SameSizeMultiply(
            resultArr,
            resultMediumHigh,
            tempArr,
            tsn,
            words1,
            words1Start + count2,
            words2,
            words2Start + count2,
            count2);
          // Medium high result = Abs(LowA-HighA).Multiply(Abs)(LowB-HighB)
          SameSizeMultiply(
            tempArr,
            tempStart,
            tempArr,
            tsn,
            resultArr,
            resultStart,
            resultArr,
            resultMediumLow,
            count2);
          // Low result = LowA * LowB
          SameSizeMultiply(
            resultArr,
            resultStart,
            tempArr,
            tsn,
            words1,
            words1Start,
            words2,
            words2Start,
            count2);
          int c2 = AddOneByOne(
            resultArr,
            resultMediumHigh,
            resultArr,
            resultMediumHigh,
            resultArr,
            resultMediumLow,
            count2);
          int c3 = c2;
          c2 += AddOneByOne(
            resultArr,
            resultMediumLow,
            resultArr,
            resultMediumHigh,
            resultArr,
            resultStart,
            count2);
          c3 += AddOneByOne(
            resultArr,
            resultMediumHigh,
            resultArr,
            resultMediumHigh,
            resultArr,
            resultHigh,
            count2);
          if (offset2For1 == offset2For2) {
            c3 -= SubtractOneByOne(
              resultArr,
              resultMediumLow,
              resultArr,
              resultMediumLow,
              tempArr,
              tempStart,
              count);
          } else {
            c3 += AddOneByOne(
              resultArr,
              resultMediumLow,
              resultArr,
              resultMediumLow,
              tempArr,
              tempStart,
              count);
          }
          c3 += Increment(resultArr, resultMediumHigh, count2, (short)c2);
          // DebugWords(resultArr,resultStart,count*2,"p6");
          if (c3 != 0) {
            Increment(resultArr, resultHigh, count2, (short)c3);
          }
          // DebugWords(resultArr,resultStart,count*2,"p7");
        } else {
          // Count is odd, high part will be 1 shorter
          int countHigh = count >> 1;  // Shorter part
          int countLow = count - countHigh;  // Longer part
          offset2For1 = CompareWithOneBiggerWords1(
            words1,
            words1Start,
            words1,
            words1Start + countLow,
            countLow) > 0 ? 0 : countLow;
          if (offset2For1 == 0) {
            SubtractOneBiggerWords1(
              resultArr,
              resultStart,
              words1,
              words1Start,
              words1,
              words1Start + countLow,
              countLow);
          } else {
            SubtractOneBiggerWords2(
              resultArr,
              resultStart,
              words1,
              words1Start + countLow,
              words1,
              words1Start,
              countLow);
          }
          offset2For2 = CompareWithOneBiggerWords1(
            words2,
            words2Start,
            words2,
            words2Start + countLow,
            countLow) > 0 ? 0 : countLow;
          if (offset2For2 == 0) {
            SubtractOneBiggerWords1(
              tempArr,
              tempStart,
              words2,
              words2Start,
              words2,
              words2Start + countLow,
              countLow);
          } else {
            SubtractOneBiggerWords2(
              tempArr,
              tempStart,
              words2,
              words2Start + countLow,
              words2,
              words2Start,
              countLow);
          }
          // Abs(LowA-HighA).Multiply(Abs)(LowB-HighB)
          int shorterOffset = countHigh << 1;
          int longerOffset = countLow << 1;
          SameSizeMultiply(
            tempArr,
            tempStart + shorterOffset,
            resultArr,
            resultStart + shorterOffset,
            resultArr,
            resultStart,
            tempArr,
            tempStart,
            countLow);
          // DebugWords(resultArr, resultStart + shorterOffset, countLow <<
          // 1,"w1*w2");
          short resultTmp0 = tempArr[tempStart + shorterOffset];
          short resultTmp1 = tempArr[tempStart + shorterOffset + 1];
          // HighA * HighB
          SameSizeMultiply(
            resultArr,
            resultStart + longerOffset,
            resultArr,
            resultStart,
            words1,
            words1Start + countLow,
            words2,
            words2Start + countLow,
            countHigh);
          // LowA * LowB
          SameSizeMultiply(
            resultArr,
            resultStart,
            tempArr,
            tempStart,
            words1,
            words1Start,
            words2,
            words2Start,
            countLow);
          tempArr[tempStart + shorterOffset] = resultTmp0;
          tempArr[tempStart + shorterOffset + 1] = resultTmp1;
          int countMiddle = countLow << 1;
          // DebugWords(resultArr,resultStart,count*2,"q1");
          int c2 = AddOneByOne(
            resultArr,
            resultStart + countMiddle,
            resultArr,
            resultStart + countMiddle,
            resultArr,
            resultStart + countLow,
            countLow);
          int c3 = c2;
          // DebugWords(resultArr,resultStart,count*2,"q2");
          c2 += AddOneByOne(
            resultArr,
            resultStart + countLow,
            resultArr,
            resultStart + countMiddle,
            resultArr,
            resultStart,
            countLow);
          // DebugWords(resultArr,resultStart,count*2,"q3");
          c3 += AddUnevenSize(
            resultArr,
            resultStart + countMiddle,
            resultArr,
            resultStart + countMiddle,
            countLow,
            resultArr,
            resultStart + countMiddle + countLow,
            countLow - 2);
          // DebugWords(resultArr,resultStart,count*2,"q4");
          if (offset2For1 == offset2For2) {
            c3 -= SubtractOneByOne(
              resultArr,
              resultStart + countLow,
              resultArr,
              resultStart + countLow,
              tempArr,
              tempStart + shorterOffset,
              countLow << 1);
          } else {
            c3 += AddOneByOne(
              resultArr,
              resultStart + countLow,
              resultArr,
              resultStart + countLow,
              tempArr,
              tempStart + shorterOffset,
              countLow << 1);
          }
          // DebugWords(resultArr,resultStart,count*2,"q5");
          c3 += Increment(
            resultArr,
            resultStart + countMiddle,
            countLow,
            (short)c2);
          // DebugWords(resultArr,resultStart,count*2,"q6");
          if (c3 != 0) {
            Increment(
              resultArr,
              resultStart + countMiddle + countLow,
              countLow - 2,
              (short)c3);
          }
          // DebugWords(resultArr,resultStart,count*2,"q7");
        }
      }
    }

    private static void SchoolbookMultiply(
      short[] resultArr,
      int resultStart,
      short[] words1,
      int words1Start,
      int words1Count,
      short[] words2,
      int words2Start,
      int words2Count) {
      // Method assumes that resultArr was already zeroed,
      // if resultArr is the same as words1 or words2
      int cstart;
      if (words1Count < words2Count) {
        // words1 is shorter than words2, so put words2 on top
        for (int i = 0; i < words1Count; ++i) {
          cstart = resultStart + i;
          {
            short carry = 0;
            int valueBint = ((int)words1[words1Start + i]) & 0xffff;
            for (int j = 0; j < words2Count; ++j) {
              int p;
              p = (((int)words2[words2Start + j]) & 0xffff) * valueBint;
              p += ((int)carry) & 0xffff;
              if (i != 0) {
                p += ((int)resultArr[cstart + j]) & 0xffff;
              }
              resultArr[cstart + j] = (short)p;
              carry = (short)(p >> 16);
            }
            resultArr[cstart + words2Count] = carry;
          }
        }
      } else {
        // words2 is shorter than words1
        for (int i = 0; i < words2Count; ++i) {
          cstart = resultStart + i;
          {
            short carry = 0;
            int valueBint = ((int)words2[words2Start + i]) & 0xffff;
            for (int j = 0; j < words1Count; ++j) {
              int p;
              p = (((int)words1[words1Start + j]) & 0xffff) * valueBint;
              p += ((int)carry) & 0xffff;
              if (i != 0) {
                p += ((int)resultArr[cstart + j]) & 0xffff;
              }
              resultArr[cstart + j] = (short)p;
              carry = (short)(p >> 16);
            }
            resultArr[cstart + words1Count] = carry;
          }
        }
      }
    }

    private static void SchoolbookSquare(
      short[] resultArr,
      int resultStart,
      short[] words1,
      int words1Start,
      int words1Count) {
      // Method assumes that resultArr was already zeroed,
      // if resultArr is the same as words1
      int cstart;
      for (int i = 0; i < words1Count; ++i) {
        cstart = resultStart + i;
        {
          short carry = 0;
          int valueBint = ((int)words1[words1Start + i]) & 0xffff;
          for (int j = 0; j < words1Count; ++j) {
            int p;
            p = (((int)words1[words1Start + j]) & 0xffff) * valueBint;
            p += ((int)carry) & 0xffff;
            if (i != 0) {
              p += ((int)resultArr[cstart + j]) & 0xffff;
            }
            resultArr[cstart + j] = (short)p;
            carry = (short)(p >> 16);
          }
          resultArr[cstart + words1Count] = carry;
        }
      }
    }

    private static short ShiftWordsLeftByBits(
      short[] r,
      int rstart,
      int n,
      int shiftBits) {
      {
        short u, carry = 0;
        if (shiftBits != 0) {
          for (int i = 0; i < n; ++i) {
            u = r[rstart + i];
            r[rstart + i] = (short)((int)(u << (int)shiftBits) | (((int)carry) &
                    0xffff));
            carry = (short)((((int)u) & 0xffff) >> (int)(16 - shiftBits));
          }
        }
        return carry;
      }
    }

    private static void ShiftWordsLeftByWords(
      short[] r,
      int rstart,
      int n,
      int shiftWords) {
      shiftWords = Math.min(shiftWords, n);
      if (shiftWords != 0) {
        for (int i = n - 1; i >= shiftWords; --i) {
          r[rstart + i] = r[rstart + i - shiftWords];
        }
        java.util.Arrays.fill(r, rstart, (rstart)+(shiftWords), (short)0);
      }
    }

    private static short ShiftWordsRightByBits(
      short[] r,
      int rstart,
      int n,
      int shiftBits) {
      short u, carry = 0;
      {
        if (shiftBits != 0) {
          for (int i = n; i > 0; --i) {
            u = r[rstart + i - 1];
            r[rstart + i - 1] = (short)((((((int)u) & 0xffff) >>
                    (int)shiftBits) & 0xffff) | (((int)carry) &
                    0xffff));
            carry = (short)((((int)u) & 0xffff) << (int)(16 - shiftBits));
          }
        }
        return carry;
      }
    }

    private static short ShiftWordsRightByBitsSignExtend(
      short[] r,
      int rstart,
      int n,
      int shiftBits) {
      {
        short u, carry = (short)((int)0xffff << (int)(16 - shiftBits));
        if (shiftBits != 0) {
          for (int i = n; i > 0; --i) {
            u = r[rstart + i - 1];
            r[rstart + i - 1] = (short)(((((int)u) & 0xffff) >>
                    (int)shiftBits) | (((int)carry) & 0xffff));
            carry = (short)((((int)u) & 0xffff) << (int)(16 - shiftBits));
          }
        }
        return carry;
      }
    }

    private static void ShiftWordsRightByWordsSignExtend(
      short[] r,
      int rstart,
      int n,
      int shiftWords) {
      shiftWords = Math.min(shiftWords, n);
      if (shiftWords != 0) {
        for (int i = 0; i + shiftWords < n; ++i) {
          r[rstart + i] = r[rstart + i + shiftWords];
        }
        rstart += n - shiftWords;
        // Sign extend
        for (int i = 0; i < shiftWords; ++i) {
          r[rstart + i] = ((short)0xffff);
        }
      }
    }

    private static short[] ShortenArray(short[] reg, int wordCount) {
      if (reg.length > 32) {
        int newLength = RoundupSize(wordCount);
        if (newLength < reg.length && (reg.length - newLength) >= 16) {
          // Reallocate the array if the rounded length
          // is much smaller than the current length
          short[] newreg = new short[newLength];
          System.arraycopy(reg, 0, newreg, 0, Math.min(newLength, reg.length));
          reg = newreg;
        }
      }
      return reg;
    }

    private static int Subtract(
      short[] c,
      int cstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart,
      int n) {
      {
        int u;
        u = 0;
        for (int i = 0; i < n; i += 2) {
          u = (((int)words1[astart]) & 0xffff) - (((int)words2[bstart]) &
                    0xffff) - (int)((u >> 31) & 1);
          c[cstart++] = (short)u;
          ++astart;
          ++bstart;
          u = (((int)words1[astart]) & 0xffff) - (((int)words2[bstart]) &
                    0xffff) - (int)((u >> 31) & 1);
          c[cstart++] = (short)u;
          ++astart;
          ++bstart;
        }
        return (int)((u >> 31) & 1);
      }
    }

    private static int SubtractOneBiggerWords1(
      short[] c,
      int cstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart,
      int words1Count) {
      // Assumes that words2's count is 1 less
      {
        int u;
        u = 0;
        int cm1 = words1Count - 1;
        for (int i = 0; i < cm1; i += 1) {
          u = (((int)words1[astart]) & 0xffff) - (((int)words2[bstart]) &
                    0xffff) - (int)((u >> 31) & 1);
          c[cstart++] = (short)u;
          ++astart;
          ++bstart;
        }
        u = (((int)words1[astart]) & 0xffff) - (int)((u >> 31) & 1);
        c[cstart++] = (short)u;
        return (int)((u >> 31) & 1);
      }
    }

    private static int SubtractOneBiggerWords2(
      short[] c,
      int cstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart,
      int words2Count) {
      // Assumes that words1's count is 1 less
      {
        int u;
        u = 0;
        int cm1 = words2Count - 1;
        for (int i = 0; i < cm1; i += 1) {
          u = (((int)words1[astart]) & 0xffff) - (((int)words2[bstart]) &
                    0xffff) - (int)((u >> 31) & 1);
          c[cstart++] = (short)u;
          ++astart;
          ++bstart;
        }
        u = 0 - (((int)words2[bstart]) & 0xffff) - (int)((u >> 31) & 1);
        c[cstart++] = (short)u;
        return (int)((u >> 31) & 1);
      }
    }

    private static int SubtractOneByOne(
      short[] c,
      int cstart,
      short[] words1,
      int astart,
      short[] words2,
      int bstart,
      int n) {
      {
        int u;
        u = 0;
        for (int i = 0; i < n; i += 1) {
          u = (((int)words1[astart]) & 0xffff) - (((int)words2[bstart]) &
                    0xffff) - (int)((u >> 31) & 1);
          c[cstart++] = (short)u;
          ++astart;
          ++bstart;
        }
        return (int)((u >> 31) & 1);
      }
    }

    private static void TwosComplement(short[] words1, int words1Start, int n) {
      Decrement(words1, words1Start, n, (short)1);
      for (int i = 0; i < n; ++i) {
        words1[words1Start + i] = ((short)(~words1[words1Start + i]));
      }
    }

    private int ByteCount() {
      int wc = this.wordCount;
      if (wc == 0) {
        return 0;
      }
      short s = this.words[wc - 1];
      wc = (wc - 1) << 1;
      return (s == 0) ? wc : (((s >> 8) == 0) ? wc + 1 : wc + 2);
    }

    /**
     * Not documented yet.
     * @param n A 32-bit signed integer.
     * @return A Boolean object.
     */
    public boolean GetUnsignedBit(int n) {
      if (n < 0) {
        throw new IllegalArgumentException("n (" + n + ") is less than 0");
      }
      return ((n >> 4) < this.words.length) && ((boolean)(((this.words[(n >>
                    4)] >> (int)(n & 15)) & 1) != 0));
    }

    private boolean HasSmallValue() {
      int c = (int)this.wordCount;
      if (c > 4) {
        return false;
      }
      if (c == 4 && (this.words[3] & 0x8000) != 0) {
        return this.negative && this.words[3] == ((short)0x8000) &&
          this.words[2] == 0 && this.words[1] == 0 &&
          this.words[0] == 0;
      }
      return true;
    }

    private int PositiveCompare(EInteger t) {
      int size = this.wordCount, tempSize = t.wordCount;
      return (
        size == tempSize) ? Compare(
        this.words,
        0,
        t.words,
        0,
        (int)size) : (size > tempSize ? 1 : -1);
    }

    private String SmallValueToString() {
      long value = this.AsInt64Unchecked();
      if (value == Long.MIN_VALUE) {
        return "-9223372036854775808";
      }
      if (value == (long)Integer.MIN_VALUE) {
        return "-2147483648";
      }
      boolean neg = value < 0;
      int count = 0;
      char[] chars;
      int intvalue = ((int)value);
      if ((long)intvalue == value) {
        chars = new char[12];
        if (neg) {
          chars[0] = '-';
          ++count;
          intvalue = -intvalue;
        }
        while (intvalue != 0) {
          int intdivvalue = intvalue / 10;
          char digit = Digits.charAt((int)(intvalue - (intdivvalue * 10)));
          chars[count++] = digit;
          intvalue = intdivvalue;
        }
      } else {
        chars = new char[24];
        if (neg) {
          chars[0] = '-';
          ++count;
          value = -value;
        }
        while (value != 0) {
          long divvalue = value / 10;
          char digit = Digits.charAt((int)(value - (divvalue * 10)));
          chars[count++] = digit;
          value = divvalue;
        }
      }
      if (neg) {
        ReverseChars(chars, 1, count - 1);
      } else {
        ReverseChars(chars, 0, count);
      }
      return new String(chars, 0, count);
    }

    private EInteger[] SqrtRemInternal(boolean useRem) {
      if (this.signum() <= 0) {
        return new EInteger[] { EInteger.FromInt64(0), EInteger.FromInt64(0) };
      }
      if (this.equals(EInteger.FromInt64(1))) {
        return new EInteger[] { EInteger.FromInt64(1), EInteger.FromInt64(0) };
      }
      EInteger bigintX;
      EInteger bigintY;
      EInteger thisValue = this;
      int powerBits = (thisValue.GetUnsignedBitLength() + 1) / 2;
      if (thisValue.CanFitInInt32()) {
        int smallValue = thisValue.AsInt32Checked();
        // No need to check for ValueZero; already done above
        int smallintX = 0;
        int smallintY = 1 << powerBits;
        do {
          smallintX = smallintY;
          smallintY = smallValue / smallintX;
          smallintY += smallintX;
          smallintY >>= 1;
        } while (smallintY < smallintX);
        if (!useRem) {
          return new EInteger[] { EInteger.FromInt64(smallintX), null };
        }
        smallintY = smallintX * smallintX;
        smallintY = smallValue - smallintY;
        return new EInteger[] { EInteger.FromInt64(smallintX), EInteger.FromInt64(smallintY) };
      }
      bigintX = EInteger.FromInt64(0);
      bigintY = EInteger.FromInt64(1).ShiftLeft(powerBits);
      do {
        bigintX = bigintY;
        bigintY = thisValue.Divide(bigintX);
        bigintY = bigintY.Add(bigintX);
        bigintY = bigintY.ShiftRight(1);
      } while (bigintY != null && bigintY.compareTo(bigintX) < 0);
      if (!useRem) {
        return new EInteger[] { bigintX, null };
      }
      bigintY = bigintX.Multiply(bigintX);
      bigintY = thisValue.Subtract(bigintY);
      return new EInteger[] { bigintX, bigintY };
    }
  }
