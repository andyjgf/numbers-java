package com.upokecenter.numbers;
/*
Written by Peter O. in 2013.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/
If you like this, you should donate to Peter O.
at: http://peteroupc.github.io/
 */

  /**
   * Represents an arbitrary-precision decimal floating-point number. (The "E"
   *  stands for "extended", meaning that instances of this class can be
   * values other than numbers proper, such as infinity and not-a-number.)
   * <p><b>About decimal arithmetic</b> </p> <p>Decimal (base-10)
   * arithmetic, such as that provided by this class, is appropriate for
   * calculations involving such real-world data as prices and other sums
   * of money, tax rates, and measurements. These calculations often
   * involve multiplying or dividing one decimal with another decimal, or
   * performing other operations on decimal numbers. Many of these
   * calculations also rely on rounding behavior in which the result after
   * rounding is an arbitrary-precision decimal number (for example,
   * multiplying a price by a premium rate, then rounding, should result in
   * a decimal amount of money).</p> <p>On the other hand, most
   * implementations of <code>float</code> and <code>double</code> , including in C#
   * and Java, store numbers in a binary (base-2) floating-point format and
   * use binary floating-point arithmetic. Many decimal numbers can't be
   * represented exactly in binary floating-point format (regardless of its
   * length). Applying binary arithmetic to numbers intended to be decimals
   * can sometimes lead to unintuitive results, as is shown in the
   * description for the FromDouble() method of this class.</p> <p><b>About
   * EDecimal instances</b> </p> <p>Each instance of this class consists of
   * an integer significand and an integer exponent, both
   * arbitrary-precision. The value of the number equals significand *
   * 10^exponent.</p> <p>The significand is the value of the digits that
   * make up a number, ignoring the decimal point and exponent. For
   * example, in the number 2356.78, the significand is 235678. The
   *  exponent is where the "floating" decimal point of the number is
   *  located. A positive exponent means "move it to the right", and a
   *  negative exponent means "move it to the left." In the example 2,
   * 356.78, the exponent is -2, since it has 2 decimal places and the
   *  decimal point is "moved to the left by 2." Therefore, in the
   * arbitrary-precision decimal representation, this number would be
   * stored as 235678 * 10^-2.</p> <p>The significand and exponent format
   * preserves trailing zeros in the number's value. This may give rise to
   * multiple ways to store the same value. For example, 1.00 and 1 would
   * be stored differently, even though they have the same value. In the
   * first case, 100 * 10^-2 (100 with decimal point moved left by 2), and
   * in the second case, 1 * 10^0 (1 with decimal point moved 0).</p>
   * <p>This class also supports values for negative zero, not-a-number
   * (NaN) values, and infinity. <b>Negative zero</b> is generally used
   * when a negative number is rounded to 0; it has the same mathematical
   * value as positive zero. <b>Infinity</b> is generally used when a
   * non-zero number is divided by zero, or when a very high or very low
   * number can't be represented in a given exponent range.
   * <b>Not-a-number</b> is generally used to signal errors.</p> <p>This
   * class implements the General Decimal Arithmetic Specification version
   * 1.70 except part of chapter 6(
   * <code>http://speleotrove.com/decimal/decarith.html</code>).</p>
   * <p><b>Errors and Exceptions</b> </p> <p>Passing a signaling NaN to any
   * arithmetic operation shown here will signal the flag FlagInvalid and
   * return a quiet NaN, even if another operand to that operation is a
   * quiet NaN, unless noted otherwise.</p> <p>Passing a quiet NaN to any
   * arithmetic operation shown here will return a quiet NaN, unless noted
   * otherwise. Invalid operations will also return a quiet NaN, as stated
   * in the individual methods.</p> <p>Unless noted otherwise, passing a
   * null arbitrary-precision decimal argument to any method here will
   * throw an exception.</p> <p>When an arithmetic operation signals the
   * flag FlagInvalid, FlagOverflow, or FlagDivideByZero, it will not throw
   * an exception too, unless the flag's trap is enabled in the arithmetic
   * context (see EContext's Traps property).</p> <p>If an operation
   * requires creating an intermediate value that might be too big to fit
   * in memory (or might require more than 2 gigabytes of memory to store
   * -- due to the current use of a 32-bit integer internally as a length),
   * the operation may signal an invalid-operation flag and return
   * not-a-number (NaN). In certain rare cases, the compareTo method may
   * throw OutOfMemoryError (called OutOfMemoryError in Java) in the same
   * circumstances.</p> <p><b>Serialization</b> </p> <p>An
   * arbitrary-precision decimal value can be serialized (converted to a
   * stable format) in one of the following ways:</p> <ul><li>By calling
   * the toString() method, which will always return distinct strings for
   * distinct arbitrary-precision decimal values.</li> <li>By calling the
   * UnsignedMantissa, Exponent, and IsNegative properties, and calling the
   * IsInfinity, IsQuietNaN, and IsSignalingNaN methods. The return values
   * combined will uniquely identify a particular arbitrary-precision
   * decimal value.</li> </ul> <p><b>Thread safety</b> </p> <p>Instances of
   * this class are immutable, so they are inherently safe for use by
   * multiple threads. Multiple instances of this object with the same
   * properties are interchangeable, so they should not be compared using
   *  the "==" operator (which might only check if each side of the operator
   * is the same instance).</p> <p><b>Comparison considerations</b> </p>
   * <p>This class's natural ordering (under the compareTo method) is not
   * consistent with the Equals method. This means that two values that
   * compare as equal under the compareTo method might not be equal under
   * the Equals method. The compareTo method compares the mathematical
   * values of the two instances passed to it (and considers two different
   * NaN values as equal), while two instances with the same mathematical
   * value, but different exponents, will be considered unequal under the
   * Equals method.</p> <p><b>Security note</b> </p> <p>It is not
   * recommended to implement security-sensitive algorithms using the
   * methods in this class, for several reasons:</p>
   * <ul><li><code>EDecimal</code> objects are immutable, so they can't be
   * modified, and the memory they occupy is not guaranteed to be cleared
   * in a timely fashion due to garbage collection. This is relevant for
   * applications that use many-digit-long numbers as secret
   * parameters.</li> <li>The methods in this class (especially those that
   *  involve arithmetic) are not guaranteed to be "constant-time"
   * (non-data-dependent) for all relevant inputs. Certain attacks that
   * involve encrypted communications have exploited the timing and other
   * aspects of such communications to derive keying material or cleartext
   * indirectly.</li> </ul> <p>Applications should instead use dedicated
   * security libraries to handle big numbers in security-sensitive
   * algorithms.</p> <p><b>Forms of numbers</b> </p> <p>There are several
   * other types of numbers that are mentioned in this class and elsewhere
   * in this documentation. For reference, they are specified here.</p>
   * <p><b>Unsigned integer</b> : An integer that's always 0 or greater,
   * with the following maximum values:</p> <ul><li>8-bit unsigned integer,
   * or <i>byte</i> : 255.</li> <li>16-bit unsigned integer: 65535.</li>
   * <li>32-bit unsigned integer: (2 <sup>32</sup> -1).</li> <li>64-bit
   * unsigned integer: (2 <sup>64</sup> -1).</li> </ul> <p><b>Signed
   * integer</b> : An integer in <i>two's-complement form</i> , with the
   * following ranges:</p> <ul><li>8-bit signed integer: -128 to 127.</li>
   * <li>16-bit signed integer: -32768 to 32767.</li> <li>32-bit signed
   * integer: -2 <sup>31</sup> to (2 <sup>31</sup> - 1).</li> <li>64-bit
   * signed integer: -2 <sup>63</sup> to (2 <sup>63</sup> - 1).</li> </ul>
   * <p><b>Two's complement form</b> : In <i>two's-complement form</i> ,
   * nonnegative numbers have the highest (most significant) bit set to
   * zero, and negative numbers have that bit (and all bits beyond) set to
   * one, and a negative number is stored in such form by decreasing its
   * absolute value by 1 and swapping the bits of the resulting number.</p>
   * <p><b>64-bit floating-point number</b> : A 64-bit binary
   * floating-point number, in the form <i>significand</i> * 2
   * <sup><i>exponent</i> </sup> . The significand is 53 bits long
   * (Precision) and the exponent ranges from -1074 (EMin) to 971 (EMax).
   * The number is stored in the following format (commonly called the IEEE
   * 754 format):</p> <pre>|C|BBB...BBB|AAAAAA...AAAAAA|</pre> <ul><li>A.
   * Low 52 bits (Precision minus 1 bits): Lowest bits of the
   * significand.</li> <li>B. Next 11 bits: Exponent area: <ul><li>If all
   * bits are ones, this value is infinity (positive or negative depending
   * on the C bit) if all bits in area A are zeros, or not-a-number (NaN)
   * otherwise.</li> <li>If all bits are zeros, this is a subnormal number.
   * The exponent is EMin and the highest bit of the significand is
   * zero.</li> <li>If any other number, the exponent is this value reduced
   * by 1, then raised by EMin, and the highest bit of the significand is
   * one.</li> </ul> </li> <li>C. Highest bit: If one, this is a negative
   * number.</li> </ul> <p>The elements described above are in the same
   * order as the order of each bit of each element, that is, either most
   * significant first or least significant first.</p> <p><b>32-bit binary
   * floating-point number</b> : A 32-bit binary number which is stored
   * similarly to a <i>64-bit floating-point number</i> , except that:</p>
   * <ul><li>Precision is 24 bits.</li> <li>EMin is -149.</li> <li>EMax is
   * 104.</li> <li>A. The low 23 bits (Precision minus 1 bits) are the
   * lowest bits of the significand.</li> <li>B. The next 8 bits are the
   * exponent area.</li> <li>C. If the highest bit is one, this is a
   * negative number.</li> </ul> <p><b>.NET Framework decimal</b> : A
   * 128-bit decimal floating-point number, in the form <i>significand</i>
   * * 10 <sup>- <i>scale</i> </sup> , where the scale ranges from 0 to 28.
   * The number is stored in the following format:</p> <ul><li>Low 96 bits
   * are the significand, as a 96-bit unsigned integer (all 96-bit values
   * are allowed, up to (2 <sup>96</sup> -1)).</li> <li>Next 16 bits are
   * unused.</li> <li>Next 8 bits are the scale, stored as an 8-bit
   * unsigned integer.</li> <li>Next 7 bits are unused.</li> <li>If the
   * highest bit is one, it's a negative number.</li> </ul> <p>The elements
   * described above are in the same order as the order of each bit of each
   * element, that is, either most significant first or least significant
   * first.</p>
   */

  public final class EDecimal implements Comparable<EDecimal> {
    //----------------------------------------------------------------

    /**
     * A not-a-number value.
     */

    public static final EDecimal NaN = CreateWithFlags(
        EInteger.FromInt32(0),
        EInteger.FromInt32(0),
        BigNumberFlags.FlagQuietNaN);

    /**
     * Negative infinity, less than any other number.
     */

    public static final EDecimal NegativeInfinity =
      CreateWithFlags(
        EInteger.FromInt32(0),
        EInteger.FromInt32(0),
        BigNumberFlags.FlagInfinity | BigNumberFlags.FlagNegative);

    /**
     * Represents the number negative zero.
     */

    public static final EDecimal NegativeZero =
      CreateWithFlags(
        EInteger.FromInt32(0),
        EInteger.FromInt32(0),
        BigNumberFlags.FlagNegative);

    /**
     * Represents the number 1.
     */

    public static final EDecimal One =
      EDecimal.Create(EInteger.FromInt32(1), EInteger.FromInt32(0));

    /**
     * Positive infinity, greater than any other number.
     */

    public static final EDecimal PositiveInfinity =
      CreateWithFlags(
        EInteger.FromInt32(0),
        EInteger.FromInt32(0),
        BigNumberFlags.FlagInfinity);

    /**
     * A not-a-number value that signals an invalid operation flag when it's passed
     * as an argument to any arithmetic operation in arbitrary-precision
     * decimal.
     */

    public static final EDecimal SignalingNaN =
      CreateWithFlags(
        EInteger.FromInt32(0),
        EInteger.FromInt32(0),
        BigNumberFlags.FlagSignalingNaN);

    /**
     * Represents the number 10.
     */

    public static final EDecimal Ten =
      EDecimal.Create(EInteger.FromInt32(10), EInteger.FromInt32(0));

    /**
     * Represents the number 0.
     */

    public static final EDecimal Zero =
      EDecimal.Create(EInteger.FromInt32(0), EInteger.FromInt32(0));

    private static final int MaxSafeInt = 214748363;

    private static final IRadixMath<EDecimal> ExtendedMathValue = new
RadixMath<EDecimal>(new DecimalMathHelper());
    private static final FastIntegerFixed FastIntZero = new
FastIntegerFixed(0);
    //----------------------------------------------------------------
    private static final IRadixMath<EDecimal> MathValue = new
TrappableRadixMath<EDecimal>(
      new ExtendedOrSimpleRadixMath<EDecimal>(new
        DecimalMathHelper()));

    private static final int[] ValueTenPowers = {
      1, 10, 100, 1000, 10000, 100000,
      1000000, 10000000, 100000000,
      1000000000,
    };

    private final FastIntegerFixed exponent;
    private final int flags;
    private final FastIntegerFixed unsignedMantissa;

    private EDecimal(
      FastIntegerFixed unsignedMantissa,
      FastIntegerFixed exponent,
      int flags) {
      this.unsignedMantissa = unsignedMantissa;
      this.exponent = exponent;
      this.flags = flags;
    }

    /**
     * Creates a copy of this arbitrary-precision binary number.
     * @return An arbitrary-precision decimal floating-point number.
     */
    public EDecimal Copy() {
      return new EDecimal(
          this.unsignedMantissa.Copy(),
          this.exponent.Copy(),
          this.flags);
    }

    /**
     * Gets this object's exponent. This object's value will be an integer if the
     * exponent is positive or zero.
     * @return This object's exponent. This object's value will be an integer if
     * the exponent is positive or zero.
     */
    public final EInteger getExponent() {
        return this.exponent.ToEInteger();
      }

    /**
     * Gets a value indicating whether this object is finite (not infinity or NaN).
     * @return {@code true} if this object is finite (not infinity or NaN);
     * otherwise, {@code false}.
     */
    public final boolean isFinite() {
        return (this.flags & (BigNumberFlags.FlagInfinity |
              BigNumberFlags.FlagNaN)) == 0;
      }

    /**
     * Gets a value indicating whether this object is negative, including negative
     * zero.
     * @return {@code true} if this object is negative, including negative zero;
     * otherwise, {@code false}.
     */
    public final boolean isNegative() {
        return (this.flags & BigNumberFlags.FlagNegative) != 0;
      }

    /**
     * Gets a value indicating whether this object's value equals 0.
     * @return {@code true} if this object's value equals 0; otherwise, {@code
     * false}. {@code true} if this object's value equals 0; otherwise,
     * {@code false}.
     */
    public final boolean isZero() {
        return ((this.flags & BigNumberFlags.FlagSpecial) == 0) &&
          this.unsignedMantissa.isValueZero();
      }

    /**
     * Gets this object's unscaled value, or significand, and makes it negative if
     * this object is negative. If this value is not-a-number (NaN), that
     *  value's absolute value is the NaN's "payload" (diagnostic
     * information).
     * @return This object's unscaled value. Will be negative if this object's
     * value is negative (including a negative NaN).
     */
    public final EInteger getMantissa() {
        return this.isNegative() ? this.unsignedMantissa.ToEInteger().Negate() :
          this.unsignedMantissa.ToEInteger();
      }

    /**
     * Gets this value's sign: -1 if negative; 1 if positive; 0 if zero.
     * @return This value's sign: -1 if negative; 1 if positive; 0 if zero.
     */
    public final int signum() {
        return (((this.flags & BigNumberFlags.FlagSpecial) == 0) &&
            this.unsignedMantissa.isValueZero()) ? 0 : (((this.flags &
                BigNumberFlags.FlagNegative) != 0) ? -1 : 1);
      }

    /**
     * Gets the absolute value of this object's unscaled value, or significand. If
     *  this value is not-a-number (NaN), that value is the NaN's "payload"
     * (diagnostic information).
     * @return The absolute value of this object's unscaled value.
     */
    public final EInteger getUnsignedMantissa() {
        return this.unsignedMantissa.ToEInteger();
      }

    /**
     * Creates a number with the value <code>exponent*10^significand</code>.
     * @param mantissaSmall The parameter {@code mantissaSmall} is a 32-bit signed
     * integer.
     * @param exponentSmall Desired value for the exponent.
     * @return An arbitrary-precision decimal number.
     */
    public static EDecimal Create(int mantissaSmall, int exponentSmall) {
      if (mantissaSmall == Integer.MIN_VALUE) {
        return Create(EInteger.FromInt32(mantissaSmall), EInteger.FromInt32(exponentSmall));
      } else if (mantissaSmall < 0) {
        return new EDecimal(
            new FastIntegerFixed(mantissaSmall).Negate(),
            new FastIntegerFixed(exponentSmall),
            BigNumberFlags.FlagNegative);
      } else if (mantissaSmall == 0) {
        return new EDecimal(
            FastIntZero,
            new FastIntegerFixed(exponentSmall),
            0);
      } else {
        return new EDecimal(
            new FastIntegerFixed(mantissaSmall),
            new FastIntegerFixed(exponentSmall),
            0);
      }
    }

    /**
     * Creates a number with the value <code>exponent*10^significand</code>.
     * @param mantissa The parameter {@code mantissa} is a Numbers.EInteger object.
     * @param exponent Desired value for the exponent.
     * @return An arbitrary-precision decimal number.
     * @throws NullPointerException The parameter {@code mantissa} or {@code
     * exponent} is null.
     */
    public static EDecimal Create(
      EInteger mantissa,
      EInteger exponent) {
      if (mantissa == null) {
        throw new NullPointerException("mantissa");
      }
      if (exponent == null) {
        throw new NullPointerException("exponent");
      }
      FastIntegerFixed fi = FastIntegerFixed.FromBig(mantissa);
      int sign = fi.signum();
      return new EDecimal(
          sign < 0 ? fi.Negate() : fi,
          FastIntegerFixed.FromBig(exponent),
          (sign < 0) ? BigNumberFlags.FlagNegative : 0);
    }

    /**
     * Creates a not-a-number arbitrary-precision decimal number.
     * @param diag An integer, 0 or greater, to use as diagnostic information
     * associated with this object. If none is needed, should be zero. To
     * get the diagnostic information from another arbitrary-precision
     * decimal floating-point number, use that object's {@code
     * UnsignedMantissa} property.
     * @return A quiet not-a-number.
     */
    public static EDecimal CreateNaN(EInteger diag) {
      return CreateNaN(diag, false, false, null);
    }

    /**
     * Creates a not-a-number arbitrary-precision decimal number.
     * @param diag An integer, 0 or greater, to use as diagnostic information
     * associated with this object. If none is needed, should be zero. To
     * get the diagnostic information from another arbitrary-precision
     * decimal floating-point number, use that object's {@code
     * UnsignedMantissa} property.
     * @param signaling Whether the return value will be signaling (true) or quiet
     * (false).
     * @param negative Whether the return value is negative.
     * @param ctx An arithmetic context to control the precision (in decimal
     * digits) of the diagnostic information. The rounding and exponent
     * range of this context will be ignored. Can be null. The only flag
     * that can be signaled in this context is FlagInvalid, which happens
     * if diagnostic information needs to be truncated and too much memory
     * is required to do so.
     * @return An arbitrary-precision decimal number.
     * @throws NullPointerException The parameter {@code diag} is null or is less
     * than 0.
     */
    public static EDecimal CreateNaN(
      EInteger diag,
      boolean signaling,
      boolean negative,
      EContext ctx) {
      if (diag == null) {
        throw new NullPointerException("diag");
      }
      if (diag.signum() < 0) {
        throw new IllegalArgumentException("Diagnostic information must be 0 or" +
"\u0020greater," +
          "\u0020 was: " + diag);
      }
      if (diag.isZero() && !negative) {
        return signaling ? SignalingNaN : NaN;
      }
      int flags = 0;
      if (negative) {
        flags |= BigNumberFlags.FlagNegative;
      }
      if (ctx != null && ctx.getHasMaxPrecision()) {
        flags |= BigNumberFlags.FlagQuietNaN;
        EDecimal ef = new EDecimal(
          FastIntegerFixed.FromBig(diag),
          FastIntZero,
          flags).RoundToPrecision(ctx);
        int newFlags = ef.flags;
        newFlags &= ~BigNumberFlags.FlagQuietNaN;
        newFlags |= signaling ? BigNumberFlags.FlagSignalingNaN :
          BigNumberFlags.FlagQuietNaN;
        return new EDecimal(
            ef.unsignedMantissa,
            ef.exponent,
            newFlags);
      }
      flags |= signaling ? BigNumberFlags.FlagSignalingNaN :
        BigNumberFlags.FlagQuietNaN;
      return new EDecimal(
          FastIntegerFixed.FromBig(diag),
          FastIntZero,
          flags);
    }

    /**
     * Creates an arbitrary-precision decimal number from a 64-bit binary
     * floating-point number. This method computes the exact value of the
     * floating point number, not an approximation, as is often the case by
     * converting the floating point number to a string first. Remember,
     * though, that the exact value of a 64-bit binary floating-point
     * number is not always the value that results when passing a literal
     * decimal number (for example, calling
     * <code>ExtendedDecimal.FromDouble(0.1)</code>), since not all decimal
     * numbers can be converted to exact binary numbers (in the example
     * given, the resulting arbitrary-precision decimal will be the value
     *  of the closest "double" to 0.1, not 0.1 exactly). To create an
     * arbitrary-precision decimal number from a decimal value, use
     * FromString instead in most cases (for example:
     *  <code>ExtendedDecimal.FromString("0.1")</code>).
     * @param dbl The parameter {@code dbl} is a 64-bit floating-point number.
     * @return An arbitrary-precision decimal number with the same value as {@code
     * dbl}.
     */
    public static EDecimal FromDouble(double dbl) {
      int[] value = Extras.DoubleToIntegers(dbl);
      int floatExponent = (int)((value[1] >> 20) & 0x7ff);
      boolean neg = (value[1] >> 31) != 0;
      long lvalue;
      if (floatExponent == 2047) {
        if ((value[1] & 0xfffff) == 0 && value[0] == 0) {
          return neg ? NegativeInfinity : PositiveInfinity;
        }
        // Treat high bit of mantissa as quiet/signaling bit
        boolean quiet = (value[1] & 0x80000) != 0;
        value[1] &= 0x7ffff;
        lvalue = ((value[0] & 0xffffffffL) | ((long)value[1] << 32));
        int flags = (neg ? BigNumberFlags.FlagNegative : 0) | (quiet ?
            BigNumberFlags.FlagQuietNaN : BigNumberFlags.FlagSignalingNaN);
        return lvalue == 0 ? (quiet ? NaN : SignalingNaN) :
          new EDecimal(
            FastIntegerFixed.FromLong(lvalue),
            FastIntZero,
            flags);
      }
      value[1] &= 0xfffff;

      // Mask out the exponent and sign
      if (floatExponent == 0) {
        ++floatExponent;
      } else {
        value[1] |= 0x100000;
      }
      if ((value[1] | value[0]) != 0) {
        floatExponent += NumberUtility.ShiftAwayTrailingZerosTwoElements(
            value);
      } else {
        return neg ? EDecimal.NegativeZero : EDecimal.Zero;
      }
      floatExponent -= 1075;
      lvalue = ((value[0] & 0xffffffffL) | ((long)value[1] << 32));
      if (floatExponent == 0) {
        if (neg) {
          lvalue = -lvalue;
        }
        return EDecimal.FromInt64(lvalue);
      }
      if (floatExponent > 0) {
        // Value is an integer
        EInteger bigmantissa = EInteger.FromInt64(lvalue);
        bigmantissa = bigmantissa.ShiftLeft(floatExponent);
        if (neg) {
          bigmantissa=(bigmantissa).Negate();
        }
        return EDecimal.FromEInteger(bigmantissa);
      } else {
        // Value has a fractional part
        EInteger bigmantissa = EInteger.FromInt64(lvalue);
        EInteger bigexp = NumberUtility.FindPowerOfFive(-floatExponent);
        bigmantissa = bigmantissa.Multiply(bigexp);
        if (neg) {
          bigmantissa=(bigmantissa).Negate();
        }
        return EDecimal.Create(bigmantissa, EInteger.FromInt32(floatExponent));
      }
    }

    /**
     * Converts an arbitrary-precision integer to an arbitrary precision decimal.
     * @param bigint An arbitrary-precision integer.
     * @return An arbitrary-precision decimal number with the exponent set to 0.
     */
    public static EDecimal FromEInteger(EInteger bigint) {
      return EDecimal.Create(bigint, EInteger.FromInt32(0));
    }

    /**
     * Converts an arbitrary-precision binary floating-point number to an arbitrary
     * precision decimal.
     * @param ef An arbitrary-precision binary floating-point number.
     * @return An arbitrary-precision decimal number.
     * @deprecated Renamed to FromEFloat.
 */
@Deprecated
    public static EDecimal FromExtendedFloat(EFloat ef) {
      return FromEFloat(ef);
    }

    /**
     * Creates an arbitrary-precision decimal number from an arbitrary-precision
     * binary floating-point number.
     * @param bigfloat An arbitrary-precision binary floating-point number.
     * @return An arbitrary-precision decimal number.
     * @throws NullPointerException The parameter {@code bigfloat} is null.
     */
    public static EDecimal FromEFloat(EFloat bigfloat) {
      if (bigfloat == null) {
        throw new NullPointerException("bigfloat");
      }
      if (bigfloat.IsNaN() || bigfloat.IsInfinity()) {
        int flags = (bigfloat.isNegative() ? BigNumberFlags.FlagNegative : 0) |
          (bigfloat.IsInfinity() ? BigNumberFlags.FlagInfinity : 0) |
          (bigfloat.IsQuietNaN() ? BigNumberFlags.FlagQuietNaN : 0) |
          (bigfloat.IsSignalingNaN() ? BigNumberFlags.FlagSignalingNaN : 0);
        return CreateWithFlags(
            bigfloat.getUnsignedMantissa(),
            bigfloat.getExponent(),
            flags);
      }
      EInteger bigintExp = bigfloat.getExponent();
      EInteger bigintMant = bigfloat.getMantissa();
      if (bigintMant.isZero()) {
        return bigfloat.isNegative() ? EDecimal.NegativeZero :
          EDecimal.Zero;
      }
      if (bigintExp.isZero()) {
        // Integer
        return EDecimal.FromEInteger(bigintMant);
      }
      if (bigintExp.signum() > 0) {
        // Scaled integer
        FastInteger intcurexp = FastInteger.FromBig(bigintExp);
        EInteger bigmantissa = bigintMant;
        boolean neg = bigmantissa.signum() < 0;
        if (neg) {
          bigmantissa=(bigmantissa).Negate();
        }
        while (intcurexp.signum() > 0) {
          int shift = 1000000;
          if (intcurexp.CompareToInt(1000000) < 0) {
            shift = intcurexp.AsInt32();
          }
          bigmantissa = bigmantissa.ShiftLeft(shift);
          intcurexp.AddInt(-shift);
        }
        if (neg) {
          bigmantissa=(bigmantissa).Negate();
        }
        return EDecimal.FromEInteger(bigmantissa);
      } else {
        // Fractional number
        EInteger bigmantissa = bigintMant;
        EInteger negbigintExp=(bigintExp).Negate();
        negbigintExp = NumberUtility.FindPowerOfFiveFromBig(negbigintExp);
        bigmantissa = bigmantissa.Multiply(negbigintExp);
        return EDecimal.Create(bigmantissa, bigintExp);
      }
    }

    /**
     * Converts a boolean value (true or false) to an arbitrary-precision decimal
     * number.
     * @param boolValue Either true or false.
     * @return The number 1 if {@code boolValue} is true; otherwise, 0.
     */
    public static EDecimal FromBoolean(boolean boolValue) {
      return boolValue ? EDecimal.One : EDecimal.Zero;
    }

    /**
     * Creates an arbitrary-precision decimal number from a 32-bit signed integer.
     * @param valueSmaller The parameter {@code valueSmaller} is a 32-bit signed
     * integer.
     * @return An arbitrary-precision decimal number with the exponent set to 0.
     */
    public static EDecimal FromInt32(int valueSmaller) {
      if (valueSmaller == 0) {
        return EDecimal.Zero;
      }
      if (valueSmaller == Integer.MIN_VALUE) {
        return Create(EInteger.FromInt32(valueSmaller), EInteger.FromInt32(0));
      }
      if (valueSmaller < 0) {
        return new EDecimal(
            new FastIntegerFixed(valueSmaller).Negate(),
            FastIntZero,
            BigNumberFlags.FlagNegative);
      } else {
        return new EDecimal(
            new FastIntegerFixed(valueSmaller),
            FastIntZero,
            0);
      }
    }

    /**
     * Creates an arbitrary-precision decimal number from a 64-bit signed integer.
     * @param valueSmall The parameter {@code valueSmall} is a 64-bit signed
     * integer.
     * @return An arbitrary-precision decimal number with the exponent set to 0.
     */
    public static EDecimal FromInt64(long valueSmall) {
      if (valueSmall == 0) {
        return EDecimal.Zero;
      }
      if (valueSmall > Integer.MIN_VALUE && valueSmall <= Integer.MAX_VALUE) {
        if (valueSmall < 0) {
          return new EDecimal(
              new FastIntegerFixed((int)valueSmall).Negate(),
              FastIntZero,
              BigNumberFlags.FlagNegative);
        } else {
          return new EDecimal(
              new FastIntegerFixed((int)valueSmall),
              FastIntZero,
              0);
        }
      }
      EInteger bigint = EInteger.FromInt64(valueSmall);
      return EDecimal.Create(bigint, EInteger.FromInt32(0));
    }

    /**
     * Creates an arbitrary-precision decimal number from a 32-bit binary
     * floating-point number. This method computes the exact value of the
     * floating point number, not an approximation, as is often the case by
     * converting the floating point number to a string first. Remember,
     * though, that the exact value of a 32-bit binary floating-point
     * number is not always the value that results when passing a literal
     * decimal number (for example, calling
     * <code>ExtendedDecimal.FromSingle(0.1f)</code>), since not all decimal
     * numbers can be converted to exact binary numbers (in the example
     * given, the resulting arbitrary-precision decimal will be the the
     *  value of the closest "float" to 0.1, not 0.1 exactly). To create an
     * arbitrary-precision decimal number from a decimal value, use
     * FromString instead in most cases (for example:
     *  <code>ExtendedDecimal.FromString("0.1")</code>).
     * @param flt The parameter {@code flt} is a 32-bit binary floating-point
     * number.
     * @return An arbitrary-precision decimal number with the same value as {@code
     * flt}.
     */
    public static EDecimal FromSingle(float flt) {
      int value = Float.floatToRawIntBits(flt);
      boolean neg = (value >> 31) != 0;
      int floatExponent = (int)((value >> 23) & 0xff);
      int valueFpMantissa = value & 0x7fffff;
      if (floatExponent == 255) {
        if (valueFpMantissa == 0) {
          return neg ? NegativeInfinity : PositiveInfinity;
        }
        // Treat high bit of mantissa as quiet/signaling bit
        boolean quiet = (valueFpMantissa & 0x400000) != 0;
        valueFpMantissa &= 0x3fffff;
        value = (neg ? BigNumberFlags.FlagNegative : 0) |
          (quiet ? BigNumberFlags.FlagQuietNaN :
            BigNumberFlags.FlagSignalingNaN);
        return valueFpMantissa == 0 ? (quiet ? NaN : SignalingNaN) :
          new EDecimal(
            new FastIntegerFixed(valueFpMantissa),
            FastIntZero,
            value);
      }
      if (floatExponent == 0) {
        ++floatExponent;
      } else {
        valueFpMantissa |= 1 << 23;
      }
      if (valueFpMantissa == 0) {
        return neg ? EDecimal.NegativeZero : EDecimal.Zero;
      }
      floatExponent -= 150;
      while ((valueFpMantissa & 1) == 0) {
        ++floatExponent;
        valueFpMantissa >>= 1;
      }
      if (floatExponent == 0) {
        if (neg) {
          valueFpMantissa = -valueFpMantissa;
        }
        return EDecimal.FromInt64(valueFpMantissa);
      }
      if (floatExponent > 0) {
        // Value is an integer
        EInteger bigmantissa = EInteger.FromInt32(valueFpMantissa);
        bigmantissa = bigmantissa.ShiftLeft(floatExponent);
        if (neg) {
          bigmantissa=(bigmantissa).Negate();
        }
        return EDecimal.FromEInteger(bigmantissa);
      } else {
        // Value has a fractional part
        EInteger bigmantissa = EInteger.FromInt32(valueFpMantissa);
        EInteger bigexponent = NumberUtility.FindPowerOfFive(-floatExponent);
        bigmantissa = bigmantissa.Multiply(bigexponent);
        if (neg) {
          bigmantissa=(bigmantissa).Negate();
        }
        return EDecimal.Create(bigmantissa, EInteger.FromInt32(floatExponent));
      }
    }

    /**
     * Creates an arbitrary-precision decimal number from a text string that
     * represents a number. See <code>FromString(string, int, int,
     * EContext)</code> for more information. Note that calling the overload
     * that takes an EContext is often much faster than creating the
     * EDecimal then calling <code>RoundToPrecision</code> on that EDecimal,
     * especially if the context specifies a precision limit and exponent
     * range.
     * @param str A string that represents a number.
     * @return An arbitrary-precision decimal number with the same value as the
     * given string.
     * @throws NumberFormatException The parameter {@code str} is not a correctly
     * formatted number string.
     */
    public static EDecimal FromString(String str) {
      return FromString(str, 0, str == null ? 0 : str.length(), null);
    }

    /**
     * Creates an arbitrary-precision decimal number from a text string that
     * represents a number. See <code>FromString(string, int, int,
     * EContext)</code> for more information.
     * @param str A string that represents a number.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * Note that providing a context is often much faster than creating the
     * EDecimal without a context then calling {@code RoundToPrecision} on
     * that EDecimal, especially if the context specifies a precision limit
     * and exponent range.
     * @return An arbitrary-precision decimal number with the same value as the
     * given string.
     * @throws NullPointerException The parameter {@code str} is null.
     */
    public static EDecimal FromString(String str, EContext ctx) {
      return FromString(str, 0, str == null ? 0 : str.length(), ctx);
    }

    /**
     * Creates an arbitrary-precision decimal number from a text string that
     * represents a number. See <code>FromString(string, int, int,
     * EContext)</code> for more information. Note that calling the overload
     * that takes an EContext is often much faster than creating the
     * EDecimal then calling <code>RoundToPrecision</code> on that EDecimal,
     * especially if the context specifies a precision limit and exponent
     * range.
     * @param str A string that represents a number.
     * @param offset An index starting at 0 showing where the desired portion of
     * {@code str} begins.
     * @param length The length, in code units, of the desired portion of {@code
     * str} (but not more than {@code str} 's length).
     * @return An arbitrary-precision decimal number with the same value as the
     * given string.
     * @throws NumberFormatException The parameter {@code str} is not a correctly
     * formatted number string.
     * @throws NullPointerException The parameter {@code str} is null.
     * @throws IllegalArgumentException Either {@code offset} or {@code length} is less
     * than 0 or greater than {@code str} 's length, or {@code str} 's
     * length minus {@code offset} is less than {@code length}.
     */
    public static EDecimal FromString(
      String str,
      int offset,
      int length) {
      return FromString(str, offset, length, null);
    }

    // private static final System.Diagnostics.Stopwatch swRound = new
    // System.Diagnostics.Stopwatch();

    /**
     * <p>Creates an arbitrary-precision decimal number from a text string that
     * represents a number.</p> <p>The format of the string generally
     *  consists of:</p> <ul> <li>An optional plus sign ("+" , U+002B) or
     *  minus sign ("-", U+002D) (if the minus sign, the value is
     * negative.)</li> <li>One or more digits, with a single optional
     *  decimal point (".", U+002E) before or after those digits or between
     * two of them. These digits may begin with any number of zeros.</li>
     *  <li>Optionally, "E"/"e" followed by an optional (positive exponent)
     *  or "-" (negative exponent) and followed by one or more digits
     * specifying the exponent (these digits may begin with any number of
     *  zeros).</li></ul> <p>The string can also be "-INF", "-Infinity",
     *  "Infinity", "INF", quiet NaN ("NaN" /"-NaN") followed by any number
     * of digits (these digits may begin with any number of zeros), or
     *  signaling NaN ("sNaN" /"-sNaN") followed by any number of digits
     * (these digits may begin with any number of zeros), all where the
     * letters can be any combination of basic upper-case and/or basic
     * lower-case letters.</p> <p>All characters mentioned above are the
     * corresponding characters in the Basic Latin range. In particular,
     * the digits must be the basic digits 0 to 9 (U+0030 to U+0039). The
     * string is not allowed to contain white space characters, including
     * spaces.</p>
     * @param str A text string, a portion of which represents a number.
     * @param offset An index starting at 0 showing where the desired portion of
     * {@code str} begins.
     * @param length The length, in code units, of the desired portion of {@code
     * str} (but not more than {@code str} 's length).
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * Note that providing a context is often much faster than creating the
     * EDecimal without a context then calling {@code RoundToPrecision} on
     * that EDecimal, especially if the context specifies a precision limit
     * and exponent range.
     * @return An arbitrary-precision decimal number with the same value as the
     * given string.
     * @throws NullPointerException The parameter {@code str} is null.
     * @throws IllegalArgumentException Either {@code offset} or {@code length} is less
     * than 0 or greater than {@code str} 's length, or {@code str} 's
     * length minus {@code offset} is less than {@code length}.
     */
    public static EDecimal FromString(
      String str,
      int offset,
      int length,
      EContext ctx) {
      int tmpoffset = offset;
      if (str == null) {
        throw new NullPointerException("str");
      }
      if (tmpoffset < 0) {
        throw new NumberFormatException("offset(" + tmpoffset + ") is less than " +
          "0");
      }
      if (tmpoffset > str.length()) {
        throw new NumberFormatException("offset(" + tmpoffset + ") is more than " +
          str.length());
      }
      if (length < 0) {
        throw new NumberFormatException("length(" + length + ") is less than " +
          "0");
      }
      if (length > str.length()) {
        throw new NumberFormatException("length(" + length + ") is more than " +
          str.length());
      }
      if (str.length() - tmpoffset < length) {
        throw new NumberFormatException("str's length minus " + tmpoffset + "(" +
          (str.length() - tmpoffset) + ") is less than " + length);
      }
      if (length == 0) {
        throw new NumberFormatException();
      }
      boolean negative = false;
      int endStr = tmpoffset + length;
      if (str.charAt(tmpoffset) == '+' || str.charAt(tmpoffset) == '-') {
        negative = str.charAt(tmpoffset) == '-';
        ++tmpoffset;
      }
      int mantInt = 0;
      EInteger mant = null;
      boolean haveDigits = false;
      int digitStart = 0;
      int i = tmpoffset;
      if (i + 8 == endStr) {
        if ((str.charAt(i) == 'I' || str.charAt(i) == 'i') &&
          (str.charAt(i + 1) == 'N' || str.charAt(i + 1) == 'n') &&
          (str.charAt(i + 2) == 'F' || str.charAt(i + 2) == 'f') &&
          (str.charAt(i + 3) == 'I' || str.charAt(i + 3) == 'i') && (str.charAt(i + 4) == 'N' ||
            str.charAt(i + 4) == 'n') && (str.charAt(i + 5) == 'I' || str.charAt(i + 5) == 'i') &&
          (str.charAt(i + 6) == 'T' || str.charAt(i + 6) == 't') && (str.charAt(i + 7) == 'Y' ||
            str.charAt(i + 7) == 'y')) {
          if (ctx != null && ctx.isSimplified() && i < endStr) {
            throw new NumberFormatException("Infinity not allowed");
          }
          return negative ? NegativeInfinity : PositiveInfinity;
        }
      }
      if (i + 3 == endStr) {
        if ((str.charAt(i) == 'I' || str.charAt(i) == 'i') &&
          (str.charAt(i + 1) == 'N' || str.charAt(i + 1) == 'n') && (str.charAt(i + 2) == 'F' ||
            str.charAt(i + 2) == 'f')) {
          if (ctx != null && ctx.isSimplified() && i < endStr) {
            throw new NumberFormatException("Infinity not allowed");
          }
          return negative ? NegativeInfinity : PositiveInfinity;
        }
      }
      if (i + 3 <= endStr) {
        // Quiet NaN
        if ((str.charAt(i) == 'N' || str.charAt(i) == 'n') && (str.charAt(i + 1) == 'A' || str.charAt(i +
              1) == 'a') && (str.charAt(i + 2) == 'N' || str.charAt(i + 2) == 'n')) {
          if (ctx != null && ctx.isSimplified() && i < endStr) {
            throw new NumberFormatException("NaN not allowed");
          }
          int flags2 = (negative ? BigNumberFlags.FlagNegative : 0) |
            BigNumberFlags.FlagQuietNaN;
          if (i + 3 == endStr) {
            return (!negative) ? NaN : new EDecimal(
                FastIntZero,
                FastIntZero,
                flags2);
          }
          i += 3;
          FastInteger digitCount = new FastInteger(0);
          FastInteger maxDigits = null;
          haveDigits = false;
          if (ctx != null && ctx.getHasMaxPrecision()) {
            maxDigits = FastInteger.FromBig(ctx.getPrecision());
            if (ctx.getClampNormalExponents()) {
              maxDigits.Decrement();
            }
          }
          digitStart = i;
          for (; i < endStr; ++i) {
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
              int thisdigit = (int)(str.charAt(i) - '0');
              haveDigits = haveDigits || thisdigit != 0;
              if (mantInt <= MaxSafeInt) {
                // multiply by 10
                mantInt = (mantInt << 3) + (mantInt << 1);
                mantInt += thisdigit;
              }
              if (haveDigits && maxDigits != null) {
                digitCount.Increment();
                if (digitCount.compareTo(maxDigits) > 0) {
                  // NaN contains too many digits
                  throw new NumberFormatException();
                }
              }
            } else {
              throw new NumberFormatException();
            }
          }
          if (mantInt > MaxSafeInt) {
            mant = EInteger.FromSubstring(str, digitStart, endStr);
          }
          EInteger bigmant = (mant == null) ? (EInteger.FromInt32(mantInt)) :
            mant;
          flags2 = (negative ? BigNumberFlags.FlagNegative : 0) |
            BigNumberFlags.FlagQuietNaN;
          return CreateWithFlags(
              FastIntegerFixed.FromBig(bigmant),
              FastIntZero,
              flags2);
        }
      }
      if (i + 4 <= endStr) {
        // Signaling NaN
        if ((str.charAt(i) == 'S' || str.charAt(i) == 's') && (str.charAt(i + 1) == 'N' || str.charAt(i +
              1) == 'n') && (str.charAt(i + 2) == 'A' || str.charAt(i + 2) == 'a') &&
          (str.charAt(i + 3) == 'N' || str.charAt(i + 3) == 'n')) {
          if (ctx != null && ctx.isSimplified() && i < endStr) {
            throw new NumberFormatException("NaN not allowed");
          }
          if (i + 4 == endStr) {
            int flags2 = (negative ? BigNumberFlags.FlagNegative : 0) |
              BigNumberFlags.FlagSignalingNaN;
            return (!negative) ? SignalingNaN :
              new EDecimal(
                FastIntZero,
                FastIntZero,
                flags2);
          }
          i += 4;
          FastInteger digitCount = new FastInteger(0);
          FastInteger maxDigits = null;
          haveDigits = false;
          if (ctx != null && ctx.getHasMaxPrecision()) {
            maxDigits = FastInteger.FromBig(ctx.getPrecision());
            if (ctx.getClampNormalExponents()) {
              maxDigits.Decrement();
            }
          }
          digitStart = i;
          for (; i < endStr; ++i) {
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
              int thisdigit = (int)(str.charAt(i) - '0');
              haveDigits = haveDigits || thisdigit != 0;
              if (mantInt <= MaxSafeInt) {
                // multiply by 10
                mantInt = (mantInt << 3) + (mantInt << 1);
                mantInt += thisdigit;
              }
              if (haveDigits && maxDigits != null) {
                digitCount.Increment();
                if (digitCount.compareTo(maxDigits) > 0) {
                  // NaN contains too many digits
                  throw new NumberFormatException();
                }
              }
            } else {
              throw new NumberFormatException();
            }
          }
          if (mantInt > MaxSafeInt) {
            mant = EInteger.FromSubstring(str, digitStart, endStr);
          }
          int flags3 = (negative ? BigNumberFlags.FlagNegative : 0) |
            BigNumberFlags.FlagSignalingNaN;
          EInteger bigmant = (mant == null) ? (EInteger.FromInt32(mantInt)) :
            mant;
          return CreateWithFlags(
              bigmant,
              EInteger.FromInt32(0),
              flags3);
        }
      }
      if (ctx != null && ctx.getHasMaxPrecision() && ctx.getHasExponentRange() &&
        !ctx.isSimplified()) {
        return ParseOrdinaryNumberLimitedPrecision(
            str,
            i,
            endStr,
            negative,
            ctx);
      } else {
        return ParseOrdinaryNumber(str, i, endStr, negative, ctx);
      }
    }

    private static EDecimal SignalUnderflow(EContext ec, boolean negative, boolean
      zeroSignificand) {
      EInteger eTiny = ec.getEMin().Subtract(ec.getPrecision().Subtract(1));
      eTiny = eTiny.Subtract(1); // subtract 1 from proper eTiny to
      // trigger underflow
      EDecimal ret = EDecimal.Create(
          zeroSignificand ? EInteger.FromInt32(0) : EInteger.FromInt32(1),
          eTiny);
      if (negative) {
        ret = ret.Negate();
      }
      return ret.RoundToPrecision(ec);
    }

    private static EDecimal SignalOverflow(EContext ec, boolean negative, boolean
      zeroSignificand) {
      if (zeroSignificand) {
        EDecimal ret = EDecimal.Create(EInteger.FromInt32(0), ec.getEMax());
        if (negative) {
          ret = ret.Negate();
        }
        return ret.RoundToPrecision(ec);
      } else {
        return GetMathValue(ec).SignalOverflow(ec, negative);
      }
    }

    private static EDecimal ParseOrdinaryNumberLimitedPrecision(
      String str,
      int offset,
      int endStr,
      boolean negative,
      EContext ctx) {
      int tmpoffset = offset;
      if (str == null) {
        throw new NullPointerException("str");
      }
      if (ctx == null || !ctx.getHasMaxPrecision()) {
        throw new IllegalStateException();
      }
      boolean haveDecimalPoint = false;
      boolean haveDigits = false;
      boolean haveExponent = false;
      int newScaleInt = 0;
      int i = tmpoffset;
      long mantissaLong = 0L;
      // Ordinary number
      int digitStart = i;
      int digitEnd = i;
      int decimalDigitStart = i;
      boolean haveNonzeroDigit = false;
      int decimalPrec = 0;
      int decimalDigitEnd = i;
      boolean nonzeroBeyondMax = false;
      boolean beyondMax = false;
      int lastdigit = -1;
      for (; i < endStr; ++i) {
        char ch = str.charAt(i);
        if (ch >= '0' && ch <= '9') {
          int thisdigit = (int)(ch - '0');
          haveDigits = true;
          haveNonzeroDigit |= thisdigit != 0;
          if (beyondMax || (ctx.getPrecision().Add(2).compareTo(decimalPrec) < 0 &&
              mantissaLong == Long.MAX_VALUE)) {
            // Well beyond maximum precision, significand is
            // max or bigger
            beyondMax = true;
            if (thisdigit != 0) {
              nonzeroBeyondMax = true;
            }
            if (!haveDecimalPoint) {
              // NOTE: Absolute value will not be more than
              // the String portion's length, so will fit comfortably
              // in an 'int'.
              newScaleInt = (newScaleInt + 1);
            }
            continue;
          }
          lastdigit = thisdigit;
          if (haveNonzeroDigit) {
            ++decimalPrec;
          }
          if (haveDecimalPoint) {
            decimalDigitEnd = i + 1;
          } else {
            digitEnd = i + 1;
          }
          if (mantissaLong <= 922337203685477580L) {
            mantissaLong *= 10;
            mantissaLong += thisdigit;
          } else {
            mantissaLong = Long.MAX_VALUE;
          }
          if (haveDecimalPoint) {
            // NOTE: Absolute value will not be more than
            // the String portion's length, so will fit comfortably
            // in an 'int'.
            newScaleInt = (newScaleInt - 1);
          }
        } else if (ch == '.') {
          if (haveDecimalPoint) {
            throw new NumberFormatException();
          }
          haveDecimalPoint = true;
          decimalDigitStart = i + 1;
          decimalDigitEnd = i + 1;
        } else if (ch == 'E' || ch == 'e') {
          haveExponent = true;
          ++i;
          break;
        } else {
          throw new NumberFormatException();
        }
      }
      if (!haveDigits) {
        throw new NumberFormatException();
      }
      int expInt = 0;
      int expoffset = 1;
      int expDigitStart = -1;
      int expPrec = 0;
      boolean zeroMantissa = !haveNonzeroDigit;
      haveNonzeroDigit = false;
      if (haveExponent) {
        haveDigits = false;
        if (i == endStr) {
          throw new NumberFormatException();
        }
        if (str.charAt(i) == '+' || str.charAt(i) == '-') {
          if (str.charAt(i) == '-') {
            expoffset = -1;
          }
          ++i;
        }
        expDigitStart = i;
        for (; i < endStr; ++i) {
          char ch = str.charAt(i);
          if (ch >= '0' && ch <= '9') {
            haveDigits = true;
            int thisdigit = (int)(ch - '0');
            haveNonzeroDigit |= thisdigit != 0;
            if (haveNonzeroDigit) {
              ++expPrec;
            }
            if (expInt <= 214748364) {
              expInt *= 10;
              expInt += thisdigit;
            } else {
              expInt = Integer.MAX_VALUE;
            }
          } else {
            throw new NumberFormatException();
          }
        }
        if (!haveDigits) {
          throw new NumberFormatException();
        }
        expInt *= expoffset;
        if (expPrec > 12) {
          // Exponent that can't be compensated by digit
          // length without remaining higher than Integer.MAX_VALUE
          if (expoffset < 0) {
            return SignalUnderflow(ctx, negative, zeroMantissa);
          } else {
            return SignalOverflow(ctx, negative, zeroMantissa);
          }
        }
      }
      if (i != endStr) {
        throw new NumberFormatException();
      }
      if (expInt != Integer.MAX_VALUE && expInt > -Integer.MAX_VALUE &&
        mantissaLong != Long.MAX_VALUE) {
        // Low precision, low exponent
        long finalexp = (long)expInt + (long)newScaleInt;
        if (negative) {
          mantissaLong = -mantissaLong;
        }
        EDecimal eret = EDecimal.Create(
            EInteger.FromInt64(mantissaLong),
            EInteger.FromInt64(finalexp));
        if (negative && zeroMantissa) {
          eret = eret.Negate();
        }
        return eret.RoundToPrecision(ctx);
      }
      EInteger mant = null;
      EInteger exp = (!haveExponent) ? EInteger.FromInt32(0) :
        EInteger.FromSubstring(str, expDigitStart, endStr);
      if (expoffset < 0) {
        exp = exp.Negate();
      }
      exp = exp.Add(newScaleInt);
      if (nonzeroBeyondMax) {
        exp = exp.Subtract(1);
        ++decimalPrec;
      }
      if (ctx.getHasExponentRange()) {
        EInteger adjExpUpperBound = exp.Add(decimalPrec).Subtract(1);
        EInteger adjExpLowerBound = exp;
        EInteger eTiny = ctx.getEMin().Subtract(ctx.getPrecision().Subtract(1));
        eTiny = eTiny.Subtract(1);
        // DebugUtility.Log("exp=" + adjExpLowerBound + "~" +
        // adjExpUpperBound + ", emin={0} emax={1}", ctx.getEMin(), ctx.getEMax());
        if (adjExpUpperBound.compareTo(eTiny) < 0) {
          return SignalUnderflow(ctx, negative, zeroMantissa);
        } else if (adjExpLowerBound.compareTo(ctx.getEMax()) > 0) {
          return SignalOverflow(ctx, negative, zeroMantissa);
        }
      }
      if (zeroMantissa) {
        EDecimal ef = EDecimal.Create(
            EInteger.FromInt32(0),
            exp);
        if (negative) {
          ef = ef.Negate();
        }
        return ef.RoundToPrecision(ctx);
      } else if (decimalDigitStart != decimalDigitEnd) {
        String tmpstr = str.substring(digitStart, (digitStart)+(digitEnd - digitStart)) +
          str.substring(
            decimalDigitStart, (
            decimalDigitStart)+(decimalDigitEnd - decimalDigitStart));
        mant = EInteger.FromString(tmpstr);
      } else {
        mant = EInteger.FromSubstring(str, digitStart, digitEnd);
      }
      if (nonzeroBeyondMax) {
        mant = mant.Multiply(10).Add(1);
      }
      if (negative) {
        mant = mant.Negate();
      }
      return EDecimal.Create(mant, exp)
        .RoundToPrecision(ctx);
    }

    private static EDecimal ParseOrdinaryNumber(
      String str,
      int i,
      int endStr,
      boolean negative,
      EContext ctx) {
      int mantInt = 0;
      EInteger mant = null;
      boolean haveDecimalPoint = false;
      boolean haveExponent = false;
      int newScaleInt = 0;
      boolean haveDigits = false;
      EInteger newScale = null;
      // Ordinary number
      int digitStart = i;
      int digitEnd = i;
      int decimalDigitStart = i;
      boolean haveNonzeroDigit = false;
      int decimalPrec = 0;
      int decimalDigitEnd = i;
      // NOTE: Also check HasFlagsOrTraps here because
      // it's burdensome to determine which flags have
      // to be set when applying the optimization here
      boolean roundDown = ctx != null && ctx.getHasMaxPrecision() &&
        !ctx.isPrecisionInBits() && (ctx.getRounding() == ERounding.Down ||
          (negative && ctx.getRounding() == ERounding.Ceiling) ||
          (!negative && ctx.getRounding() == ERounding.Floor)) &&
        !ctx.getHasFlagsOrTraps();
      boolean roundHalf = ctx != null && ctx.getHasMaxPrecision() &&
        !ctx.isPrecisionInBits() && (ctx.getRounding() == ERounding.HalfUp ||
          (ctx.getRounding() == ERounding.HalfDown) ||
          (ctx.getRounding() == ERounding.HalfEven)) &&
        !ctx.getHasFlagsOrTraps();
      boolean roundUp = ctx != null && ctx.getHasMaxPrecision() &&
        !ctx.isPrecisionInBits() && (ctx.getRounding() == ERounding.Up ||
          (!negative && ctx.getRounding() == ERounding.Ceiling) ||
          (negative && ctx.getRounding() == ERounding.Floor)) &&
        !ctx.getHasFlagsOrTraps();
      boolean haveIgnoredDigit = false;
      int lastdigit = -1;
      boolean beyondPrecision = false;
      boolean ignoreNextDigit = false;
      int zerorun = 0;
      // DebugUtility.Log("round half=" + (// roundHalf) +
      // " up=" + roundUp + " down=" + roundDown +
      // " maxprec=" + (ctx != null && ctx.getHasMaxPrecision()));
      for (; i < endStr; ++i) {
        char ch = str.charAt(i);
        if (ch >= '0' && ch <= '9') {
          int thisdigit = (int)(ch - '0');
          haveNonzeroDigit |= thisdigit != 0;
          haveDigits = true;
          beyondPrecision |= ctx != null && ctx.getHasMaxPrecision() &&
            !ctx.isPrecisionInBits() && ctx.getPrecision().compareTo(decimalPrec)
            <= 0;
          if (ctx != null) {
            if (ignoreNextDigit) {
              haveIgnoredDigit = true;
              ignoreNextDigit = false;
            }
            if (roundDown && (haveIgnoredDigit || beyondPrecision)) {
              // "Ignored" digit
              haveIgnoredDigit = true;
            } else if (roundUp && beyondPrecision && !haveIgnoredDigit) {
              if (thisdigit > 0) {
                ignoreNextDigit = true;
              } else {
                roundUp = false;
              }
            } else if (roundHalf && beyondPrecision && !haveIgnoredDigit) {
              if (thisdigit >= 1 && thisdigit < 5) {
                ignoreNextDigit = true;
              } else if (thisdigit > 5 || (thisdigit == 5 &&
                  ctx.getRounding() == ERounding.HalfUp)) {
                roundHalf = false;
                roundUp = true;
                ignoreNextDigit = true;
              } else {
                roundHalf = false;
              }
            }
          }
          if (haveIgnoredDigit) {
            zerorun = 0;
            if (newScaleInt == Integer.MIN_VALUE ||
              newScaleInt == Integer.MAX_VALUE) {
              newScale = (newScale == null) ? (EInteger.FromInt32(newScaleInt)) : newScale;
              newScale = newScale.Add(1);
            } else {
              ++newScaleInt;
            }
          } else {
            lastdigit = thisdigit;
            if (beyondPrecision && thisdigit == 0) {
              ++zerorun;
            } else {
              zerorun = 0;
            }
            if (haveNonzeroDigit) {
              ++decimalPrec;
            }
            if (haveDecimalPoint) {
              decimalDigitEnd = i + 1;
            } else {
              digitEnd = i + 1;
            }
            if (mantInt <= MaxSafeInt) {
              // multiply by 10
              mantInt = (mantInt << 3) + (mantInt << 1);
              mantInt += thisdigit;
            }
          }
          if (haveDecimalPoint) {
            if (newScaleInt == Integer.MIN_VALUE ||
              newScaleInt == Integer.MAX_VALUE) {
              newScale = (newScale == null) ? (EInteger.FromInt32(newScaleInt)) : newScale;
              newScale = newScale.Subtract(1);
            } else {
              --newScaleInt;
            }
          }
        } else if (ch == '.') {
          if (haveDecimalPoint) {
            throw new NumberFormatException();
          }
          haveDecimalPoint = true;
          decimalDigitStart = i + 1;
          decimalDigitEnd = i + 1;
        } else if (ch == 'E' || ch == 'e') {
          haveExponent = true;
          ++i;
          break;
        } else {
          throw new NumberFormatException();
        }
      }
      if (!haveDigits) {
        throw new NumberFormatException();
      }
      /*
      if (ctx != null) {
       DebugUtility.Log("zerorun=" + zerorun + " roundup=" + roundUp +
       ", haveIgnored="+haveIgnoredDigit +", decimalPrec=" + decimalPrec +
      ", ctx="+ctx);
      }*/
      if (zerorun > 0 && lastdigit == 0 && (ctx == null ||
          !ctx.getHasFlagsOrTraps())) {
        decimalPrec -= zerorun;
        int nondec = 0;
        // NOTE: This check is apparently needed for correctness
        if (ctx == null || (!ctx.getHasMaxPrecision() ||
            decimalPrec - ctx.getPrecision().ToInt32Checked() > zerorun)) {
          if (haveDecimalPoint) {
            int decdigits = decimalDigitEnd - decimalDigitStart;
            nondec = Math.min(decdigits, zerorun);
            decimalDigitEnd -= nondec;
            int remain = zerorun - nondec;
            digitEnd -= remain;
            // DebugUtility.Log("remain={0} nondec={1}
            // newScale={2}",remain,nondec,newScaleInt);
            nondec = zerorun;
          } else {
            digitEnd -= zerorun;
            nondec = zerorun;
          }
          if (newScaleInt > Integer.MIN_VALUE + nondec &&
            newScaleInt < Integer.MAX_VALUE - nondec) {
            newScaleInt += nondec;
          } else {
            newScale = (newScale == null) ? (EInteger.FromInt32(newScaleInt)) : newScale;
            newScale = newScale.Add(nondec);
          }
        }
        // DebugUtility.Log("-->zerorun={0} prec={1} [whole={2}, dec={3}]
        // str={4}",zerorun,decimalPrec,
        // digitEnd-digitStart, decimalDigitEnd-decimalDigitStart, str);
      }
      // if (ctx != null) {
      // DebugUtility.Log("roundup [prec=" + decimalPrec + ", ctxprec=" +
      // (// ctx.getPrecision()) + ", str=" + (// str.substring(0,Math.min(20,
      // str.length()))) + "] " + (ctx.getRounding()));
      // }
      if (roundUp && ctx != null && ctx.getPrecision().compareTo(decimalPrec) < 0) {
        int precdiff = decimalPrec - ctx.getPrecision().ToInt32Checked();
        // DebugUtility.Log("precdiff = " + precdiff + " [prec=" + (// decimalPrec) +
        // ",
        // ctxprec=" + ctx.getPrecision() + "]");
        if (precdiff > 1) {
          int precchop = precdiff - 1;
          decimalPrec -= precchop;
          int nondec = precchop;
          // DebugUtility.Log("precchop=" + (precchop));
          if (haveDecimalPoint) {
            int decdigits = decimalDigitEnd - decimalDigitStart;
            // DebugUtility.Log("decdigits=" + decdigits + " decprecchop=" + (decdigits));
            decimalDigitEnd -= nondec;
            int remain = precchop - nondec;
            digitEnd -= remain;
          } else {
            digitEnd -= precchop;
          }
          if (newScaleInt < Integer.MAX_VALUE - nondec) {
            newScaleInt += nondec;
          } else {
            newScale = (newScale == null) ? (EInteger.FromInt32(newScaleInt)) : newScale;
            newScale = newScale.Add(nondec);
          }
        }
      }
      EDecimal ret = null;
      EInteger exp = null;
      int expInt = 0;
      int expoffset = 1;
      int expDigitStart = -1;
      int expPrec = 0;
      haveNonzeroDigit = false;
      if (haveExponent) {
        haveDigits = false;
        if (i == endStr) {
          throw new NumberFormatException();
        }
        if (str.charAt(i) == '+' || str.charAt(i) == '-') {
          if (str.charAt(i) == '-') {
            expoffset = -1;
          }
          ++i;
        }
        expDigitStart = i;
        for (; i < endStr; ++i) {
          char ch = str.charAt(i);
          if (ch >= '0' && ch <= '9') {
            haveDigits = true;
            int thisdigit = (int)(ch - '0');
            haveNonzeroDigit |= thisdigit != 0;
            if (haveNonzeroDigit) {
              ++expPrec;
            }
            if (expInt <= MaxSafeInt) {
              expInt *= 10;
              expInt += thisdigit;
            }
          } else {
            throw new NumberFormatException();
          }
        }
        if (!haveDigits) {
          throw new NumberFormatException();
        }
      }
      if (i != endStr) {
        throw new NumberFormatException();
      }
      // Calculate newScale if exponent is "small"
      if (haveExponent && expInt <= MaxSafeInt) {
        if (expoffset >= 0 && newScaleInt == 0 && newScale == null) {
          newScaleInt = expInt;
        } else {
          newScale = (newScale == null) ? (EInteger.FromInt32(newScaleInt)) : newScale;
          if (expoffset < 0) {
            newScale = newScale.Subtract(expInt);
          } else if (expInt != 0) {
            newScale = newScale.Add(expInt);
          }
        }
      }
      if (mantInt > MaxSafeInt || (haveExponent && expInt > MaxSafeInt)) {
        EInteger ns;
        if (expInt <= MaxSafeInt && ctx != null) {
          ns = (newScale == null) ? (EInteger.FromInt32(newScaleInt)) : newScale;
        } else {
          EInteger trialExponent = EInteger.FromInt32(MaxSafeInt);
          if (expPrec > 25) {
            // Exponent has many significant digits; use a bigger trial exponent
            trialExponent = EInteger.FromInt64(Long.MAX_VALUE);
          }
          // Trial exponent; in case of overflow or
          // underflow, the real exponent will also overflow or underflow
          if (expoffset >= 0 && newScaleInt == 0 && newScale == null) {
            ns = trialExponent;
          } else {
            ns = (newScale == null) ? (EInteger.FromInt32(newScaleInt)) : newScale;
            ns = (expoffset < 0) ? ns.Subtract(trialExponent) :
              ns.Add(trialExponent);
          }
        }
        EInteger eiDecPrec = EInteger.FromInt32(decimalPrec);
        int expwithin = CheckOverflowUnderflow(
            ctx,
            eiDecPrec,
            ns);
        if (mantInt == 0 && (expwithin == 1 || expwithin == 2 ||
            expwithin == 3)) {
          // Significand is zero
          ret = new EDecimal(
            new FastIntegerFixed(0),
            FastIntegerFixed.FromBig(ns),
            negative ? BigNumberFlags.FlagNegative : 0);
          return GetMathValue(ctx).RoundAfterConversion(ret, ctx);
        }
        if (expwithin == 1) {
          // Exponent indicates overflow
          return GetMathValue(ctx).SignalOverflow(ctx, negative);
        }
        if (expwithin == 2 || (expwithin == 3 && mantInt < MaxSafeInt)) {
          // Exponent indicates underflow to zero
          ret = new EDecimal(
            new FastIntegerFixed(expwithin == 3 ? mantInt : 1),
            FastIntegerFixed.FromBig(ns),
            negative ? BigNumberFlags.FlagNegative : 0);
          return GetMathValue(ctx).RoundAfterConversion(ret, ctx);
        } else if (expwithin == 3 && (ctx == null || ctx.getTraps() == 0)) {
          // Exponent indicates underflow to zero, adjust exponent
          ret = new EDecimal(
            new FastIntegerFixed(1),
            FastIntegerFixed.FromBig(ns),
            negative ? BigNumberFlags.FlagNegative : 0);
          ret = GetMathValue(ctx).RoundAfterConversion(ret, ctx);
          ns = ret.getExponent().Subtract(eiDecPrec.Subtract(1));
          ret = new EDecimal(
            ret.unsignedMantissa.Copy(),
            FastIntegerFixed.FromBig(ns),
            ret.flags);
          return ret;
        }
      }
      // Parse significand if it's "big"
      if (mantInt > MaxSafeInt) {
        if (haveDecimalPoint) {
          String decstr = str.substring(digitStart, (digitStart)+(digitEnd - digitStart)) +
            str.substring(
              decimalDigitStart, (
              decimalDigitStart)+(decimalDigitEnd - decimalDigitStart));
          mant = EInteger.FromString(decstr);
        } else {
          mant = EInteger.FromSubstring(str, digitStart, digitEnd);
        }
      }
      if (haveExponent && expInt > MaxSafeInt) {
        // Parse exponent if it's "big"
        exp = EInteger.FromSubstring(str, expDigitStart, endStr);
        newScale = (newScale == null) ? (EInteger.FromInt32(newScaleInt)) : newScale;
        newScale = (expoffset < 0) ? newScale.Subtract(exp) :
          newScale.Add(exp);
      }
      FastIntegerFixed fastIntScale;
      FastIntegerFixed fastIntMant;
      fastIntScale = (newScale == null) ? new FastIntegerFixed(newScaleInt) :
        FastIntegerFixed.FromBig(newScale);
      int sign = negative ? -1 : 1;
      if (mant == null) {
        fastIntMant = new FastIntegerFixed(mantInt);
        if (mantInt == 0) {
          sign = 0;
        }
      } else if (mant.CanFitInInt32()) {
        mantInt = mant.ToInt32Checked();
        fastIntMant = new FastIntegerFixed(mantInt);
        if (mantInt == 0) {
          sign = 0;
        }
      } else {
        fastIntMant = FastIntegerFixed.FromBig(mant);
      }
      ret = new EDecimal(
        fastIntMant,
        fastIntScale,
        negative ? BigNumberFlags.FlagNegative : 0);
      if (ctx != null) {
        ret = GetMathValue(ctx).RoundAfterConversion(ret, ctx);
      }
      return ret;
    }

    // 1 = Overflow; 2 = Underflow, adjust significand to 1; 0 = None;
    // 3 = Underflow, adjust significant to have precision
    private static int CheckOverflowUnderflow(
      EContext ec,
      EInteger precision,
      EInteger exponent) {
      // NOTE: Not guaranteed to catch all overflows or underflows.
      if (exponent == null) {
        throw new NullPointerException("exponent");
      }
      if (precision == null) {
        throw new NullPointerException("precision");
      }
      if (!(precision.signum() >= 0)) {
        throw new IllegalArgumentException("doesn't satisfy precision.signum()>= 0");
      }
      // "Precision" is the number of digits in a number starting with
      // the first nonzero digit
      if (ec == null || !ec.getHasExponentRange()) {
        return 0;
      }
      if (ec.getAdjustExponent()) {
        // If precision is in bits, this is too difficult to determine,
        // so ignore precision
        if (ec.isPrecisionInBits()) {
          if (exponent.compareTo(ec.getEMax()) > 0) {
            return 2; // Underflow
          }
        } else {
          EInteger adjExponent = exponent.Add(precision).Subtract(1);
          if (adjExponent.compareTo(ec.getEMax()) > 0) {
            return 1; // Overflow
          }
          if (ec.getHasMaxPrecision()) {
            EInteger etiny = ec.getEMin().Subtract(ec.getPrecision().Subtract(1));
            etiny = etiny.Subtract(1); // Buffer in case of rounding
            // DebugUtility.Log("adj: adjexp=" + adjExponent + " exp=" + exponent + "
            // etiny="+etiny);
            if (adjExponent.compareTo(etiny) < 0) {
              return 2; // Underflow to zero
            }
          } else {
            EInteger etiny = ec.getEMin().Subtract(precision.Subtract(1));
            etiny = etiny.Subtract(1); // Buffer in case of rounding
            // DebugUtility.Log("adj: adjexp=" + adjExponent + " exp=" + exponent + "
            // etiny="+etiny);
            if (adjExponent.compareTo(etiny) < 0) {
              return 3; // Underflow to zero
            }
          }
        }
      } else {
        // Exponent range is independent of precision
        if (exponent.compareTo(ec.getEMax()) > 0) {
          return 1; // Overflow
        }
        if (!ec.isPrecisionInBits()) {
          EInteger adjExponent = exponent.Add(precision).Subtract(1);
          EInteger etiny = ec.getHasMaxPrecision() ?
            ec.getEMin().Subtract(ec.getPrecision().Subtract(1)) :
            ec.getEMin().Subtract(precision.Subtract(1));
          etiny = etiny.Subtract(1); // Buffer in case of rounding
          // DebugUtility.Log("noadj: adjexp=" + adjExponent + " exp=" + exponent + "
          // etiny="+etiny);
          if (adjExponent.compareTo(etiny) < 0) {
            return 2; // Underflow to zero
          }
        }
      }
      return 0;
    }

    /**
     * Gets the greater value between two decimal numbers.
     * @param first The first value to compare.
     * @param second The second value to compare.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return The larger value of the two numbers.
     */
    public static EDecimal Max(
      EDecimal first,
      EDecimal second,
      EContext ctx) {
      return GetMathValue(ctx).Max(first, second, ctx);
    }

    /**
     * Gets the greater value between two decimal numbers.
     * @param first An arbitrary-precision decimal number.
     * @param second Another arbitrary-precision decimal number.
     * @return The larger value of the two numbers.
     */
    public static EDecimal Max(
      EDecimal first,
      EDecimal second) {
      return Max(first, second, null);
    }

    /**
     * Gets the greater value between two values, ignoring their signs. If the
     * absolute values are equal, has the same effect as Max.
     * @param first The first value to compare.
     * @param second The second value to compare.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return An arbitrary-precision decimal number.
     */
    public static EDecimal MaxMagnitude(
      EDecimal first,
      EDecimal second,
      EContext ctx) {
      return GetMathValue(ctx).MaxMagnitude(first, second, ctx);
    }

    /**
     * Gets the greater value between two values, ignoring their signs. If the
     * absolute values are equal, has the same effect as Max.
     * @param first The first value to compare.
     * @param second The second value to compare.
     * @return An arbitrary-precision decimal number.
     */
    public static EDecimal MaxMagnitude(
      EDecimal first,
      EDecimal second) {
      return MaxMagnitude(first, second, null);
    }

    /**
     * Gets the lesser value between two decimal numbers.
     * @param first The first value to compare.
     * @param second The second value to compare.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return The smaller value of the two numbers.
     */
    public static EDecimal Min(
      EDecimal first,
      EDecimal second,
      EContext ctx) {
      return GetMathValue(ctx).Min(first, second, ctx);
    }

    /**
     * Gets the lesser value between two decimal numbers.
     * @param first The first value to compare.
     * @param second The second value to compare.
     * @return The smaller value of the two numbers.
     */
    public static EDecimal Min(
      EDecimal first,
      EDecimal second) {
      return Min(first, second, null);
    }

    /**
     * Gets the lesser value between two values, ignoring their signs. If the
     * absolute values are equal, has the same effect as Min.
     * @param first The first value to compare.
     * @param second The second value to compare.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return An arbitrary-precision decimal number.
     */
    public static EDecimal MinMagnitude(
      EDecimal first,
      EDecimal second,
      EContext ctx) {
      return GetMathValue(ctx).MinMagnitude(first, second, ctx);
    }

    /**
     * Gets the lesser value between two values, ignoring their signs. If the
     * absolute values are equal, has the same effect as Min.
     * @param first The first value to compare.
     * @param second The second value to compare.
     * @return An arbitrary-precision decimal number.
     */
    public static EDecimal MinMagnitude(
      EDecimal first,
      EDecimal second) {
      return MinMagnitude(first, second, null);
    }

    /**
     * Finds the constant π, the circumference of a circle divided by its diameter.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). <i>This parameter
     * can't be null, as π can never be represented exactly.</i>.
     * @return The constant π rounded to the given precision. Signals FlagInvalid
     * and returns not-a-number (NaN) if the parameter {@code ctx} is null
     * or the precision is unlimited (the context's Precision property is
     * 0).
     */
    public static EDecimal PI(EContext ctx) {
      return GetMathValue(ctx).Pi(ctx);
    }

    /**
     * Finds the absolute value of this object (if it's negative, it becomes
     * positive).
     * @return An arbitrary-precision decimal number. Returns signaling NaN if this
     * value is signaling NaN. (In this sense, this method is similar to
     *  the "copy-abs" operation in the General Decimal Arithmetic
     * Specification, except this method does not necessarily return a copy
     * of this object.).
     */
    public EDecimal Abs() {
      if (this.isNegative()) {
        EDecimal er = new EDecimal(
          this.unsignedMantissa,
          this.exponent,
          this.flags & ~BigNumberFlags.FlagNegative);
        return er;
      }
      return this;
    }

    /**
     * Returns a number with the same value as this one, but copying the sign
     * (positive or negative) of another number. (This method is similar to
     *  the "copy-sign" operation in the General Decimal Arithmetic
     * Specification, except this method does not necessarily return a copy
     * of this object.).
     * @param other A number whose sign will be copied.
     * @return An arbitrary-precision decimal number.
     * @throws NullPointerException The parameter {@code other} is null.
     */
    public EDecimal CopySign(EDecimal other) {
      if (other == null) {
        throw new NullPointerException("other");
      }
      if (this.isNegative()) {
        return other.isNegative() ? this : this.Negate();
      } else {
        return other.isNegative() ? this.Negate() : this;
      }
    }

    /**
     * Finds the absolute value of this object (if it's negative, it becomes
     * positive).
     * @param context An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and no rounding is needed.
     * @return The absolute value of this object. Signals FlagInvalid and returns
     * quiet NaN if this value is signaling NaN.
     */
    public EDecimal Abs(EContext context) {
      return ((context == null || context == EContext.UnlimitedHalfEven) ?
          ExtendedMathValue : MathValue).Abs(this, context);
    }

    /**
     * Adds this object and another decimal number and returns the result.
     * @param otherValue An arbitrary-precision decimal number.
     * @return The sum of the two objects.
     */
    public EDecimal Add(EDecimal otherValue) {
      if (this.isFinite() && otherValue != null && otherValue.isFinite() &&
        ((this.flags | otherValue.flags) & BigNumberFlags.FlagNegative) == 0 &&
        this.exponent.compareTo(otherValue.exponent) == 0) {
        FastIntegerFixed result = FastIntegerFixed.Add(
            this.unsignedMantissa,
            otherValue.unsignedMantissa);
        return new EDecimal(result, this.exponent, 0);
      }
      return this.Add(otherValue, EContext.UnlimitedHalfEven);
    }

    /**
     * Finds the sum of this object and another object. The result's exponent is
     * set to the lower of the exponents of the two operands.
     * @param otherValue The number to add to.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and no rounding is needed.
     * @return The sum of thisValue and the other object.
     */
    public EDecimal Add(
      EDecimal otherValue,
      EContext ctx) {
      return GetMathValue(ctx).Add(this, otherValue, ctx);
    }

    /**
     * Compares the mathematical values of this object and another object,
     * accepting NaN values. This method currently uses the rules given in
     * the CompareToValue method, so that it it is not consistent with the
     * Equals method, but it may change in a future version to use the
     * rules for the CompareToTotal method instead.
     * @param other An arbitrary-precision decimal number.
     * @return Less than 0 if this object's value is less than the other value, or
     * greater than 0 if this object's value is greater than the other
     * value or if {@code other} is null, or 0 if both values are equal.
     * This implementation returns a positive number if.
     */
    public int compareTo(EDecimal other) {
      return this.CompareToValue(other);
    }

    /**
     * Compares the mathematical values of this object and another object,
     * accepting NaN values. This method currently uses the rules given in
     * the CompareToValue method, so that it it is not consistent with the
     * Equals method, but it may change in a future version to use the
     * rules for the CompareToTotal method instead.
     * @param intOther The parameter {@code intOther} is a 32-bit signed integer.
     * @return Less than 0 if this object's value is less than the other value, or
     * greater than 0 if this object's value is greater than the other
     * value, or 0 if both values are equal.
     */
    public int compareTo(int intOther) {
      return this.CompareToValue(EDecimal.FromInt32(intOther));
    }

    /**
     * Compares the mathematical values of this object and another object,
     * accepting NaN values. <p>This method is not consistent with the
     * Equals method because two different numbers with the same
     * mathematical value, but different exponents, will compare as
     * equal.</p> <p>In this method, negative zero and positive zero are
     * considered equal.</p> <p>If this object or the other object is a
     * quiet NaN or signaling NaN, this method will not trigger an error.
     * Instead, NaN will compare greater than any other number, including
     * infinity. Two different NaN values will be considered equal.</p>
     * @param intOther The parameter {@code intOther} is a 32-bit signed integer.
     * @return Less than 0 if this object's value is less than the other value, or
     * greater than 0 if this object's value is greater than the other
     * value, or 0 if both values are equal.
     */
    public int CompareToValue(int intOther) {
      return this.CompareToValue(EDecimal.FromInt32(intOther));
    }

    /**
     * Compares the mathematical values of this object and another object,
     * accepting NaN values. <p>This method is not consistent with the
     * Equals method because two different numbers with the same
     * mathematical value, but different exponents, will compare as
     * equal.</p> <p>In this method, negative zero and positive zero are
     * considered equal.</p> <p>If this object or the other object is a
     * quiet NaN or signaling NaN, this method will not trigger an error.
     * Instead, NaN will compare greater than any other number, including
     * infinity. Two different NaN values will be considered equal.</p>
     * @param other An arbitrary-precision decimal number.
     * @return Less than 0 if this object's value is less than the other value, or
     * greater than 0 if this object's value is greater than the other
     * value or if {@code other} is null, or 0 if both values are equal.
     * This implementation returns a positive number if.
     */
    public int CompareToValue(EDecimal other) {
      return ExtendedMathValue.compareTo(this, other);
    }

    /**
     * Compares an arbitrary-precision binary floating-point number with this
     * instance.
     * @param other The other object to compare. Can be null.
     * @return Zero if the values are equal; a negative number if this instance is
     * less; or a positive number if this instance is greater. Returns 0 if
     * both values are NaN (even signaling NaN) and 1 if this value is NaN
     * (even signaling NaN) and the other isn't, or if the other value is
     * null. This implementation returns a positive number if.
     */
    public int CompareToBinary(EFloat other) {
      return CompareEDecimalToEFloat(this, other);
    }
    private static int CompareEDecimalToEFloat(EDecimal ed, EFloat ef) {
      if (ef == null) {
        return 1;
      }
      if (ed.IsNaN()) {
        return ef.IsNaN() ? 0 : 1;
      }
      int signA = ed.signum();
      int signB = ef.signum();
      if (signA != signB) {
        return (signA < signB) ? -1 : 1;
      }
      if (signB == 0 || signA == 0) {
        // Special case: Either operand is zero
        return 0;
      }
      if (ed.IsInfinity()) {
        if (ef.IsInfinity()) {
          // if we get here, this only means that
          // both are positive infinity or both
          // are negative infinity
          return 0;
        }
        return ed.isNegative() ? -1 : 1;
      }
      if (ef.IsInfinity()) {
        return ef.isNegative() ? 1 : -1;
      }
      // At this point, both numbers are finite and
      // have the same sign

      if (ef.getExponent().compareTo(EInteger.FromInt64(-1000)) < 0) {
        // For very low exponents, the conversion to decimal can take
        // very long, so try this approach
        if (ef.Abs(null).compareTo(EFloat.One) < 0) {
          // Abs less than 1
          if (ed.Abs(null).compareTo(EDecimal.One) >= 0) {
            // Abs 1 or more
            return (signA > 0) ? 1 : -1;
          }
        }
        // DebugUtility.Log("edexp=" + ed.getExponent() + ", efexp=" +
        // (ef.getExponent()));
        EInteger bitCount = ef.getMantissa().GetUnsignedBitLengthAsEInteger();
        EInteger absexp = ef.getExponent().Abs();
        if (absexp.compareTo(bitCount) > 0) {
          // Float's absolute value is less than 1, so do a trial comparison
          // using exponent closer to 0
          EFloat trial = EFloat.Create(ef.getMantissa(), EInteger.FromInt32(
                -1000));
          int trialcmp = CompareEDecimalToEFloat(ed, trial);
          if (ef.signum() < 0 && trialcmp < 0) {
            // if float and decimal are negative and
            // decimal is less than trial float (which in turn is
            // less than the actual float), then the decimal is
            // less than the actual float
            return -1;
          }
          if (ef.signum() > 0 && trialcmp > 0) {
            // if float and decimal are positive and
            // decimal is greater than trial float (which in turn is
            // greater than the actual float), then the decimal is
            // greater than the actual float
            return 1;
          }
        }
        EInteger thisAdjExp = GetAdjustedExponent(ed);
        EInteger otherAdjExp = GetAdjustedExponentBinary(ef);
        // DebugUtility.Log("taexp=" + thisAdjExp + ", oaexp=" + otherAdjExp);
        // DebugUtility.Log("td=" + ed.ToDouble() + ", tf=" + ef.ToDouble());
        if (
          thisAdjExp.signum() < 0 && thisAdjExp.compareTo(EInteger.FromInt64(-1000))
          >= 0 && otherAdjExp.compareTo(EInteger.FromInt64(-4000)) < 0) {
          // With these exponent combinations, the binary's absolute
          // value is less than the decimal's
          return (signA > 0) ? 1 : -1;
        }
        if (
          thisAdjExp.signum() < 0 && thisAdjExp.compareTo(EInteger.FromInt64(-1000)) <
          0 && otherAdjExp.compareTo(EInteger.FromInt64(-1000)) < 0) {
          thisAdjExp = thisAdjExp.Add(EInteger.FromInt32(1)).Abs();
          otherAdjExp = otherAdjExp.Add(EInteger.FromInt32(1)).Abs();
          EInteger ratio = otherAdjExp.Multiply(1000).Divide(thisAdjExp);
          // DebugUtility.Log("taexp={0}, oaexp={1} ratio={2}"
          // , thisAdjExp, otherAdjExp, ratio);
          // Check the ratio of the negative binary exponent to
          // negative the decimal exponent.
          // If the ratio times 1000, rounded down, is less than 3321, the
          // binary's absolute value is
          // greater. If it's 3322 or greater, the decimal's absolute value is
          // greater.
          // (If the two absolute values are equal, the ratio will approach
          // ln(10)/ln(2), or about 3.32193, as the exponents get higher and
          // higher.) This check assumes that both exponents are 1000 or
          // greater, when the ratio between exponents of equal values is
          // close to ln(10)/ln(2).
          if (ratio.compareTo(EInteger.FromInt64(3321)) < 0) {
            // Binary abs. value is greater
            return (signA > 0) ? -1 : 1;
          }
          if (ratio.compareTo(EInteger.FromInt64(3322)) >= 0) {
            return (signA > 0) ? 1 : -1;
          }
        }
      }
      if (ef.getExponent().compareTo(EInteger.FromInt64(1000)) > 0) {
        // Very high exponents
        EInteger bignum = EInteger.FromInt32(1).ShiftLeft(999);
        if (ed.Abs(null).compareTo(EDecimal.FromEInteger(bignum)) <=
          0) {
          // this object's absolute value is less
          return (signA > 0) ? -1 : 1;
        }
        // NOTE: The following check assumes that both
        // operands are nonzero
        EInteger thisAdjExp = GetAdjustedExponent(ed);
        EInteger otherAdjExp = GetAdjustedExponentBinary(ef);
        if (thisAdjExp.signum() > 0 && thisAdjExp.compareTo(otherAdjExp) >= 0) {
          // This Object's adjusted exponent is greater and is positive;
          // so this object's absolute value is greater, since exponents
          // have a greater value in decimal than in binary
          return (signA > 0) ? 1 : -1;
        }
        if (thisAdjExp.signum() > 0 && thisAdjExp.compareTo(1000) < 0 &&
          otherAdjExp.compareTo(4000) >= 0) {
          // With these exponent combinations, the binary's absolute
          // value is greater than the decimal's
          return (signA > 0) ? -1 : 1;
        }
        if (thisAdjExp.signum() > 0 && thisAdjExp.compareTo(EInteger.FromInt64(1000)) >= 0 &&
          otherAdjExp.compareTo(EInteger.FromInt64(1000)) >= 0) {
          thisAdjExp = thisAdjExp.Add(EInteger.FromInt32(1));
          otherAdjExp = otherAdjExp.Add(EInteger.FromInt32(1));
          EInteger ratio = otherAdjExp.Multiply(1000).Divide(thisAdjExp);
          // Check the ratio of the binary exponent to the decimal exponent.
          // If the ratio times 1000, rounded down, is less than 3321, the
          // decimal's absolute value is
          // greater. If it's 3322 or greater, the binary's absolute value is
          // greater.
          // (If the two absolute values are equal, the ratio will approach
          // ln(10)/ln(2), or about 3.32193, as the exponents get higher and
          // higher.) This check assumes that both exponents are 1000 or
          // greater, when the ratio between exponents of equal values is
          // close to ln(10)/ln(2).
          if (ratio.compareTo(EInteger.FromInt64(3321)) < 0) {
            // Decimal abs. value is greater
            return (signA > 0) ? 1 : -1;
          }
          if (ratio.compareTo(EInteger.FromInt64(3322)) >= 0) {
            return (signA > 0) ? -1 : 1;
          }
        }
      }
      EDecimal otherDec = EDecimal.FromEFloat(ef);
      return ed.compareTo(otherDec);
    }

    /**
     * Compares the mathematical values of this object and another object, treating
     * quiet NaN as signaling. <p>In this method, negative zero and
     * positive zero are considered equal.</p> <p>If this object or the
     * other object is a quiet NaN or signaling NaN, this method will
     * return a quiet NaN and will signal a FlagInvalid flag.</p>
     * @param other An arbitrary-precision decimal number.
     * @param ctx An arithmetic context. The precision, rounding, and exponent
     * range are ignored. If {@code HasFlags} of the context is true, will
     * store the flags resulting from the operation (the flags are in
     * addition to the pre-existing flags). Can be null.
     * @return Quiet NaN if this object or the other object is NaN, or 0 if both
     * objects have the same value, or -1 if this object is less than the
     * other value, or a 1 if this object is greater. This implementation
     * returns a positive number if.
     */
    public EDecimal CompareToSignal(
      EDecimal other,
      EContext ctx) {
      return GetMathValue(ctx).CompareToWithContext(this, other, true, ctx);
    }

    /**
     * Compares the absolute values of this object and another object, imposing a
     * total ordering on all possible values (ignoring their signs). In
     * this method: <ul> <li>For objects with the same value, the one with
     *  the higher exponent has a greater "absolute value".</li>
     * <li>Negative zero and positive zero are considered equal.</li>
     *  <li>Quiet NaN has a higher "absolute value" than signaling NaN. If
     * both objects are quiet NaN or both are signaling NaN, the one with
     *  the higher diagnostic information has a greater "absolute
     *  value".</li> <li>NaN has a higher "absolute value" than
     *  infinity.</li> <li>Infinity has a higher "absolute value" than any
     * finite number.</li></ul>
     * @param other An arbitrary-precision decimal number to compare with this one.
     * @return The number 0 if both objects have the same value (ignoring their
     * signs), or -1 if this object is less than the other value (ignoring
     * their signs), or 1 if this object is greater (ignoring their signs).
     * This implementation returns a positive number if.
     */
    public int CompareToTotalMagnitude(EDecimal other) {
      if (other == null) {
        return 1;
      }
      int valueIThis = 0;
      int valueIOther = 0;
      int cmp;
      if (this.IsSignalingNaN()) {
        valueIThis = 2;
      } else if (this.IsNaN()) {
        valueIThis = 3;
      } else if (this.IsInfinity()) {
        valueIThis = 1;
      }
      if (other.IsSignalingNaN()) {
        valueIOther = 2;
      } else if (other.IsNaN()) {
        valueIOther = 3;
      } else if (other.IsInfinity()) {
        valueIOther = 1;
      }
      if (valueIThis > valueIOther) {
        return 1;
      } else if (valueIThis < valueIOther) {
        return -1;
      }
      if (valueIThis >= 2) {
        cmp = this.unsignedMantissa.compareTo(
            other.unsignedMantissa);
        return cmp;
      } else if (valueIThis == 1) {
        return 0;
      } else {
        cmp = this.Abs().compareTo(other.Abs());
        if (cmp == 0) {
          cmp = this.exponent.compareTo(
              other.exponent);
          return cmp;
        }
        return cmp;
      }
    }

    /**
     * Compares the values of this object and another object, imposing a total
     * ordering on all possible values. In this method: <ul> <li>For
     * objects with the same value, the one with the higher exponent has a
     *  greater "absolute value".</li> <li>Negative zero is less than
     *  positive zero.</li> <li>Quiet NaN has a higher "absolute value" than
     * signaling NaN. If both objects are quiet NaN or both are signaling
     * NaN, the one with the higher diagnostic information has a greater
     *  "absolute value".</li> <li>NaN has a higher "absolute value" than
     *  infinity.</li> <li>Infinity has a higher "absolute value" than any
     * finite number.</li> <li>Negative numbers are less than positive
     * numbers.</li></ul>
     * @param other An arbitrary-precision decimal number to compare with this one.
     * @param ctx An arithmetic context. Flags will be set in this context only if
     * {@code HasFlags} and {@code IsSimplified} of the context are true
     * and only if an operand needed to be rounded before carrying out the
     * operation. Can be null.
     * @return The number 0 if both objects have the same value, or -1 if this
     * object is less than the other value, or 1 if this object is greater.
     * Does not signal flags if either value is signaling NaN. This
     * implementation returns a positive number if.
     */
    public int CompareToTotal(EDecimal other, EContext ctx) {
      if (other == null) {
        return 1;
      }
      if (this.IsSignalingNaN() || other.IsSignalingNaN()) {
        return this.CompareToTotal(other);
      }
      if (ctx != null && ctx.isSimplified()) {
        return this.RoundToPrecision(ctx)
          .CompareToTotal(other.RoundToPrecision(ctx));
        } else {
        return this.CompareToTotal(other);
      }
    }

    /**
     * Compares the values of this object and another object, imposing a total
     * ordering on all possible values (ignoring their signs). In this
     * method: <ul> <li>For objects with the same value, the one with the
     *  higher exponent has a greater "absolute value".</li> <li>Negative
     * zero is less than positive zero.</li> <li>Quiet NaN has a higher
     *  "absolute value" than signaling NaN. If both objects are quiet NaN
     * or both are signaling NaN, the one with the higher diagnostic
     *  information has a greater "absolute value".</li> <li>NaN has a
     *  higher "absolute value" than infinity.</li> <li>Infinity has a
     *  higher "absolute value" than any finite number.</li> <li>Negative
     * numbers are less than positive numbers.</li></ul>
     * @param other An arbitrary-precision decimal number to compare with this one.
     * @param ctx An arithmetic context. Flags will be set in this context only if
     * {@code HasFlags} and {@code IsSimplified} of the context are true
     * and only if an operand needed to be rounded before carrying out the
     * operation. Can be null.
     * @return The number 0 if both objects have the same value (ignoring their
     * signs), or -1 if this object is less than the other value (ignoring
     * their signs), or 1 if this object is greater (ignoring their signs).
     * Does not signal flags if either value is signaling NaN. This
     * implementation returns a positive number if.
     */
    public int CompareToTotalMagnitude(EDecimal other, EContext ctx) {
      if (other == null) {
        return 1;
      }
      if (this.IsSignalingNaN() || other.IsSignalingNaN()) {
        return this.CompareToTotalMagnitude(other);
      }
      if (ctx != null && ctx.isSimplified()) {
        return this.RoundToPrecision(ctx)
          .CompareToTotalMagnitude(other.RoundToPrecision(ctx));
        } else {
        return this.CompareToTotalMagnitude(other);
      }
    }

    /**
     * Compares the values of this object and another object, imposing a total
     * ordering on all possible values. In this method: <ul> <li>For
     * objects with the same value, the one with the higher exponent has a
     *  greater "absolute value".</li> <li>Negative zero is less than
     *  positive zero.</li> <li>Quiet NaN has a higher "absolute value" than
     * signaling NaN. If both objects are quiet NaN or both are signaling
     * NaN, the one with the higher diagnostic information has a greater
     *  "absolute value".</li> <li>NaN has a higher "absolute value" than
     *  infinity.</li> <li>Infinity has a higher "absolute value" than any
     * finite number.</li> <li>Negative numbers are less than positive
     * numbers.</li></ul>
     * @param other An arbitrary-precision decimal number to compare with this one.
     * @return The number 0 if both objects have the same value, or -1 if this
     * object is less than the other value, or 1 if this object is greater.
     * This implementation returns a positive number if.
     */
    public int CompareToTotal(EDecimal other) {
      if (other == null) {
        return -1;
      }
      boolean neg1 = this.isNegative();
      boolean neg2 = other.isNegative();
      if (neg1 != neg2) {
        return neg1 ? -1 : 1;
      }
      int valueIThis = 0;
      int valueIOther = 0;
      int cmp;
      if (this.IsSignalingNaN()) {
        valueIThis = 2;
      } else if (this.IsNaN()) {
        valueIThis = 3;
      } else if (this.IsInfinity()) {
        valueIThis = 1;
      }
      if (other.IsSignalingNaN()) {
        valueIOther = 2;
      } else if (other.IsNaN()) {
        valueIOther = 3;
      } else if (other.IsInfinity()) {
        valueIOther = 1;
      }
      if (valueIThis > valueIOther) {
        return neg1 ? -1 : 1;
      } else if (valueIThis < valueIOther) {
        return neg1 ? 1 : -1;
      }
      if (valueIThis >= 2) {
        cmp = this.unsignedMantissa.compareTo(
            other.unsignedMantissa);
        return neg1 ? -cmp : cmp;
      } else if (valueIThis == 1) {
        return 0;
      } else {
        cmp = this.compareTo(other);
        if (cmp == 0) {
          cmp = this.exponent.compareTo(
              other.exponent);
          return neg1 ? -cmp : cmp;
        }
        return cmp;
      }
    }

    /**
     * Compares the mathematical values of this object and another object. <p>In
     * this method, negative zero and positive zero are considered
     * equal.</p> <p>If this object or the other object is a quiet NaN or
     * signaling NaN, this method returns a quiet NaN, and will signal a
     * FlagInvalid flag if either is a signaling NaN.</p>
     * @param other An arbitrary-precision decimal number.
     * @param ctx An arithmetic context. The precision, rounding, and exponent
     * range are ignored. If {@code HasFlags} of the context is true, will
     * store the flags resulting from the operation (the flags are in
     * addition to the pre-existing flags). Can be null.
     * @return Quiet NaN if this object or the other object is NaN, or 0 if both
     * objects have the same value, or -1 if this object is less than the
     * other value, or 1 if this object is greater. This implementation
     * returns a positive number if.
     */
    public EDecimal CompareToWithContext(
      EDecimal other,
      EContext ctx) {
      return GetMathValue(ctx).CompareToWithContext(this, other, false, ctx);
    }

    /**
     * Divides this object by another decimal number and returns the result. When
     * possible, the result will be exact.
     * @param divisor The number to divide by.
     * @return The quotient of the two numbers. Returns infinity if the divisor is
     * 0 and the dividend is nonzero. Returns not-a-number (NaN) if the
     * divisor and the dividend are 0. Returns NaN if the result can't be
     * exact because it would have a nonterminating decimal expansion.
     */
    public EDecimal Divide(EDecimal divisor) {
      return this.Divide(
          divisor,
          EContext.ForRounding(ERounding.None));
    }

    /**
     * Divides this arbitrary-precision decimal number by another
     * arbitrary-precision decimal number. The preferred exponent for the
     * result is this object's exponent minus the divisor's exponent.
     * @param divisor The number to divide by.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and no rounding is needed.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0; or, either {@code ctx} is null or {@code
     * ctx} 's precision is 0, and the result would have a nonterminating
     * decimal expansion; or, the rounding mode is ERounding.None and the
     * result is not exact.
     */
    public EDecimal Divide(
      EDecimal divisor,
      EContext ctx) {
      return GetMathValue(ctx).Divide(this, divisor, ctx);
    }

    /**
     * Calculates the quotient and remainder using the DivideToIntegerNaturalScale
     * and the formula in RemainderNaturalScale.
     * @param divisor The number to divide by.
     * @return A 2 element array consisting of the quotient and remainder in that
     * order.
     * @deprecated Renamed to DivRemNaturalScale.
 */
@Deprecated
    public EDecimal[] DivideAndRemainderNaturalScale(EDecimal
      divisor) {
      return this.DivRemNaturalScale(divisor, null);
    }

    /**
     * Calculates the quotient and remainder using the DivideToIntegerNaturalScale
     * and the formula in RemainderNaturalScale.
     * @param divisor The number to divide by.
     * @param ctx An arithmetic context object to control the precision, rounding,
     * and exponent range of the result. This context will be used only in
     * the division portion of the remainder calculation; as a result, it's
     * possible for the remainder to have a higher precision than given in
     * this context. Flags will be set on the given context only if the
     * context's {@code HasFlags} is true and the integer part of the
     * division result doesn't fit the precision and exponent range without
     * rounding. Can be null, in which the precision is unlimited and no
     * additional rounding, other than the rounding down to an integer
     * after division, is needed.
     * @return A 2 element array consisting of the quotient and remainder in that
     * order.
     * @deprecated Renamed to DivRemNaturalScale.
 */
@Deprecated
    public EDecimal[] DivideAndRemainderNaturalScale(
      EDecimal divisor,
      EContext ctx) {
      return this.DivRemNaturalScale(divisor, ctx);
    }

    /**
     * Calculates the quotient and remainder using the DivideToIntegerNaturalScale
     * and the formula in RemainderNaturalScale.
     * @param divisor The number to divide by.
     * @return A 2 element array consisting of the quotient and remainder in that
     * order.
     */
    public EDecimal[] DivRemNaturalScale(EDecimal
      divisor) {
      return this.DivRemNaturalScale(divisor, null);
    }

    /**
     * Calculates the quotient and remainder using the DivideToIntegerNaturalScale
     * and the formula in RemainderNaturalScale.
     * @param divisor The number to divide by.
     * @param ctx An arithmetic context object to control the precision, rounding,
     * and exponent range of the result. This context will be used only in
     * the division portion of the remainder calculation; as a result, it's
     * possible for the remainder to have a higher precision than given in
     * this context. Flags will be set on the given context only if the
     * context's {@code HasFlags} is true and the integer part of the
     * division result doesn't fit the precision and exponent range without
     * rounding. Can be null, in which the precision is unlimited and no
     * additional rounding, other than the rounding down to an integer
     * after division, is needed.
     * @return A 2 element array consisting of the quotient and remainder in that
     * order.
     */
    public EDecimal[] DivRemNaturalScale(
      EDecimal divisor,
      EContext ctx) {
      EDecimal[] result = new EDecimal[2];
      result[0] = this.DivideToIntegerNaturalScale(divisor, null);
      result[1] = this.Subtract(
          result[0].Multiply(divisor, null),
          ctx);
      result[0] = result[0].RoundToPrecision(ctx);
      return result;
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent to the result.
     * @param divisor The number to divide by.
     * @param desiredExponentSmall The desired exponent. A negative number places
     * the cutoff point to the right of the usual decimal point (so a
     * negative number means the number of decimal places to round to). A
     * positive number places the cutoff point to the left of the usual
     * decimal point.
     * @param ctx An arithmetic context object to control the rounding mode to use
     * if the result must be scaled down to have the same exponent as this
     * value. If the precision given in the context is other than 0, calls
     * the Quantize method with both arguments equal to the result of the
     * operation (and can signal FlagInvalid and return NaN if the result
     * doesn't fit the given precision). If {@code HasFlags} of the context
     * is true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0. Signals FlagInvalid and returns not-a-number
     * (NaN) if the context defines an exponent range and the desired
     * exponent is outside that range. Signals FlagInvalid and returns
     * not-a-number (NaN) if the rounding mode is ERounding.None and the
     * result is not exact.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      long desiredExponentSmall,
      EContext ctx) {
      return this.DivideToExponent(
          divisor,
          EInteger.FromInt64(desiredExponentSmall),
          ctx);
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent (expressed as a 32-bit signed integer) to the result, using
     * the half-even rounding mode.
     * @param divisor The number to divide by.
     * @param desiredExponentInt The desired exponent. A negative number places the
     * cutoff point to the right of the usual decimal point (so a negative
     * number means the number of decimal places to round to). A positive
     * number places the cutoff point to the left of the usual decimal
     * point.
     * @param ctx An arithmetic context object to control the rounding mode to use
     * if the result must be scaled down to have the same exponent as this
     * value. If the precision given in the context is other than 0, calls
     * the Quantize method with both arguments equal to the result of the
     * operation (and can signal FlagInvalid and return NaN if the result
     * doesn't fit the given precision). If {@code HasFlags} of the context
     * is true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0. Signals FlagInvalid and returns not-a-number
     * (NaN) if the context defines an exponent range and the desired
     * exponent is outside that range. Signals FlagInvalid and returns
     * not-a-number (NaN) if the rounding mode is ERounding.None and the
     * result is not exact.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      int desiredExponentInt,
      EContext ctx) {
      return this.DivideToExponent(
          divisor,
          EInteger.FromInt32(desiredExponentInt),
          ctx);
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent to the result.
     * @param divisor The number to divide by.
     * @param desiredExponentSmall The desired exponent. A negative number places
     * the cutoff point to the right of the usual decimal point (so a
     * negative number means the number of decimal places to round to). A
     * positive number places the cutoff point to the left of the usual
     * decimal point.
     * @param rounding The rounding mode to use if the result must be scaled down
     * to have the same exponent as this value.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0. Signals FlagInvalid and returns not-a-number
     * (NaN) if the rounding mode is ERounding.None and the result is not
     * exact.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      long desiredExponentSmall,
      ERounding rounding) {
      return this.DivideToExponent(
          divisor,
          EInteger.FromInt64(desiredExponentSmall),
          EContext.ForRounding(rounding));
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent (expressed as a 32-bit signed integer) to the result, using
     * the half-even rounding mode.
     * @param divisor The number to divide by.
     * @param desiredExponentInt The desired exponent. A negative number places the
     * cutoff point to the right of the usual decimal point (so a negative
     * number means the number of decimal places to round to). A positive
     * number places the cutoff point to the left of the usual decimal
     * point.
     * @param rounding The rounding mode to use if the result must be scaled down
     * to have the same exponent as this value.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0. Signals FlagInvalid and returns not-a-number
     * (NaN) if the rounding mode is ERounding.None and the result is not
     * exact.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      int desiredExponentInt,
      ERounding rounding) {
      return this.DivideToExponent(
          divisor,
          EInteger.FromInt32(desiredExponentInt),
          EContext.ForRounding(rounding));
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent to the result.
     * @param divisor The number to divide by.
     * @param exponent The desired exponent. A negative number places the cutoff
     * point to the right of the usual decimal point (so a negative number
     * means the number of decimal places to round to). A positive number
     * places the cutoff point to the left of the usual decimal point.
     * @param ctx An arithmetic context object to control the rounding mode to use
     * if the result must be scaled down to have the same exponent as this
     * value. If the precision given in the context is other than 0, calls
     * the Quantize method with both arguments equal to the result of the
     * operation (and can signal FlagInvalid and return NaN if the result
     * doesn't fit the given precision). If {@code HasFlags} of the context
     * is true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0. Signals FlagInvalid and returns not-a-number
     * (NaN) if the context defines an exponent range and the desired
     * exponent is outside that range. Signals FlagInvalid and returns
     * not-a-number (NaN) if the rounding mode is ERounding.None and the
     * result is not exact.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      EInteger exponent,
      EContext ctx) {
      return GetMathValue(ctx).DivideToExponent(this, divisor, exponent, ctx);
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent to the result, using the half-even rounding mode.
     * @param divisor The number to divide by.
     * @param exponent The desired exponent. A negative number places the cutoff
     * point to the right of the usual decimal point (so a negative number
     * means the number of decimal places to round to). A positive number
     * places the cutoff point to the left of the usual decimal point.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      EInteger exponent) {
      return this.DivideToExponent(divisor, exponent, ERounding.HalfEven);
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent (expressed as a 64-bit signed integer) to the result, using
     * the half-even rounding mode.
     * @param divisor The number to divide by.
     * @param desiredExponentSmall The desired exponent. A negative number places
     * the cutoff point to the right of the usual decimal point (so a
     * negative number means the number of decimal places to round to). A
     * positive number places the cutoff point to the left of the usual
     * decimal point.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      long desiredExponentSmall) {
      return this.DivideToExponent(
          divisor,
          desiredExponentSmall,
          ERounding.HalfEven);
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent (expressed as a 32-bit signed integer) to the result, using
     * the half-even rounding mode.
     * @param divisor The number to divide by.
     * @param desiredExponentInt The desired exponent. A negative number places the
     * cutoff point to the right of the usual decimal point (so a negative
     * number means the number of decimal places to round to). A positive
     * number places the cutoff point to the left of the usual decimal
     * point.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      int desiredExponentInt) {
      return this.DivideToExponent(
          divisor,
          desiredExponentInt,
          ERounding.HalfEven);
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and gives a particular
     * exponent to the result.
     * @param divisor The number to divide by.
     * @param desiredExponent The desired exponent. A negative number places the
     * cutoff point to the right of the usual decimal point (so a negative
     * number means the number of decimal places to round to). A positive
     * number places the cutoff point to the left of the usual decimal
     * point.
     * @param rounding The rounding mode to use if the result must be scaled down
     * to have the same exponent as this value.
     * @return The quotient of the two objects. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Returns not-a-number (NaN) if the divisor and the dividend are 0.
     * Returns NaN if the rounding mode is ERounding.None and the result is
     * not exact.
     */
    public EDecimal DivideToExponent(
      EDecimal divisor,
      EInteger desiredExponent,
      ERounding rounding) {
      return this.DivideToExponent(
          divisor,
          desiredExponent,
          EContext.ForRounding(rounding));
    }

    /**
     * Divides two arbitrary-precision decimal numbers, and returns the integer
     * part of the result, rounded down, with the preferred exponent set to
     * this value's exponent minus the divisor's exponent.
     * @param divisor The number to divide by.
     * @return The integer part of the quotient of the two objects. Signals
     * FlagDivideByZero and returns infinity if the divisor is 0 and the
     * dividend is nonzero. Signals FlagInvalid and returns not-a-number
     * (NaN) if the divisor and the dividend are 0.
     */
    public EDecimal DivideToIntegerNaturalScale(EDecimal
      divisor) {
      return this.DivideToIntegerNaturalScale(
          divisor,
          EContext.ForRounding(ERounding.Down));
    }

    /**
     * Divides this object by another object, and returns the integer part of the
     * result (which is initially rounded down), with the preferred
     * exponent set to this value's exponent minus the divisor's exponent.
     * @param divisor The parameter {@code divisor} is an arbitrary-precision
     * decimal floating-point number.
     * @param ctx The parameter {@code ctx} is an EContext object.
     * @return The integer part of the quotient of the two objects. Signals
     * FlagInvalid and returns not-a-number (NaN) if the return value would
     * overflow the exponent range. Signals FlagDivideByZero and returns
     * infinity if the divisor is 0 and the dividend is nonzero. Signals
     * FlagInvalid and returns not-a-number (NaN) if the divisor and the
     * dividend are 0. Signals FlagInvalid and returns not-a-number (NaN)
     * if the rounding mode is ERounding.None and the result is not exact.
     */
    public EDecimal DivideToIntegerNaturalScale(
      EDecimal divisor,
      EContext ctx) {
      return GetMathValue(ctx).DivideToIntegerNaturalScale(
          this,
          divisor,
          ctx);
    }

    /**
     * Divides this object by another object, and returns the integer part of the
     * result, with the exponent set to 0.
     * @param divisor The number to divide by.
     * @param ctx An arithmetic context object to control the precision. The
     * rounding and exponent range settings of this context are ignored. If
     * {@code HasFlags} of the context is true, will also store the flags
     * resulting from the operation (the flags are in addition to the
     * pre-existing flags). Can be null, in which case the precision is
     * unlimited.
     * @return The integer part of the quotient of the two objects. The exponent
     * will be set to 0. Signals FlagDivideByZero and returns infinity if
     * the divisor is 0 and the dividend is nonzero. Signals FlagInvalid
     * and returns not-a-number (NaN) if the divisor and the dividend are
     * 0, or if the result doesn't fit the given precision.
     */
    public EDecimal DivideToIntegerZeroScale(
      EDecimal divisor,
      EContext ctx) {
      return GetMathValue(ctx).DivideToIntegerZeroScale(this, divisor, ctx);
    }

    /**
     * Divides this object by another decimal number and returns a result with the
     * same exponent as this object (the dividend).
     * @param divisor The number to divide by.
     * @param rounding The rounding mode to use if the result must be scaled down
     * to have the same exponent as this value.
     * @return The quotient of the two numbers. Signals FlagDivideByZero and
     * returns infinity if the divisor is 0 and the dividend is nonzero.
     * Signals FlagInvalid and returns not-a-number (NaN) if the divisor
     * and the dividend are 0. Signals FlagInvalid and returns not-a-number
     * (NaN) if the rounding mode is ERounding.None and the result is not
     * exact.
     */
    public EDecimal DivideToSameExponent(
      EDecimal divisor,
      ERounding rounding) {
      return this.DivideToExponent(
          divisor,
          this.exponent.ToEInteger(),
          EContext.ForRounding(rounding));
    }

    /**
     * Determines whether this object's significand, exponent, and properties are
     * equal to those of another object. Not-a-number values are considered
     * equal if the rest of their properties are equal.
     * @param other An arbitrary-precision decimal number.
     * @return {@code true} if this object's significand and exponent are equal to
     * those of another object; otherwise, {@code false}.
     */
    public boolean equals(EDecimal other) {
      return this.EqualsInternal(other);
    }

    /**
     * Determines whether this object's significand, exponent, and properties are
     * equal to those of another object and that other object is an
     * arbitrary-precision decimal number. Not-a-number values are
     * considered equal if the rest of their properties are equal.
     * @param obj The parameter {@code obj} is an arbitrary object.
     * @return {@code true} if the objects are equal; otherwise, {@code false}. In
     * this method, two objects are not equal if they don't have the same
     * type or if one is null and the other isn't.
     */
    @Override public boolean equals(Object obj) {
      return this.EqualsInternal(((obj instanceof EDecimal) ? (EDecimal)obj : null));
    }

    /**
     * Finds e (the base of natural logarithms) raised to the power of this
     * object's value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). <i>This parameter
     * can't be null, as the exponential function's results are generally
     * not exact.</i> (Unlike in the General Decimal Arithmetic
     * Specification, any rounding mode is allowed.).
     * @return Exponential of this object. If this object's value is 1, returns an
     *  approximation to " e" within the given precision. Signals
     * FlagInvalid and returns not-a-number (NaN) if the parameter {@code
     * ctx} is null or the precision is unlimited (the context's Precision
     * property is 0).
     */
    public EDecimal Exp(EContext ctx) {
      return GetMathValue(ctx).Exp(this, ctx);
    }

    /**
     * Calculates this object's hash code. No application or process IDs are used
     * in the hash code calculation.
     * @return A 32-bit signed integer.
     */
    @Override public int hashCode() {
      int valueHashCode = 964453631;
      {
        valueHashCode += 964453723 * this.exponent.hashCode();
        valueHashCode += 964453939 * this.unsignedMantissa.hashCode();
        valueHashCode += 964453967 * this.flags;
      }
      return valueHashCode;
    }

    /**
     * Gets a value indicating whether this object is positive or negative
     * infinity.
     * @return {@code true} if this object is positive or negative infinity;
     * otherwise, {@code false}.
     */
    public boolean IsInfinity() {
      return (this.flags & BigNumberFlags.FlagInfinity) != 0;
    }

    /**
     * Gets a value indicating whether this object is not a number (NaN).
     * @return {@code true} if this object is not a number (NaN); otherwise, {@code
     * false}.
     */
    public boolean IsNaN() {
      return (this.flags & (BigNumberFlags.FlagQuietNaN |
            BigNumberFlags.FlagSignalingNaN)) != 0;
    }

    /**
     * Returns whether this object is negative infinity.
     * @return {@code true} if this object is negative infinity; otherwise, {@code
     * false}.
     */
    public boolean IsNegativeInfinity() {
      return (this.flags & (BigNumberFlags.FlagInfinity |
            BigNumberFlags.FlagNegative)) == (BigNumberFlags.FlagInfinity |
          BigNumberFlags.FlagNegative);
    }

    /**
     * Returns whether this object is positive infinity.
     * @return {@code true} if this object is positive infinity; otherwise, {@code
     * false}.
     */
    public boolean IsPositiveInfinity() {
      return (this.flags & (BigNumberFlags.FlagInfinity |
            BigNumberFlags.FlagNegative)) == BigNumberFlags.FlagInfinity;
    }

    /**
     * Gets a value indicating whether this object is a quiet not-a-number value.
     * @return {@code true} if this object is a quiet not-a-number value;
     * otherwise, {@code false}.
     */
    public boolean IsQuietNaN() {
      return (this.flags & BigNumberFlags.FlagQuietNaN) != 0;
    }

    /**
     * Gets a value indicating whether this object is a signaling not-a-number
     * value.
     * @return {@code true} if this object is a signaling not-a-number value;
     * otherwise, {@code false}.
     */
    public boolean IsSignalingNaN() {
      return (this.flags & BigNumberFlags.FlagSignalingNaN) != 0;
    }

    /**
     * Finds the natural logarithm of this object, that is, the power (exponent)
     * that e (the base of natural logarithms) must be raised to in order
     * to equal this object's value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). <i>This parameter
     * can't be null, as the ln function's results are generally not
     * exact.</i> (Unlike in the General Decimal Arithmetic Specification,
     * any rounding mode is allowed.).
     * @return Ln(this object). Signals the flag FlagInvalid and returns NaN if
     * this object is less than 0 (the result would be a complex number
     * with a real part equal to Ln of this object's absolute value and an
     * imaginary part equal to pi, but the return value is still NaN.).
     * Signals FlagInvalid and returns not-a-number (NaN) if the parameter
     * {@code ctx} is null or the precision is unlimited (the context's
     * Precision property is 0). Signals no flags and returns negative
     * infinity if this object's value is 0.
     */
    public EDecimal Log(EContext ctx) {
      return GetMathValue(ctx).Ln(this, ctx);
    }

    /**
     * Finds the base-10 logarithm of this object, that is, the power (exponent)
     * that the number 10 must be raised to in order to equal this object's
     * value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). <i>This parameter
     * can't be null, as the ln function's results are generally not
     * exact.</i> (Unlike in the General Decimal Arithmetic Specification,
     * any rounding mode is allowed.).
     * @return Ln(this object)/Ln(10). Signals the flag FlagInvalid and returns
     * not-a-number (NaN) if this object is less than 0. Signals
     * FlagInvalid and returns not-a-number (NaN) if the parameter {@code
     * ctx} is null or the precision is unlimited (the context's Precision
     * property is 0).
     */
    public EDecimal Log10(EContext ctx) {
      return GetMathValue(ctx).Log10(this, ctx);
    }

    /**
     * Returns a number similar to this number but with the decimal point moved to
     * the left.
     * @param places The number of decimal places to move the decimal point to the
     * left. If this number is negative, instead moves the decimal point to
     * the right by this number's absolute value.
     * @return A number whose exponent is decreased by {@code places}, but not to
     * more than 0.
     */
    public EDecimal MovePointLeft(int places) {
      return this.MovePointLeft(EInteger.FromInt32(places), null);
    }

    /**
     * Returns a number similar to this number but with the decimal point moved to
     * the left.
     * @param places The number of decimal places to move the decimal point to the
     * left. If this number is negative, instead moves the decimal point to
     * the right by this number's absolute value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return A number whose exponent is decreased by {@code places}, but not to
     * more than 0.
     */
    public EDecimal MovePointLeft(int places, EContext ctx) {
      return this.MovePointLeft(EInteger.FromInt32(places), ctx);
    }

    /**
     * Returns a number similar to this number but with the decimal point moved to
     * the left.
     * @param bigPlaces The number of decimal places to move the decimal point to
     * the left. If this number is negative, instead moves the decimal
     * point to the right by this number's absolute value.
     * @return A number whose exponent is decreased by {@code bigPlaces}, but not
     * to more than 0.
     */
    public EDecimal MovePointLeft(EInteger bigPlaces) {
      return this.MovePointLeft(bigPlaces, null);
    }

    /**
     * Returns a number similar to this number but with the decimal point moved to
     * the left.
     * @param bigPlaces The number of decimal places to move the decimal point to
     * the left. If this number is negative, instead moves the decimal
     * point to the right by this number's absolute value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return A number whose exponent is decreased by {@code bigPlaces}, but not
     * to more than 0.
     */
    public EDecimal MovePointLeft(
      EInteger bigPlaces,
      EContext ctx) {
      return (!this.isFinite()) ? this.RoundToPrecision(ctx) :
        this.MovePointRight((bigPlaces).Negate(), ctx);
    }

    /**
     * Returns a number similar to this number but with the decimal point moved to
     * the right.
     * @param places The number of decimal places to move the decimal point to the
     * right. If this number is negative, instead moves the decimal point
     * to the left by this number's absolute value.
     * @return A number whose exponent is increased by {@code places}, but not to
     * more than 0.
     */
    public EDecimal MovePointRight(int places) {
      return this.MovePointRight(EInteger.FromInt32(places), null);
    }

    /**
     * Returns a number similar to this number but with the decimal point moved to
     * the right.
     * @param places The number of decimal places to move the decimal point to the
     * right. If this number is negative, instead moves the decimal point
     * to the left by this number's absolute value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return A number whose exponent is increased by {@code places}, but not to
     * more than 0.
     */
    public EDecimal MovePointRight(int places, EContext ctx) {
      return this.MovePointRight(EInteger.FromInt32(places), ctx);
    }

    /**
     * Returns a number similar to this number but with the decimal point moved to
     * the right.
     * @param bigPlaces The number of decimal places to move the decimal point to
     * the right. If this number is negative, instead moves the decimal
     * point to the left by this number's absolute value.
     * @return A number whose exponent is increased by {@code bigPlaces}, but not
     * to more than 0.
     */
    public EDecimal MovePointRight(EInteger bigPlaces) {
      return this.MovePointRight(bigPlaces, null);
    }

    /**
     * Returns a number similar to this number but with the decimal point moved to
     * the right.
     * @param bigPlaces The number of decimal places to move the decimal point to
     * the right. If this number is negative, instead moves the decimal
     * point to the left by this number's absolute value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return A number whose exponent is increased by {@code bigPlaces}, but not
     * to more than 0.
     */
    public EDecimal MovePointRight(
      EInteger bigPlaces,
      EContext ctx) {
      if (!this.isFinite()) {
        return this.RoundToPrecision(ctx);
      }
      EInteger bigExp = this.getExponent();
      bigExp = bigExp.Add(bigPlaces);
      if (bigExp.signum() > 0) {
        EInteger mant = this.unsignedMantissa.ToEInteger();
        EInteger bigPower = NumberUtility.FindPowerOfTenFromBig(bigExp);
        mant = mant.Multiply(bigPower);
        return CreateWithFlags(
            mant,
            EInteger.FromInt32(0),
            this.flags).RoundToPrecision(ctx);
      }
      return CreateWithFlags(
          this.unsignedMantissa,
          FastIntegerFixed.FromBig(bigExp),
          this.flags).RoundToPrecision(ctx);
    }

    /**
     * Multiplies two decimal numbers. The resulting exponent will be the sum of
     * the exponents of the two decimal numbers.
     * @param otherValue Another decimal number.
     * @return The product of the two decimal numbers.
     * @throws NullPointerException The parameter {@code otherValue} is null.
     */
    public EDecimal Multiply(EDecimal otherValue) {
      if (otherValue == null) {
        throw new NullPointerException("otherValue");
      }
      if (this.isFinite() && otherValue.isFinite()) {
        int newflags = otherValue.flags ^ this.flags;
        if (this.unsignedMantissa.CanFitInInt32() &&
          otherValue.unsignedMantissa.CanFitInInt32()) {
          int integerA = this.unsignedMantissa.AsInt32();
          int integerB = otherValue.unsignedMantissa.AsInt32();
          long longA = ((long)integerA) * ((long)integerB);
          FastIntegerFixed exp = FastIntegerFixed.Add(
              this.exponent,
              otherValue.exponent);
          if ((longA >> 31) == 0) {
            return new EDecimal(
                new FastIntegerFixed((int)longA),
                exp,
                newflags);
          } else {
            return new EDecimal(
                FastIntegerFixed.FromBig(EInteger.FromInt64(longA)),
                exp,
                newflags);
          }
        } else {
          EInteger eintA = this.unsignedMantissa.ToEInteger().Multiply(
              otherValue.unsignedMantissa.ToEInteger());
          return new EDecimal(
              FastIntegerFixed.FromBig(eintA),
              FastIntegerFixed.Add(this.exponent, otherValue.exponent),
              newflags);
        }
      }
      return this.Multiply(otherValue, EContext.UnlimitedHalfEven);
    }

    /**
     * Multiplies two decimal numbers. The resulting scale will be the sum of the
     * scales of the two decimal numbers. The result's sign is positive if
     * both operands have the same sign, and negative if they have
     * different signs.
     * @param op Another decimal number.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return The product of the two decimal numbers.
     */
    public EDecimal Multiply(EDecimal op, EContext ctx) {
      return GetMathValue(ctx).Multiply(this, op, ctx);
    }

    /**
     * Adds this object and an 32-bit signed integer and returns the result.
     * @param intValue A 32-bit signed integer to add to this object.
     * @return The sum of the two objects.
     */
    public EDecimal Add(int intValue) {
      return this.Add(EDecimal.FromInt32(intValue));
    }

    /**
     * Subtracts a 32-bit signed integer from this object and returns the result.
     * @param intValue A 32-bit signed integer to subtract from this object.
     * @return The difference of the two objects.
     */
    public EDecimal Subtract(int intValue) {
      return (intValue == Integer.MIN_VALUE) ?
        this.Subtract(EDecimal.FromInt32(intValue)) : this.Add(-intValue);
    }

    /**
     * Multiplies this object by the given 32-bit signed integer. The resulting
     * exponent will be the sum of the exponents of the two numbers.
     * @param intValue A 32-bit signed integer to multiply this object by.
     * @return The product of the two numbers.
     */
    public EDecimal Multiply(int intValue) {
      return this.Multiply(EDecimal.FromInt32(intValue));
    }

    /**
     * Divides this object by an 32-bit signed integer and returns the result. When
     * possible, the result will be exact.
     * @param intValue A 32-bit signed integer, the divisor, to divide this object
     * by.
     * @return The quotient of the two numbers. Returns infinity if the divisor is
     * 0 and the dividend is nonzero. Returns not-a-number (NaN) if the
     * divisor and the dividend are 0. Returns NaN if the result can't be
     * exact because it would have a nonterminating decimal expansion.
     */
    public EDecimal Divide(int intValue) {
      return this.Divide(EDecimal.FromInt32(intValue));
    }

    /**
     * Multiplies by one decimal number, and then adds another decimal number.
     * @param multiplicand The value to multiply.
     * @param augend The value to add.
     * @return An arbitrary-precision decimal floating-point number.
     */
    public EDecimal MultiplyAndAdd(
      EDecimal multiplicand,
      EDecimal augend) {
      return this.MultiplyAndAdd(multiplicand, augend, null);
    }

    /**
     * Multiplies by one value, and then adds another value.
     * @param op The value to multiply.
     * @param augend The value to add.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed. If
     * the precision doesn't indicate a simplified arithmetic, rounding and
     * precision.Divide(exponent) adjustment is done only once, namely,
     * after multiplying and adding.
     * @return The result thisValue * multiplicand + augend.
     */
    public EDecimal MultiplyAndAdd(
      EDecimal op,
      EDecimal augend,
      EContext ctx) {
      return GetMathValue(ctx).MultiplyAndAdd(this, op, augend, ctx);
    }

    /**
     * Multiplies by one value, and then subtracts another value.
     * @param op The value to multiply.
     * @param subtrahend The value to subtract.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed. If
     * the precision doesn't indicate a simplified arithmetic, rounding and
     * precision.Divide(exponent) adjustment is done only once, namely,
     * after multiplying and subtracting.
     * @return The result thisValue * multiplicand - subtrahend.
     * @throws NullPointerException The parameter {@code op} or {@code subtrahend}
     * is null.
     */
    public EDecimal MultiplyAndSubtract(
      EDecimal op,
      EDecimal subtrahend,
      EContext ctx) {
      if (op == null) {
        throw new NullPointerException("op");
      }
      if (subtrahend == null) {
        throw new NullPointerException("subtrahend");
      }
      EDecimal negated = subtrahend;
      if ((subtrahend.flags & BigNumberFlags.FlagNaN) == 0) {
        int newflags = subtrahend.flags ^ BigNumberFlags.FlagNegative;
        negated = CreateWithFlags(
            subtrahend.unsignedMantissa,
            subtrahend.exponent,
            newflags);
      }
      return GetMathValue(ctx)
        .MultiplyAndAdd(this, op, negated, ctx);
    }

    /**
     * Gets an object with the same value as this one, but with the sign reversed.
     * @return An arbitrary-precision decimal number. If this value is positive
     * zero, returns negative zero. Returns signaling NaN if this value is
     * signaling NaN. (In this sense, this method is similar to the
     *  "copy-negate" operation in the General Decimal Arithmetic
     * Specification, except this method does not necessarily return a copy
     * of this object.).
     */
    public EDecimal Negate() {
      return new EDecimal(
          this.unsignedMantissa,
          this.exponent,
          this.flags ^ BigNumberFlags.FlagNegative);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but with the sign reversed.
     * @param context An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return An arbitrary-precision decimal number. If this value is positive
     * zero, returns positive zero. Signals FlagInvalid and returns quiet
     * NaN if this value is signaling NaN.
     */
    public EDecimal Negate(EContext context) {
      return ((context == null || context == EContext.UnlimitedHalfEven) ?
          ExtendedMathValue : MathValue).Negate(this, context);
    }

    /**
     * Finds the largest value that's smaller than the given value.
     * @param ctx An arithmetic context object to control the precision and
     * exponent range of the result. The rounding mode from this context is
     * ignored. If {@code HasFlags} of the context is true, will also store
     * the flags resulting from the operation (the flags are in addition to
     * the pre-existing flags).
     * @return Returns the largest value that's less than the given value. Returns
     * negative infinity if the result is negative infinity. Signals
     * FlagInvalid and returns not-a-number (NaN) if the parameter {@code
     * ctx} is null, the precision is 0, or {@code ctx} has an unlimited
     * exponent range.
     */
    public EDecimal NextMinus(EContext ctx) {
      return GetMathValue(ctx).NextMinus(this, ctx);
    }

    /**
     * Finds the smallest value that's greater than the given value.
     * @param ctx An arithmetic context object to control the precision and
     * exponent range of the result. The rounding mode from this context is
     * ignored. If {@code HasFlags} of the context is true, will also store
     * the flags resulting from the operation (the flags are in addition to
     * the pre-existing flags).
     * @return Returns the smallest value that's greater than the given
     * value.Signals FlagInvalid and returns not-a-number (NaN) if the
     * parameter {@code ctx} is null, the precision is 0, or {@code ctx}
     * has an unlimited exponent range.
     */
    public EDecimal NextPlus(EContext ctx) {
      return GetMathValue(ctx).NextPlus(this, ctx);
    }

    /**
     * Finds the next value that is closer to the other object's value than this
     * object's value. Returns a copy of this value with the same sign as
     * the other value if both values are equal.
     * @param otherValue An arbitrary-precision decimal number that the return
     * value will approach.
     * @param ctx An arithmetic context object to control the precision and
     * exponent range of the result. The rounding mode from this context is
     * ignored. If {@code HasFlags} of the context is true, will also store
     * the flags resulting from the operation (the flags are in addition to
     * the pre-existing flags).
     * @return Returns the next value that is closer to the other object' s value
     * than this object's value. Signals FlagInvalid and returns NaN if the
     * parameter {@code ctx} is null, the precision is 0, or {@code ctx}
     * has an unlimited exponent range.
     */
    public EDecimal NextToward(
      EDecimal otherValue,
      EContext ctx) {
      return GetMathValue(ctx)
        .NextToward(this, otherValue, ctx);
    }

    /**
     * Rounds this object's value to a given precision, using the given rounding
     * mode and range of exponent, and also converts negative zero to
     * positive zero.
     * @param ctx A context for controlling the precision, rounding mode, and
     * exponent range. Can be null, in which case the precision is
     * unlimited and rounding isn't needed.
     * @return The closest value to this object's value, rounded to the specified
     * precision. Returns the same value as this object if {@code ctx} is
     * null or the precision and exponent range are unlimited.
     */
    public EDecimal Plus(EContext ctx) {
      return GetMathValue(ctx).Plus(this, ctx);
    }

    /**
     * Raises this object's value to the given exponent.
     * @param exponent An arbitrary-precision decimal number expressing the
     * exponent to raise this object's value to.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return This^exponent. Signals the flag FlagInvalid and returns NaN if this
     * object and exponent are both 0; or if this value is less than 0 and
     * the exponent either has a fractional part or is infinity. Signals
     * FlagInvalid and returns not-a-number (NaN) if the parameter {@code
     * ctx} is null or the precision is unlimited (the context's Precision
     * property is 0), and the exponent has a fractional part.
     */
    public EDecimal Pow(EDecimal exponent, EContext ctx) {
      return GetMathValue(ctx).Power(this, exponent, ctx);
    }

    /**
     * Raises this object's value to the given exponent, using unlimited precision.
     * @param exponent An arbitrary-precision decimal number expressing the
     * exponent to raise this object's value to.
     * @return This^exponent. Returns not-a-number (NaN) if the exponent has a
     * fractional part.
     */
    public EDecimal Pow(EDecimal exponent) {
      return this.Pow(exponent, null);
    }

    /**
     * Raises this object's value to the given exponent.
     * @param exponentSmall The exponent to raise this object's value to.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return This^exponent. Signals the flag FlagInvalid and returns NaN if this
     * object and exponent are both 0.
     */
    public EDecimal Pow(int exponentSmall, EContext ctx) {
      return this.Pow(EDecimal.FromInt64(exponentSmall), ctx);
    }

    /**
     * Raises this object's value to the given exponent.
     * @param exponentSmall The exponent to raise this object's value to.
     * @return This^exponent. Returns not-a-number (NaN) if this object and
     * exponent are both 0.
     */
    public EDecimal Pow(int exponentSmall) {
      return this.Pow(EDecimal.FromInt64(exponentSmall), null);
    }

    /**
     * Finds the number of digits in this number's significand. Returns 1 if this
     * value is 0, and 0 if this value is infinity or not-a-number (NaN).
     * @return An arbitrary-precision integer.
     */
    public EInteger Precision() {
      if (!this.isFinite()) {
        return EInteger.FromInt32(0);
      }
      return this.isZero() ? EInteger.FromInt32(1) :
        this.unsignedMantissa.ToEInteger().GetDigitCountAsEInteger();
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value but a new
     * exponent. <p>Note that this is not always the same as rounding to a
     * given number of decimal places, since it can fail if the difference
     * between this value's exponent and the desired exponent is too big,
     * depending on the maximum precision. If rounding to a number of
     * decimal places is desired, it's better to use the RoundToExponent
     * and RoundToIntegral methods instead.</p> <p><b>Remark:</b> This
     * method can be used to implement fixed-point decimal arithmetic, in
     * which each decimal number has a fixed number of digits after the
     * decimal point. The following code example returns a fixed-point
     * number with up to 20 digits before and exactly 5 digits after the
     * decimal point:</p> <pre> &#x2f;&#x2a; After performing arithmetic operations, adjust &#x2f;&#x2a; the number to 5&#x2a;&#x2f;&#x2a;&#x2f; &#x2f;&#x2a;&#x2a;&#x2f; digits after the decimal point number = number.Quantize(EInteger.FromInt32(-5), &#x2f;&#x2a; five digits after the decimal point&#x2a;&#x2f; EContext.ForPrecision(25) &#x2f;&#x2a; 25-digit precision);&#x2a;&#x2f;</pre> <p>A fixed-point decimal arithmetic in
     * which no digits come after the decimal point (a desired exponent of
     *  0) is considered an "integer arithmetic".</p>
     * @param desiredExponent The desired exponent for the result. The exponent is
     * the number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param ctx An arithmetic context to control precision and rounding of the
     * result. If {@code HasFlags} of the context is true, will also store
     * the flags resulting from the operation (the flags are in addition to
     * the pre-existing flags). Can be null, in which case the default
     * rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number with the same value as this
     * object but with the exponent changed. Signals FlagInvalid and
     * returns not-a-number (NaN) if this object is infinity, if the
     * rounded result can't fit the given precision, or if the context
     * defines an exponent range and the given exponent is outside that
     * range.
     */
    public EDecimal Quantize(
      EInteger desiredExponent,
      EContext ctx) {
      return this.Quantize(
          EDecimal.Create(EInteger.FromInt32(1), desiredExponent),
          ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * one but a new exponent. <p><b>Remark:</b> This method can be used to
     * implement fixed-point decimal arithmetic, in which a fixed number of
     * digits come after the decimal point. A fixed-point decimal
     * arithmetic in which no digits come after the decimal point (a
     *  desired exponent of 0) is considered an "integer arithmetic" .</p>
     * @param desiredExponentInt The desired exponent for the result. The exponent
     * is the number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param rounding A rounding mode to use in case the result needs to be
     * rounded to fit the given exponent.
     * @return An arbitrary-precision decimal number with the same value as this
     * object but with the exponent changed. Returns not-a-number (NaN) if
     * this object is infinity, or if the rounding mode is ERounding.None
     * and the result is not exact.
     */
    public EDecimal Quantize(
      int desiredExponentInt,
      ERounding rounding) {
      EDecimal ret = this.RoundToExponentFast(
          desiredExponentInt,
          rounding);
      if (ret != null) {
        return ret;
      }
      return this.Quantize(
          EDecimal.Create(EInteger.FromInt32(1), EInteger.FromInt32(desiredExponentInt)),
          EContext.ForRounding(rounding));
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value but a new
     * exponent. <p>Note that this is not always the same as rounding to a
     * given number of decimal places, since it can fail if the difference
     * between this value's exponent and the desired exponent is too big,
     * depending on the maximum precision. If rounding to a number of
     * decimal places is desired, it's better to use the RoundToExponent
     * and RoundToIntegral methods instead.</p> <p><b>Remark:</b> This
     * method can be used to implement fixed-point decimal arithmetic, in
     * which each decimal number has a fixed number of digits after the
     * decimal point. The following code example returns a fixed-point
     * number with up to 20 digits before and exactly 5 digits after the
     * decimal point:</p> <pre>/* After performing arithmetic operations, adjust the number to 5 digits after the decimal point &#x2a;&#x2f; number = number.Quantize(-5, /* five digits after the decimal point &#x2a;&#x2f;EContext.ForPrecision(25) /* 25-digit precision&#x2a;&#x2f;);</pre> <p>A
     * fixed-point decimal arithmetic in which no digits come after the
     *  decimal point (a desired exponent of 0) is considered an "integer
     *  arithmetic".</p>
     * @param desiredExponentInt The desired exponent for the result. The exponent
     * is the number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param ctx An arithmetic context to control precision and rounding of the
     * result. If {@code HasFlags} of the context is true, will also store
     * the flags resulting from the operation (the flags are in addition to
     * the pre-existing flags). Can be null, in which case the default
     * rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number with the same value as this
     * object but with the exponent changed. Signals FlagInvalid and
     * returns not-a-number (NaN) if this object is infinity, if the
     * rounded result can't fit the given precision, or if the context
     * defines an exponent range and the given exponent is outside that
     * range.
     */
    public EDecimal Quantize(
      int desiredExponentInt,
      EContext ctx) {
      if (ctx == null ||
        (!ctx.getHasExponentRange() && !ctx.getHasFlags() && ctx.getTraps() == 0 &&
          !ctx.getHasMaxPrecision() && !ctx.isSimplified())) {
        EDecimal ret = this.RoundToExponentFast(
            desiredExponentInt,
            ctx == null ? ERounding.HalfEven : ctx.getRounding());
        if (ret != null) {
          return ret;
        }
      }
      return this.Quantize(
          EDecimal.Create(EInteger.FromInt32(1), EInteger.FromInt32(desiredExponentInt)),
          ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but with the same exponent as another decimal number. <p>Note
     * that this is not always the same as rounding to a given number of
     * decimal places, since it can fail if the difference between this
     * value's exponent and the desired exponent is too big, depending on
     * the maximum precision. If rounding to a number of decimal places is
     * desired, it's better to use the RoundToExponent and RoundToIntegral
     * methods instead.</p> <p><b>Remark:</b> This method can be used to
     * implement fixed-point decimal arithmetic, in which a fixed number of
     * digits come after the decimal point. A fixed-point decimal
     * arithmetic in which no digits come after the decimal point (a
     *  desired exponent of 0) is considered an "integer arithmetic" .</p>
     * @param otherValue An arbitrary-precision decimal number containing the
     * desired exponent of the result. The significand is ignored. The
     * exponent is the number of fractional digits in the result, expressed
     * as a negative number. Can also be positive, which eliminates
     * lower-order places from the number. For example, -3 means round to
     * the thousandth (10^-3, 0.0001), and 3 means round to the
     * thousands-place (10^3, 1000). A value of 0 rounds the number to an
     * integer. The following examples for this parameter express a desired
     * exponent of 3: {@code 10e3}, {@code 8888e3}, {@code 4.56e5}.
     * @param ctx An arithmetic context to control precision and rounding of the
     * result. If {@code HasFlags} of the context is true, will also store
     * the flags resulting from the operation (the flags are in addition to
     * the pre-existing flags). Can be null, in which case the default
     * rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number with the same value as this
     * object but with the exponent changed. Signals FlagInvalid and
     * returns not-a-number (NaN) if the result can't fit the given
     * precision without rounding, or if the arithmetic context defines an
     * exponent range and the given exponent is outside that range.
     */
    public EDecimal Quantize(
      EDecimal otherValue,
      EContext ctx) {
      return GetMathValue(ctx).Quantize(this, otherValue, ctx);
    }

    /**
     * Returns an object with the same numerical value as this one but with
     * trailing zeros removed from its significand. For example, 1.00
     * becomes 1. <p>If this object's value is 0, changes the exponent to
     * 0.</p>
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and rounding isn't needed.
     * @return This value with trailing zeros removed. Note that if the result has
     * a very high exponent and the context says to clamp high exponents,
     * there may still be some trailing zeros in the significand.
     */
    public EDecimal Reduce(EContext ctx) {
      return GetMathValue(ctx).Reduce(this, ctx);
    }

    /**
     * Finds the remainder that results when dividing two arbitrary-precision
     * decimal numbers. The remainder is the value that remains when the
     * absolute value of this object is divided by the absolute value of
     * the other object; the remainder has the same sign (positive or
     * negative) as this object's value.
     * @param divisor The number to divide by.
     * @param ctx An arithmetic context object to control the precision, rounding,
     * and exponent range of the result, and of the intermediate integer
     * division. If {@code HasFlags} of the context is true, will also
     * store the flags resulting from the operation (the flags are in
     * addition to the pre-existing flags). Can be null, in which the
     * precision is unlimited.
     * @return The remainder of the two numbers. Signals FlagInvalid and returns
     * not-a-number (NaN) if the divisor is 0, or if the result doesn't fit
     * the given precision.
     */
    public EDecimal Remainder(
      EDecimal divisor,
      EContext ctx) {
      return GetMathValue(ctx).Remainder(this, divisor, ctx, true);
    }

    /**
     * Finds the remainder that results when dividing two arbitrary-precision
     * decimal numbers, except the intermediate division is not adjusted to
     * fit the precision of the given arithmetic context. The value of this
     * object is divided by the absolute value of the other object; the
     * remainder has the same sign (positive or negative) as this object's
     * value.
     * @param divisor The number to divide by.
     * @param ctx An arithmetic context object to control the precision, rounding,
     * and exponent range of the result, but not also of the intermediate
     * integer division. If {@code HasFlags} of the context is true, will
     * also store the flags resulting from the operation (the flags are in
     * addition to the pre-existing flags). Can be null, in which the
     * precision is unlimited.
     * @return The remainder of the two numbers. Signals FlagInvalid and returns
     * not-a-number (NaN) if the divisor is 0, or if the result doesn't fit
     * the given precision.
     */
    public EDecimal RemainderNoRoundAfterDivide(
      EDecimal divisor,
      EContext ctx) {
      return GetMathValue(ctx).Remainder(this, divisor, ctx, false);
    }

    /**
     * Calculates the remainder of a number by the formula <code>"this" - (("this" /
     *  "divisor") * "divisor")</code>.
     * @param divisor The number to divide by.
     * @return An arbitrary-precision decimal number.
     */
    public EDecimal RemainderNaturalScale(EDecimal divisor) {
      return this.RemainderNaturalScale(divisor, null);
    }

    /**
     * Calculates the remainder of a number by the formula "this" - (("this" /
     *  "divisor") * "divisor").
     * @param divisor The number to divide by.
     * @param ctx An arithmetic context object to control the precision, rounding,
     * and exponent range of the result. This context will be used only in
     * the division portion of the remainder calculation; as a result, it's
     * possible for the return value to have a higher precision than given
     * in this context. Flags will be set on the given context only if the
     * context's {@code HasFlags} is true and the integer part of the
     * division result doesn't fit the precision and exponent range without
     * rounding. Can be null, in which the precision is unlimited and no
     * additional rounding, other than the rounding down to an integer
     * after division, is needed.
     * @return An arbitrary-precision decimal number.
     */
    public EDecimal RemainderNaturalScale(
      EDecimal divisor,
      EContext ctx) {
      return this.Subtract(
        this.DivideToIntegerNaturalScale(divisor, null).Multiply(divisor, null),
        ctx);
    }

    /**
     * Finds the distance to the closest multiple of the given divisor, based on
     * the result of dividing this object's value by another object's
     * value. <ul> <li>If this and the other object divide evenly, the
     * result is 0.</li> <li>If the remainder's absolute value is less than
     * half of the divisor's absolute value, the result has the same sign
     * as this object and will be the distance to the closest
     * multiple.</li> <li>If the remainder's absolute value is more than
     * half of the divisor's absolute value, the result has the opposite
     * sign of this object and will be the distance to the closest
     * multiple.</li> <li>If the remainder's absolute value is exactly half
     * of the divisor's absolute value, the result has the opposite sign of
     * this object if the quotient, rounded down, is odd, and has the same
     * sign as this object if the quotient, rounded down, is even, and the
     * result's absolute value is half of the divisor's absolute
     *  value.</li></ul> This function is also known as the "IEEE Remainder"
     * function.
     * @param divisor The number to divide by.
     * @param ctx An arithmetic context object to control the precision. The
     * rounding and exponent range settings of this context are ignored
     * (the rounding mode is always treated as HalfEven). If {@code
     * HasFlags} of the context is true, will also store the flags
     * resulting from the operation (the flags are in addition to the
     * pre-existing flags). Can be null, in which the precision is
     * unlimited.
     * @return The distance of the closest multiple. Signals FlagInvalid and
     * returns not-a-number (NaN) if the divisor is 0, or either the result
     * of integer division (the quotient) or the remainder wouldn't fit the
     * given precision.
     */
    public EDecimal RemainderNear(
      EDecimal divisor,
      EContext ctx) {
      return GetMathValue(ctx)
        .RemainderNear(this, divisor, ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to a new exponent if necessary. The resulting
     * number's Exponent property will not necessarily be the given
     * exponent; use the Quantize method instead to give the result a
     * particular exponent.
     * @param exponent The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number rounded to the closest value
     * representable in the given precision. If the result can't fit the
     * precision, additional digits are discarded to make it fit. Signals
     * FlagInvalid and returns not-a-number (NaN) if the arithmetic context
     * defines an exponent range, the new exponent must be changed to the
     * given exponent when rounding, and the given exponent is outside of
     * the valid range of the arithmetic context.
     */
    public EDecimal RoundToExponent(
      EInteger exponent,
      EContext ctx) {
      return GetMathValue(ctx)
        .RoundToExponentSimple(this, exponent, ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to a new exponent if necessary, using the
     * HalfEven rounding mode. The resulting number's Exponent property
     * will not necessarily be the given exponent; use the Quantize method
     * instead to give the result a particular exponent.
     * @param exponent The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @return An arbitrary-precision decimal number rounded to the closest value
     * representable for the given exponent.
     */
    public EDecimal RoundToExponent(
      EInteger exponent) {
      return this.RoundToExponent(
          exponent,
          EContext.ForRounding(ERounding.HalfEven));
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to a new exponent if necessary, using the given
     * rounding mode. The resulting number's Exponent property will not
     * necessarily be the given exponent; use the Quantize method instead
     * to give the result a particular exponent.
     * @param exponent The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param rounding Desired mode for rounding this number's value.
     * @return An arbitrary-precision decimal number rounded to the closest value
     * representable for the given exponent.
     */
    public EDecimal RoundToExponent(
      EInteger exponent,
      ERounding rounding) {
      return this.RoundToExponent(
          exponent,
          EContext.ForRounding(rounding));
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to a new exponent if necessary, using the
     * HalfEven rounding mode. The resulting number's Exponent property
     * will not necessarily be the given exponent; use the Quantize method
     * instead to give the result a particular exponent.
     * @param exponentSmall The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @return An arbitrary-precision decimal number rounded to the closest value
     * representable for the given exponent.
     */
    public EDecimal RoundToExponent(
      int exponentSmall) {
      return this.RoundToExponent(exponentSmall, ERounding.HalfEven);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to a new exponent if necessary. The resulting
     * number's Exponent property will not necessarily be the given
     * exponent; use the Quantize method instead to give the result a
     * particular exponent.
     * @param exponentSmall The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number rounded to the closest value
     * representable in the given precision. If the result can't fit the
     * precision, additional digits are discarded to make it fit. Signals
     * FlagInvalid and returns not-a-number (NaN) if the arithmetic context
     * defines an exponent range, the new exponent must be changed to the
     * given exponent when rounding, and the given exponent is outside of
     * the valid range of the arithmetic context.
     */
    public EDecimal RoundToExponent(
      int exponentSmall,
      EContext ctx) {
      if (ctx == null ||
        (!ctx.getHasExponentRange() && !ctx.getHasFlags() && ctx.getTraps() == 0 &&
          !ctx.getHasMaxPrecision() && !ctx.isSimplified())) {
        EDecimal ret = this.RoundToExponentFast(
            exponentSmall,
            ctx == null ? ERounding.HalfEven : ctx.getRounding());
        if (ret != null) {
          return ret;
        }
      }
      return this.RoundToExponent(EInteger.FromInt32(exponentSmall), ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to a new exponent if necessary. The resulting
     * number's Exponent property will not necessarily be the given
     * exponent; use the Quantize method instead to give the result a
     * particular exponent.
     * @param exponentSmall The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param rounding The desired mode to use to round the given number to the
     * given exponent.
     * @return An arbitrary-precision decimal number rounded to the given negative
     * number of decimal places.
     */
    public EDecimal RoundToExponent(
      int exponentSmall,
      ERounding rounding) {
      EDecimal ret = this.RoundToExponentFast(
          exponentSmall,
          rounding);
      if (ret != null) {
        return ret;
      }
      return this.RoundToExponent(
          exponentSmall,
          EContext.ForRounding(rounding));
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to the given exponent represented as an
     * arbitrary-precision integer, and signals an inexact flag if the
     * result would be inexact. The resulting number's Exponent property
     * will not necessarily be the given exponent; use the Quantize method
     * instead to give the result a particular exponent.
     * @param exponent The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number rounded to the closest value
     * representable in the given precision. Signals FlagInvalid and
     * returns not-a-number (NaN) if the result can't fit the given
     * precision without rounding. Signals FlagInvalid and returns
     * not-a-number (NaN) if the arithmetic context defines an exponent
     * range, the new exponent must be changed to the given exponent when
     * rounding, and the given exponent is outside of the valid range of
     * the arithmetic context.
     */
    public EDecimal RoundToExponentExact(
      EInteger exponent,
      EContext ctx) {
      return GetMathValue(ctx)
        .RoundToExponentExact(this, exponent, ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to the given exponent represented as a 32-bit
     * signed integer, and signals an inexact flag if the result would be
     * inexact. The resulting number's Exponent property will not
     * necessarily be the given exponent; use the Quantize method instead
     * to give the result a particular exponent.
     * @param exponentSmall The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number rounded to the closest value
     * representable in the given precision. Signals FlagInvalid and
     * returns not-a-number (NaN) if the result can't fit the given
     * precision without rounding. Signals FlagInvalid and returns
     * not-a-number (NaN) if the arithmetic context defines an exponent
     * range, the new exponent must be changed to the given exponent when
     * rounding, and the given exponent is outside of the valid range of
     * the arithmetic context.
     */
    public EDecimal RoundToExponentExact(
      int exponentSmall,
      EContext ctx) {
      return this.RoundToExponentExact(EInteger.FromInt32(exponentSmall), ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to the given exponent represented as a 32-bit
     * signed integer, and signals an inexact flag if the result would be
     * inexact. The resulting number's Exponent property will not
     * necessarily be the given exponent; use the Quantize method instead
     * to give the result a particular exponent.
     * @param exponentSmall The minimum exponent the result can have. This is the
     * maximum number of fractional digits in the result, expressed as a
     * negative number. Can also be positive, which eliminates lower-order
     * places from the number. For example, -3 means round to the
     * thousandth (10^-3, 0.0001), and 3 means round to the thousand (10^3,
     * 1000). A value of 0 rounds the number to an integer.
     * @param rounding Desired mode for rounding this object's value.
     * @return An arbitrary-precision decimal number rounded to the closest value
     * representable using the given exponent.
     */
    public EDecimal RoundToExponentExact(
      int exponentSmall,
      ERounding rounding) {
      return this.RoundToExponentExact(
          EInteger.FromInt32(exponentSmall),
          EContext.Unlimited.WithRounding(rounding));
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to an integer, and signals an inexact flag if the
     * result would be inexact. The resulting number's Exponent property
     * will not necessarily be 0; use the Quantize method instead to give
     * the result an exponent of 0.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number rounded to the closest integer
     * representable in the given precision. Signals FlagInvalid and
     * returns not-a-number (NaN) if the result can't fit the given
     * precision without rounding. Signals FlagInvalid and returns
     * not-a-number (NaN) if the arithmetic context defines an exponent
     * range, the new exponent must be changed to 0 when rounding, and 0 is
     * outside of the valid range of the arithmetic context.
     */
    public EDecimal RoundToIntegerExact(EContext ctx) {
      return GetMathValue(ctx).RoundToExponentExact(this, EInteger.FromInt32(0), ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to an integer, without adding the
     * <code>FlagInexact</code> or <code>FlagRounded</code> flags. The resulting
     * number's Exponent property will not necessarily be 0; use the
     * Quantize method instead to give the result an exponent of 0.
     * @param ctx An arithmetic context to control precision and rounding of the
     * result. If {@code HasFlags} of the context is true, will also store
     * the flags resulting from the operation (the flags are in addition to
     * the pre-existing flags), except that this function will never add
     * the {@code FlagRounded} and {@code FlagInexact} flags (the only
     * difference between this and RoundToExponentExact). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number rounded to the closest integer
     * representable in the given precision. If the result can't fit the
     * precision, additional digits are discarded to make it fit. Signals
     * FlagInvalid and returns not-a-number (NaN) if the arithmetic context
     * defines an exponent range, the new exponent must be changed to 0
     * when rounding, and 0 is outside of the valid range of the arithmetic
     * context.
     */
    public EDecimal RoundToIntegerNoRoundedFlag(EContext ctx) {
      return GetMathValue(ctx)
        .RoundToExponentNoRoundedFlag(this, EInteger.FromInt32(0), ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to an integer, and signals an inexact flag if the
     * result would be inexact.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number rounded to the closest integer
     * representable in the given precision. Signals FlagInvalid and
     * returns not-a-number (NaN) if the result can't fit the given
     * precision without rounding. Signals FlagInvalid and returns
     * not-a-number (NaN) if the arithmetic context defines an exponent
     * range, the new exponent must be changed to 0 when rounding, and 0 is
     * outside of the valid range of the arithmetic context.
     * @deprecated Renamed to RoundToIntegerExact.
 */
@Deprecated
    public EDecimal RoundToIntegralExact(EContext ctx) {
      return GetMathValue(ctx).RoundToExponentExact(this, EInteger.FromInt32(0), ctx);
    }

    /**
     * Returns an arbitrary-precision decimal number with the same value as this
     * object but rounded to an integer, without adding the
     * <code>FlagInexact</code> or <code>FlagRounded</code> flags.
     * @param ctx An arithmetic context to control precision and rounding of the
     * result. If {@code HasFlags} of the context is true, will also store
     * the flags resulting from the operation (the flags are in addition to
     * the pre-existing flags), except that this function will never add
     * the {@code FlagRounded} and {@code FlagInexact} flags (the only
     * difference between this and RoundToExponentExact). Can be null, in
     * which case the default rounding mode is HalfEven.
     * @return An arbitrary-precision decimal number rounded to the closest integer
     * representable in the given precision. If the result can't fit the
     * precision, additional digits are discarded to make it fit. Signals
     * FlagInvalid and returns not-a-number (NaN) if the arithmetic context
     * defines an exponent range, the new exponent must be changed to 0
     * when rounding, and 0 is outside of the valid range of the arithmetic
     * context.
     * @deprecated Renamed to RoundToIntegerNoRoundedFlag.
 */
@Deprecated
    public EDecimal RoundToIntegralNoRoundedFlag(EContext ctx) {
      return GetMathValue(ctx)
        .RoundToExponentNoRoundedFlag(this, EInteger.FromInt32(0), ctx);
    }

    /**
     * Rounds this object's value to a given precision, using the given rounding
     * mode and range of exponent.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and no rounding is needed.
     * @return The closest value to this object's value, rounded to the specified
     * precision. Returns the same value as this object if {@code ctx} is
     * null or the precision and exponent range are unlimited.
     */
    public EDecimal RoundToPrecision(EContext ctx) {
      return GetMathValue(ctx).RoundToPrecision(this, ctx);
    }

    /**
     * Returns a number similar to this number but with the scale adjusted.
     * @param places The power of 10 to scale by.
     * @return An arbitrary-precision decimal number.
     */
    public EDecimal ScaleByPowerOfTen(int places) {
      return this.ScaleByPowerOfTen(EInteger.FromInt32(places), null);
    }

    /**
     * Returns a number similar to this number but with the scale adjusted.
     * @param places The power of 10 to scale by.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and no rounding is needed.
     * @return An arbitrary-precision decimal number.
     */
    public EDecimal ScaleByPowerOfTen(int places, EContext ctx) {
      return this.ScaleByPowerOfTen(EInteger.FromInt32(places), ctx);
    }

    /**
     * Returns a number similar to this number but with the scale adjusted.
     * @param bigPlaces The power of 10 to scale by.
     * @return An arbitrary-precision decimal number.
     */
    public EDecimal ScaleByPowerOfTen(EInteger bigPlaces) {
      return this.ScaleByPowerOfTen(bigPlaces, null);
    }

    /**
     * Returns a number similar to this number but with its scale adjusted.
     * @param bigPlaces The power of 10 to scale by.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and no rounding is needed.
     * @return A number whose exponent is increased by {@code bigPlaces}.
     * @throws NullPointerException The parameter {@code bigPlaces} is null.
     */
    public EDecimal ScaleByPowerOfTen(
      EInteger bigPlaces,
      EContext ctx) {
      if (bigPlaces == null) {
        throw new NullPointerException("bigPlaces");
      }
      if (bigPlaces.isZero()) {
        return this.RoundToPrecision(ctx);
      }
      if (!this.isFinite()) {
        return this.RoundToPrecision(ctx);
      }
      EInteger bigExp = this.getExponent();
      bigExp = bigExp.Add(bigPlaces);
      return CreateWithFlags(
          this.unsignedMantissa,
          FastIntegerFixed.FromBig(bigExp),
          this.flags).RoundToPrecision(ctx);
    }

    /**
     * Finds the square root of this object's value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). <i>This parameter
     * can't be null, as the square root function's results are generally
     * not exact for many inputs.</i> (Unlike in the General Decimal
     * Arithmetic Specification, any rounding mode is allowed.).
     * @return The square root. Signals the flag FlagInvalid and returns NaN if
     * this object is less than 0 (the square root would be a complex
     * number, but the return value is still NaN). Signals FlagInvalid and
     * returns not-a-number (NaN) if the parameter {@code ctx} is null or
     * the precision is unlimited (the context's Precision property is 0).
     */
    public EDecimal Sqrt(EContext ctx) {
      return GetMathValue(ctx).SquareRoot(this, ctx);
    }

    /**
     * Finds the square root of this object's value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). <i>This parameter
     * can't be null, as the square root function's results are generally
     * not exact for many inputs.</i> (Unlike in the General Decimal
     * Arithmetic Specification, any rounding mode is allowed.).
     * @return The square root. Signals the flag FlagInvalid and returns NaN if
     * this object is less than 0 (the square root would be a complex
     * number, but the return value is still NaN). Signals FlagInvalid and
     * returns not-a-number (NaN) if the parameter {@code ctx} is null or
     * the precision is unlimited (the context's Precision property is 0).
     * @deprecated Renamed to Sqrt.
 */
@Deprecated
    public EDecimal SquareRoot(EContext ctx) {
      return GetMathValue(ctx).SquareRoot(this, ctx);
    }

    /**
     * Subtracts an arbitrary-precision decimal number from this instance and
     * returns the result.
     * @param otherValue The number to subtract from this instance's value.
     * @return The difference of the two objects.
     */
    public EDecimal Subtract(EDecimal otherValue) {
      return this.Subtract(otherValue, EContext.UnlimitedHalfEven);
    }

    /**
     * Subtracts an arbitrary-precision decimal number from this instance.
     * @param otherValue The number to subtract from this instance's value.
     * @param ctx An arithmetic context to control the precision, rounding, and
     * exponent range of the result. If {@code HasFlags} of the context is
     * true, will also store the flags resulting from the operation (the
     * flags are in addition to the pre-existing flags). Can be null, in
     * which case the precision is unlimited and no rounding is needed.
     * @return The difference of the two objects.
     * @throws NullPointerException The parameter {@code otherValue} is null.
     */
    public EDecimal Subtract(
      EDecimal otherValue,
      EContext ctx) {
      if (otherValue == null) {
        throw new NullPointerException("otherValue");
      }
      EDecimal negated = otherValue;
      if ((otherValue.flags & BigNumberFlags.FlagNaN) == 0) {
        int newflags = otherValue.flags ^ BigNumberFlags.FlagNegative;
        negated = CreateWithFlags(
            otherValue.unsignedMantissa,
            otherValue.exponent,
            newflags);
      }
      return this.Add(negated, ctx);
    }

    private static final double[] ExactDoublePowersOfTen = {
      1, 10, 100, 1000, 10000,
      1e5, 1e6, 1e7, 1e8, 1e9,
      1e10, 1e11, 1e12, 1e13, 1e14,
      1e15, 1e16, 1e17, 1e18, 1e19,
      1e20, 1e21, 1e22,
    };

    private static final float[] ExactSinglePowersOfTen = {
      1f, 10f, 100f, 1000f, 10000f,
      1e5f, 1e6f, 1e7f, 1e8f, 1e9f, 1e10f,
    };

    /**
     * Converts this value to its closest equivalent as a 64-bit floating-point
     * number. The half-even rounding mode is used. <p>If this value is a
     * NaN, sets the high bit of the 64-bit floating point number's
     * significand area for a quiet NaN, and clears it for a signaling NaN.
     * Then the other bits of the significand area are set to the lowest
     * bits of this object's unsigned significand, and the next-highest bit
     * of the significand area is set if those bits are all zeros and this
     * is a signaling NaN. Unfortunately, in the.NET implementation, the
     * return value of this method may be a quiet NaN even if a signaling
     * NaN would otherwise be generated.</p>
     * @return The closest 64-bit floating-point number to this value. The return
     * value can be positive infinity or negative infinity if this value
     * exceeds the range of a 64-bit floating point number.
     */
    public double ToDouble() {
      if (this.IsPositiveInfinity()) {
        return Double.POSITIVE_INFINITY;
      }
      if (this.IsNegativeInfinity()) {
        return Double.NEGATIVE_INFINITY;
      }
      if (this.isNegative() && this.isZero()) {
        int highbit = ((int)(1 << 31));
        return Extras.IntegersToDouble(new int[] { 0, highbit,
        });
      }
      if (this.isZero()) {
        return 0.0;
      }
      if (this.isFinite()) {
        if (this.exponent.CompareToInt(309) > 0) {
          // Very high exponent, treat as infinity
          return this.isNegative() ? Double.NEGATIVE_INFINITY :
            Double.POSITIVE_INFINITY;
        }
        if (this.exponent.CompareToInt(-22) >= 0 &&
          this.exponent.CompareToInt(44) <= 0 &&
          this.unsignedMantissa.CanFitInInt64()) {
          // Fast-path optimization (explained on exploringbinary.com)
          long ml = this.unsignedMantissa.AsInt64();
          int iexp = this.exponent.AsInt32();
          while (ml <= 900719925474099L && iexp > 22) {
            ml *= 10;
            --iexp;
          }
          int iabsexp = Math.abs(iexp);
          if (ml < 9007199254740992L && iabsexp <= 22) {
            double d = ExactDoublePowersOfTen[iabsexp];
            double dml = this.isNegative() ? (double)(-ml) : (double)ml;
            // NOTE: This method assumes the division given below
            // is rounded as necessary by the round-to-nearest
            // (ties to even) mode by the runtime
            if (iexp < 0) {
              return dml / d;
            } else {
              return dml * d;
            }
          }
        }
        EInteger adjExp = GetAdjustedExponent(this);
        if (adjExp.compareTo(EInteger.FromInt64(-326)) < 0) {
          // Very low exponent, treat as 0
          return this.isNegative() ? Extras.IntegersToDouble(new int[] { 0,
            ((int)(1 << 31)),
          }) : 0.0;
        }
        if (adjExp.compareTo(EInteger.FromInt64(309)) > 0) {
          // Very high exponent, treat as infinity
          return this.isNegative() ? Double.NEGATIVE_INFINITY :
            Double.POSITIVE_INFINITY;
        }
      }
      return this.ToEFloat(EContext.Binary64).ToDouble();
    }

    /**
     * Converts this value to an arbitrary-precision integer. Any fractional part
     * in this value will be discarded when converting to an
     * arbitrary-precision integer.
     * @return An arbitrary-precision integer.
     * @throws ArithmeticException This object's value is infinity or not-a-number
     * (NaN).
     */
    public EInteger ToEInteger() {
      return this.ToEIntegerInternal(false);
    }

    /**
     * Converts this value to an arbitrary-precision integer, checking whether the
     * fractional part of the value would be lost.
     * @return An arbitrary-precision integer.
     * @throws ArithmeticException This object's value is infinity or not-a-number
     * (NaN).
     * @deprecated Renamed to ToEIntegerIfExact.
 */
@Deprecated
    public EInteger ToEIntegerExact() {
      return this.ToEIntegerInternal(true);
    }

    /**
     * Converts this value to an arbitrary-precision integer, checking whether the
     * fractional part of the value would be lost.
     * @return An arbitrary-precision integer.
     * @throws ArithmeticException This object's value is infinity or not-a-number
     * (NaN).
     */
    public EInteger ToEIntegerIfExact() {
      return this.ToEIntegerInternal(true);
    }

    /**
     * Same as toString(), except that when an exponent is used it will be a
     * multiple of 3.
     * @return A text string.
     */
    public String ToEngineeringString() {
      return this.ToStringInternal(1);
    }

    /**
     * Creates a binary floating-point number from this object's value. Note that
     * if the binary floating-point number contains a negative exponent,
     * the resulting value might not be exact, in which case the resulting
     * binary floating-point number will be an approximation of this
     * decimal number's value.
     * @return An arbitrary-precision binary floating-point number.
     * @deprecated Renamed to ToEFloat.
 */
@Deprecated
    public EFloat ToExtendedFloat() {
      return this.ToEFloat(EContext.UnlimitedHalfEven);
    }

    /**
     * Creates a binary floating-point number from this object's value. Note that
     * if the binary floating-point number contains a negative exponent,
     * the resulting value might not be exact, in which case the resulting
     * binary floating-point number will be an approximation of this
     * decimal number's value.
     * @return An arbitrary-precision binary floating-point number.
     */
    public EFloat ToEFloat() {
      return this.ToEFloat(EContext.UnlimitedHalfEven);
    }

    /**
     * Converts this value to a string, but without using exponential notation.
     * @return A text string.
     */
    public String ToPlainString() {
      return this.ToStringInternal(2);
    }

    /**
     * Converts this value to its closest equivalent as a 32-bit floating-point
     * number. The half-even rounding mode is used. <p>If this value is a
     * NaN, sets the high bit of the 32-bit floating point number's
     * significand area for a quiet NaN, and clears it for a signaling NaN.
     * Then the other bits of the significand area are set to the lowest
     * bits of this object's unsigned significand, and the next-highest bit
     * of the significand area is set if those bits are all zeros and this
     * is a signaling NaN. Unfortunately, in the.NET implementation, the
     * return value of this method may be a quiet NaN even if a signaling
     * NaN would otherwise be generated.</p>
     * @return The closest 32-bit binary floating-point number to this value. The
     * return value can be positive infinity or negative infinity if this
     * value exceeds the range of a 32-bit floating point number.
     */
    public float ToSingle() {
      if (this.IsPositiveInfinity()) {
        return Float.POSITIVE_INFINITY;
      }
      if (this.IsNegativeInfinity()) {
        return Float.NEGATIVE_INFINITY;
      }
      if (this.isNegative() && this.isZero()) {
        return Float.intBitsToFloat(1 << 31);
      }
      if (this.isZero()) {
        return 0.0f;
      }
      if (this.isFinite()) {
        if (this.exponent.CompareToInt(-10) >= 0 &&
          this.exponent.CompareToInt(20) <= 0 &&
          this.unsignedMantissa.CanFitInInt32()) {
          // Fast-path optimization (version for 'double's explained
          // on exploringbinary.com)
          int iml = this.unsignedMantissa.AsInt32();
          int iexp = this.exponent.AsInt32();
          // DebugUtility.Log("this=" + (this.toString()));
          // DebugUtility.Log("iml=" + iml + " iexp=" + iexp + " neg=" +
          // (this.isNegative()));
          while (iml <= 1677721 && iexp > 10) {
            iml *= 10;
            --iexp;
          }
          int iabsexp = Math.abs(iexp);
          // DebugUtility.Log("--> iml=" + iml + " absexp=" + iabsexp);
          if (iml < 16777216 && iabsexp <= 10) {
            float fd = ExactSinglePowersOfTen[iabsexp];
            float fml = this.isNegative() ? (float)(-iml) : (float)iml;
            // NOTE: This method assumes the division given below
            // is rounded as necessary by the round-to-nearest
            // (ties to even) mode by the runtime
            if (iexp < 0) {
              return fml / fd;
            } else {
              return fml * fd;
            }
          }
        }
        EInteger adjExp = GetAdjustedExponent(this);
        if (adjExp.compareTo(-47) < 0) {
          // Very low exponent, treat as 0
          return this.isNegative() ?
            Float.intBitsToFloat(1 << 31) :
            0.0f;
        }
        if (adjExp.compareTo(39) > 0) {
          // Very high exponent, treat as infinity
          return this.isNegative() ? Float.NEGATIVE_INFINITY :
            Float.POSITIVE_INFINITY;
        }
      }
      return this.ToEFloat(EContext.Binary32).ToSingle();
    }

    /**
     * Converts this value to a string. Returns a value compatible with this
     * class's FromString method.
     * @return A string representation of this object. The text string will be in
     * exponential notation if the exponent is greater than 0 or if the
     * number's first nonzero digit is more than five digits after the
     * decimal point.
     */
    @Override public String toString() {
      return this.ToStringInternal(0);
    }

    /**
     * Returns the unit in the last place. The significand will be 1 and the
     * exponent will be this number's exponent. Returns 1 with an exponent
     * of 0 if this number is infinity or not-a-number (NaN).
     * @return An arbitrary-precision decimal number.
     */
    public EDecimal Ulp() {
      return (!this.isFinite()) ? EDecimal.One :
        EDecimal.Create(EInteger.FromInt32(1), this.getExponent());
    }

    static EDecimal CreateWithFlags(
      FastIntegerFixed mantissa,
      FastIntegerFixed exponent,
      int flags) {
      if (mantissa == null) {
        throw new NullPointerException("mantissa");
      }
      if (exponent == null) {
        throw new NullPointerException("exponent");
      }

      return new EDecimal(
          mantissa,
          exponent,
          flags);
    }

    static EDecimal CreateWithFlags(
      EInteger mantissa,
      EInteger exponent,
      int flags) {
      if (mantissa == null) {
        throw new NullPointerException("mantissa");
      }
      if (exponent == null) {
        throw new NullPointerException("exponent");
      }

      return new EDecimal(
          FastIntegerFixed.FromBig(mantissa),
          FastIntegerFixed.FromBig(exponent),
          flags);
    }

    private static boolean AppendString(
      StringBuilder builder,
      char c,
      FastInteger count) {
      if (count.CompareToInt(Integer.MAX_VALUE) > 0 || count.signum() < 0) {
        throw new UnsupportedOperationException();
      }
      int icount = count.AsInt32();
      for (int i = icount - 1; i >= 0; --i) {
        builder.append(c);
      }
      return true;
    }

    private static IRadixMath<EDecimal> GetMathValue(EContext ctx) {
      if (ctx == null || ctx == EContext.UnlimitedHalfEven) {
        return ExtendedMathValue;
      }
      return (!ctx.isSimplified() && ctx.getTraps() == 0) ? ExtendedMathValue :
        MathValue;
    }

    private boolean EqualsInternal(EDecimal otherValue) {
      return (otherValue != null) && (this.flags == otherValue.flags &&
          this.unsignedMantissa.equals(otherValue.unsignedMantissa) &&
          this.exponent.equals(otherValue.exponent));
    }

    private static EInteger GetAdjustedExponent(EDecimal ed) {
      if (!ed.isFinite()) {
        return EInteger.FromInt32(0);
      }
      if (ed.isZero()) {
        return EInteger.FromInt32(0);
      }
      EInteger retEInt = ed.getExponent();
      EInteger valueEiPrecision =
        ed.getUnsignedMantissa().GetDigitCountAsEInteger();
      retEInt = retEInt.Add(valueEiPrecision.Subtract(1));
      return retEInt;
    }

    private static EInteger GetAdjustedExponentBinary(EFloat ef) {
      if (!ef.isFinite()) {
        return EInteger.FromInt32(0);
      }
      if (ef.isZero()) {
        return EInteger.FromInt32(0);
      }
      EInteger retEInt = ef.getExponent();
      EInteger valueEiPrecision =
        ef.getUnsignedMantissa().GetSignedBitLengthAsEInteger();
      retEInt = retEInt.Add(valueEiPrecision.Subtract(1));
      return retEInt;
    }

    private EDecimal RoundToExponentFast(
      int exponentSmall,
      ERounding rounding) {
      if (this.isFinite() && this.exponent.CanFitInInt32() &&
        this.unsignedMantissa.CanFitInInt32()) {
        int thisExponentSmall = this.exponent.AsInt32();
        if (thisExponentSmall == exponentSmall) {
          return this;
        }
        int thisMantissaSmall = this.unsignedMantissa.AsInt32();
        if (thisExponentSmall >= -100 && thisExponentSmall <= 100 &&
          exponentSmall >= -100 && exponentSmall <= 100) {
          if (rounding == ERounding.Down) {
            int diff = exponentSmall - thisExponentSmall;
            if (diff >= 1 && diff <= 9) {
              thisMantissaSmall /= ValueTenPowers[diff];
              return new EDecimal(
                  new FastIntegerFixed(thisMantissaSmall),
                  new FastIntegerFixed(exponentSmall),
                  this.flags);
            }
          } else if (rounding == ERounding.HalfEven &&
            thisMantissaSmall != Integer.MAX_VALUE) {
            int diff = exponentSmall - thisExponentSmall;
            if (diff >= 1 && diff <= 9) {
              int pwr = ValueTenPowers[diff - 1];
              int div = thisMantissaSmall / pwr;
              int div2 = (div > 43698) ? (div / 10) : ((div * 26215) >> 18);
              int rem = div - (div2 * 10);
              if (rem > 5) {
                ++div2;
              } else if (rem == 5 && (thisMantissaSmall - (div * pwr)) != 0) {
                ++div2;
              } else if (rem == 5 && (div2 & 1) == 1) {
                ++div2;
              }
              return new EDecimal(
                  new FastIntegerFixed(div2),
                  new FastIntegerFixed(exponentSmall),
                  this.flags);
            }
          }
        }
      }
      return null;
    }

    private boolean IsIntegerPartZero() {
      if (!this.isFinite()) {
        return false;
      }
      if (this.unsignedMantissa.isValueZero()) {
        return true;
      }
      int sign = this.getExponent().signum();
      if (sign >= 0) {
        return false;
      } else {
        EInteger bigexponent = this.getExponent();
        EInteger digitCount = this.getUnsignedMantissa()
          .GetDigitCountAsEInteger();
        return (digitCount.compareTo(bigexponent) <= 0) ? true :
          false;
      }
    }

    private EInteger ToEIntegerInternal(boolean exact) {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      int sign = this.getExponent().signum();
      if (this.isZero()) {
        return EInteger.FromInt32(0);
      }
      if (sign == 0) {
        EInteger bigmantissa = this.getMantissa();
        return bigmantissa;
      }
      if (sign > 0) {
        EInteger bigmantissa = this.getMantissa();
        EInteger bigexponent =
          NumberUtility.FindPowerOfTenFromBig(this.getExponent());
        bigmantissa = bigmantissa.Multiply(bigexponent);
        return bigmantissa;
      } else {
        if (exact && !this.unsignedMantissa.isEvenNumber()) {
          // Mantissa is odd and will have to shift a nonzero
          // number of digits, so can't be an exact integer
          throw new ArithmeticException("Not an exact integer");
        }
        FastInteger bigexponent = this.exponent.ToFastInteger().Negate();
        EInteger bigmantissa = this.unsignedMantissa.ToEInteger();
        DigitShiftAccumulator acc = new DigitShiftAccumulator(bigmantissa, 0, 0);
        acc.TruncateOrShiftRight(
          bigexponent,
          true);
        if (exact && (acc.getLastDiscardedDigit() != 0 || acc.getOlderDiscardedDigits() !=
            0)) {
          // Some digits were discarded
          throw new ArithmeticException("Not an exact integer");
        }
        bigmantissa = acc.getShiftedInt();
        if (this.isNegative()) {
          bigmantissa = bigmantissa.Negate();
        }
        return bigmantissa;
      }
    }

    private static boolean HasTerminatingBinaryExpansion(EInteger
      den) {
      if (den.isZero()) {
        return false;
      }
      if (den.GetUnsignedBit(0) && den.compareTo(EInteger.FromInt32(1)) != 0) {
        return false;
      }
      // NOTE: Equivalent to (den >> lowBit(den)) == 1
      return den.GetUnsignedBitLengthAsEInteger()
        .equals(den.GetLowBitAsEInteger().Add(1));
    }

    private EFloat WithThisSign(EFloat ef) {
      return this.isNegative() ? ef.Negate() : ef;
    }

    private static EInteger DigitCountUpperBound(EInteger ei) {
      EInteger bi = ei.GetUnsignedBitLengthAsEInteger();
      if (bi.compareTo(33) < 0) {
        // Can easily be calculated without estimation
        return ei.GetDigitCountAsEInteger();
      } else if (bi.compareTo(2135) <= 0) {
        // May overestimate by 1
        return EInteger.FromInt32(1 + ((bi.ToInt32Checked() *
                631305) >> 21));
      } else {
        // Bit length is big enough that dividing it by 3 will not
        // underestimate the true base-10 digit length.
        return bi.Divide(3);
      }
    }

    private static EInteger DigitCountLowerBound(EInteger ei) {
      EInteger bi = ei.GetUnsignedBitLengthAsEInteger();
      if (bi.compareTo(33) < 0) {
        // Can easily be calculated without estimation
        return ei.GetDigitCountAsEInteger();
      } else if (bi.compareTo(2135) <= 0) {
        int ov = 1 + ((bi.ToInt32Checked() * 631305) >> 21);
        return EInteger.FromInt32(ov - 2);
      } else {
        // Bit length is big enough that multiplying it by 100 and dividing by 335
        // will not
        // overestimate the true base-10 digit length.
        return bi.Multiply(100).Divide(335);
      }
    }

    /**
     * Creates a binary floating-point number from this object's value. Note that
     * if the binary floating-point number contains a negative exponent,
     * the resulting value might not be exact, in which case the resulting
     * binary floating-point number will be an approximation of this
     * decimal number's value.
     * @param ec An arithmetic context to control the precision, rounding, and
     * exponent range of the result. Can be null.
     * @return An arbitrary-precision float floating-point number.
     * @throws NullPointerException The parameter {@code ec} is null.
     */
    public EFloat ToEFloat(EContext ec) {
      EInteger bigintExp = this.getExponent();
      EInteger bigintMant = this.getUnsignedMantissa();
      if (this.IsNaN()) {
        return EFloat.CreateNaN(
            this.getUnsignedMantissa(),
            this.IsSignalingNaN(),
            this.isNegative(),
            ec);
      }
      if (this.IsPositiveInfinity()) {
        return EFloat.PositiveInfinity.RoundToPrecision(ec);
      }
      if (this.IsNegativeInfinity()) {
        return EFloat.NegativeInfinity.RoundToPrecision(ec);
      }
      if (bigintMant.isZero()) {
        return this.isNegative() ? EFloat.NegativeZero.RoundToPrecision(ec) :
          EFloat.Zero.RoundToPrecision(ec);
      }
      if (bigintExp.isZero()) {
        // Integer
        // DebugUtility.Log("Integer");
        return this.WithThisSign(EFloat.FromEInteger(bigintMant))
          .RoundToPrecision(ec);
      }
      EContext b64 = EContext.Binary64;
      if (ec != null && ec.getHasMaxPrecision() && ec.getHasExponentRange() &&
        !ec.isSimplified() && ec.getEMax().compareTo(b64.getEMax()) <= 0 &&
        ec.getEMin().compareTo(b64.getEMin()) >= 0 &&
        ec.getPrecision().compareTo(b64.getPrecision()) <= 0) {
        // Quick check for overflow or underflow
        EInteger adjexpLowerBound = bigintExp;
        EInteger adjexpUpperBound = bigintExp.Add(
            DigitCountUpperBound(bigintMant.Abs()).Subtract(1));
        if (adjexpUpperBound.compareTo(-326) < 0) {
          // Underflow to zero
          EInteger eTiny = ec.getEMin().Subtract(ec.getPrecision().Subtract(1));
          eTiny = eTiny.Subtract(1); // subtract 1 from proper eTiny to
          // trigger underflow
          EFloat ret = EFloat.Create(EInteger.FromInt32(
                this.isNegative() ? -1 : 1),
              eTiny);
          return ret.RoundToPrecision(ec);
        } else if (adjexpLowerBound.compareTo(309) > 0) {
          return EFloat.GetMathValue().SignalOverflow(ec, this.isNegative());
        }
        EInteger digitsLowerBound = DigitCountLowerBound(bigintMant.Abs());
        if (digitsLowerBound.compareTo(800) > 0) {
          String estr = this.toString();
          return EFloat.FromString(estr, ec);
        }
      }
      if (bigintExp.signum() > 0) {
        // Scaled integer
        // --- Optimizations for Binary32 and Binary64
        if (ec == EContext.Binary32) {
          if (bigintExp.compareTo(39) > 0) {
            return this.isNegative() ? EFloat.NegativeInfinity :
              EFloat.PositiveInfinity;
          }
        } else if (ec == EContext.Binary64) {
          if (bigintExp.compareTo(309) > 0) {
            return this.isNegative() ? EFloat.NegativeInfinity :
              EFloat.PositiveInfinity;
          }
        }
        // --- End optimizations for Binary32 and Binary64
        // DebugUtility.Log("Scaled integer");
        EInteger bigmantissa = bigintMant;
        bigintExp = NumberUtility.FindPowerOfTenFromBig(bigintExp);
        bigmantissa = bigmantissa.Multiply(bigintExp);
        return this.WithThisSign(EFloat.FromEInteger(bigmantissa))
          .RoundToPrecision(ec);
        } else {
        // Fractional number
        // DebugUtility.Log("Fractional");
        EInteger scale = bigintExp;
        EInteger bigmantissa = bigintMant;
        boolean neg = bigmantissa.signum() < 0;
        if (neg) {
          bigmantissa=(bigmantissa).Negate();
        }
        EInteger negscale = scale.Negate();
        // DebugUtility.Log("scale=" + scale + " mantissaPrecision=" +
        // bigmantissa.GetDigitCountAsEInteger());
        EInteger divisor = NumberUtility.FindPowerOfTenFromBig(negscale);
        EInteger desiredHigh;
        EInteger desiredLow;
        boolean haveCopy = false;
        ec = (ec == null) ? (EContext.UnlimitedHalfEven) : ec;
        EContext originalEc = ec;
        if (!ec.getHasMaxPrecision()) {
          EInteger num = bigmantissa;
          EInteger den = divisor;
          EInteger gcd = num.Gcd(den);
          if (gcd.compareTo(EInteger.FromInt32(1)) != 0) {
            den = den.Divide(gcd);
          }
          // DebugUtility.Log("num=" + (num.Divide(gcd)));
          // DebugUtility.Log("den=" + den);
          if (!HasTerminatingBinaryExpansion(den)) {
            // DebugUtility.Log("Approximate");
            // DebugUtility.Log("=>{0}\r\n->{1}", bigmantissa, divisor);
            ec = ec.WithPrecision(53).WithBlankFlags();
            haveCopy = true;
          } else {
            bigmantissa = bigmantissa.Divide(gcd);
            divisor = den;
          }
        }
        // NOTE: Precision raised by 2 to accommodate rounding
        // to odd
        EInteger valueEcPrec = ec.getHasMaxPrecision() ? ec.getPrecision().Add(2) :
          ec.getPrecision();
        desiredHigh = EInteger.FromInt32(1).ShiftLeft(valueEcPrec);
        desiredLow = EInteger.FromInt32(1).ShiftLeft(valueEcPrec.Subtract(1));
        // DebugUtility.Log("=>{0}\r\n->{1}", bigmantissa, divisor);
        EInteger[] quorem = ec.getHasMaxPrecision() ?
          bigmantissa.DivRem(divisor) : null;
        // DebugUtility.Log("=>{0}\r\n->{1}", quorem[0], desiredHigh);
        FastInteger adjust = new FastInteger(0);
        if (!ec.getHasMaxPrecision()) {
          EInteger eterm = divisor.GetLowBitAsEInteger();
          bigmantissa = bigmantissa.ShiftLeft(eterm);
          adjust.SubtractBig(eterm);
          quorem = bigmantissa.DivRem(divisor);
        } else if (quorem[0].compareTo(desiredHigh) >= 0) {
          do {
            boolean optimized = false;
            if (ec.getClampNormalExponents() && valueEcPrec.signum() > 0) {
              EInteger valueBmBits =
                bigmantissa.GetUnsignedBitLengthAsEInteger();
              EInteger divBits = divisor.GetUnsignedBitLengthAsEInteger();
              if (divisor.compareTo(bigmantissa) < 0) {
                if (divBits.compareTo(valueBmBits) < 0) {
                  EInteger bitdiff = valueBmBits.Subtract(divBits);
                  if (bitdiff.compareTo(valueEcPrec.Add(1)) > 0) {
                    bitdiff = bitdiff.Subtract(valueEcPrec).Subtract(1);
                    divisor = divisor.ShiftLeft(bitdiff);
                    adjust.AddBig(bitdiff);
                    optimized = true;
                  }
                }
              } else {
                if (valueBmBits.compareTo(divBits) >= 0 &&
                  valueEcPrec.compareTo(
                    EInteger.FromInt32(Integer.MAX_VALUE).Subtract(divBits)) <=
                  0) {
                  EInteger vbb = divBits.Add(valueEcPrec);
                  if (valueBmBits.compareTo(vbb) < 0) {
                    valueBmBits = vbb.Subtract(valueBmBits);
                    divisor = divisor.ShiftLeft(valueBmBits);
                    adjust.AddBig(valueBmBits);
                    optimized = true;
                  }
                }
              }
            }
            if (!optimized) {
              divisor = divisor.ShiftLeft(1);
              adjust.Increment();
            }
            // DebugUtility.Log("deshigh\n==>" + (//
            // bigmantissa) + "\n-->" + (//
            // divisor));
            // DebugUtility.Log("deshigh " + (//
            // bigmantissa.GetUnsignedBitLengthAsEInteger()) + "/" + (//
            // divisor.GetUnsignedBitLengthAsEInteger()));
            quorem = bigmantissa.DivRem(divisor);
            if (quorem[1].isZero()) {
              EInteger valueBmBits = quorem[0].GetUnsignedBitLengthAsEInteger();
              EInteger divBits = desiredLow.GetUnsignedBitLengthAsEInteger();
              if (valueBmBits.compareTo(divBits) < 0) {
                valueBmBits = divBits.Subtract(valueBmBits);
                quorem[0] = quorem[0].ShiftLeft(valueBmBits);
                adjust.AddBig(valueBmBits);
              }
            }
            // DebugUtility.Log("quorem[0]="+quorem[0]);
            // DebugUtility.Log("quorem[1]="+quorem[1]);
            // DebugUtility.Log("desiredLow="+desiredLow);
            // DebugUtility.Log("desiredHigh="+desiredHigh);
          } while (quorem[0].compareTo(desiredHigh) >= 0);
        } else if (quorem[0].compareTo(desiredLow) < 0) {
          do {
            boolean optimized = false;
            if (bigmantissa.compareTo(divisor) < 0) {
              EInteger valueBmBits =
                bigmantissa.GetUnsignedBitLengthAsEInteger();
              EInteger divBits = divisor.GetUnsignedBitLengthAsEInteger();
              if (valueBmBits.compareTo(divBits) < 0) {
                valueBmBits = divBits.Subtract(valueBmBits);
                bigmantissa = bigmantissa.ShiftLeft(valueBmBits);
                adjust.SubtractBig(valueBmBits);
                optimized = true;
              }
            } else {
              if (ec.getClampNormalExponents() && valueEcPrec.signum() > 0) {
                EInteger valueBmBits =
                  bigmantissa.GetUnsignedBitLengthAsEInteger();
                EInteger divBits = divisor.GetUnsignedBitLengthAsEInteger();
                if (valueBmBits.compareTo(divBits) >= 0 &&
                  valueEcPrec.compareTo(
                    EInteger.FromInt32(Integer.MAX_VALUE).Subtract(divBits)) <=
                  0) {
                  EInteger vbb = divBits.Add(valueEcPrec);
                  if (valueBmBits.compareTo(vbb) < 0) {
                    valueBmBits = vbb.Subtract(valueBmBits);
                    bigmantissa = bigmantissa.ShiftLeft(valueBmBits);
                    adjust.SubtractBig(valueBmBits);
                    optimized = true;
                  }
                }
              }
            }
            if (!optimized) {
              bigmantissa = bigmantissa.ShiftLeft(1);
              adjust.Decrement();
            }
            // DebugUtility.Log("deslow " + (
            // bigmantissa.GetUnsignedBitLengthAsEInteger()) + "/" + (
            // divisor.GetUnsignedBitLengthAsEInteger()));
            quorem = bigmantissa.DivRem(divisor);
            if (quorem[1].isZero()) {
              EInteger valueBmBits = quorem[0].GetUnsignedBitLengthAsEInteger();
              EInteger divBits = desiredLow.GetUnsignedBitLengthAsEInteger();
              if (valueBmBits.compareTo(divBits) < 0) {
                valueBmBits = divBits.Subtract(valueBmBits);
                quorem[0] = quorem[0].ShiftLeft(valueBmBits);
                adjust.SubtractBig(valueBmBits);
              }
            }
          } while (quorem[0].compareTo(desiredLow) < 0);
        }
        // Round to odd to avoid rounding errors
        if (!quorem[1].isZero() && quorem[0].isEven()) {
          quorem[0] = quorem[0].Add(EInteger.FromInt32(1));
        }
        EFloat efret = this.WithThisSign(
            EFloat.Create(
              quorem[0],
              adjust.AsEInteger()));
        // DebugUtility.Log("-->" + (efret.getMantissa().ToRadixString(2)) + " " +
        // efret.getExponent());
        efret = efret.RoundToPrecision(ec);
        if (ec == null) {
          throw new NullPointerException("ec");
        }
        if (haveCopy && originalEc.getHasFlags()) {
          originalEc.setFlags(originalEc.getFlags()|(ec.getFlags()));
        }
        return efret;
      }
    }

    private String ToStringInternal(int mode) {
      boolean negative = (this.flags & BigNumberFlags.FlagNegative) != 0;
      if (!this.isFinite()) {
        if ((this.flags & BigNumberFlags.FlagInfinity) != 0) {
          return negative ? "-Infinity" : "Infinity";
        }
        if ((this.flags & BigNumberFlags.FlagSignalingNaN) != 0) {
          return this.unsignedMantissa.isValueZero() ?
            (negative ? "-sNaN" : "sNaN") :
            (negative ? "-sNaN" + this.unsignedMantissa :
              "sNaN" + this.unsignedMantissa);
        }
        if ((this.flags & BigNumberFlags.FlagQuietNaN) != 0) {
          return this.unsignedMantissa.isValueZero() ? (negative ?
              "-NaN" : "NaN") : (negative ? "-NaN" + this.unsignedMantissa :
              "NaN" + this.unsignedMantissa);
        }
      }
      int scaleSign = -this.exponent.signum();
      String mantissaString = this.unsignedMantissa.toString();
      if (scaleSign == 0) {
        return negative ? "-" + mantissaString : mantissaString;
      }
      boolean iszero = this.unsignedMantissa.isValueZero();
      if (mode == 2 && iszero && scaleSign < 0) {
        // special case for zero in plain
        return negative ? "-" + mantissaString : mantissaString;
      }
      StringBuilder builder = null;
      if (mode == 0 && mantissaString.length() < 100 &&
        this.exponent.CanFitInInt32()) {
        int intExp = this.exponent.AsInt32();
        if (intExp > -100 && intExp < 100) {
          int adj = (intExp + mantissaString.length()) - 1;
          if (scaleSign >= 0 && adj >= -6) {
            if (scaleSign > 0) {
              int dp = intExp + mantissaString.length();
              if (dp < 0) {
                builder = new StringBuilder(mantissaString.length() + 6);
                if (negative) {
                  builder.append("-0.");
                } else {
                  builder.append("0.");
                }
                dp = -dp;
                for (int j = 0; j < dp; ++j) {
                  builder.append('0');
                }
                builder.append(mantissaString);
                return builder.toString();
              } else if (dp == 0) {
                builder = new StringBuilder(mantissaString.length() + 6);
                if (negative) {
                  builder.append("-0.");
                } else {
                  builder.append("0.");
                }
                builder.append(mantissaString);
                return builder.toString();
              } else if (dp > 0 && dp <= mantissaString.length()) {
                builder = new StringBuilder(mantissaString.length() + 6);
                if (negative) {
                  builder.append('-');
                }
                builder.append(mantissaString, 0, dp);
                builder.append('.');
                builder.append(
                  mantissaString, dp, (dp)+(mantissaString.length() - dp));
                return builder.toString();
              }
            }
          }
        }
      }
      FastInteger adjustedExponent = FastInteger.FromBig(this.getExponent());
      FastInteger builderLength = new FastInteger(mantissaString.length());
      FastInteger thisExponent = adjustedExponent.Copy();
      adjustedExponent.Add(builderLength).Decrement();
      FastInteger decimalPointAdjust = new FastInteger(1);
      FastInteger threshold = new FastInteger(-6);
      if (mode == 1) {
        // engineering String adjustments
        FastInteger newExponent = adjustedExponent.Copy();
        boolean adjExponentNegative = adjustedExponent.signum() < 0;
        int intphase = adjustedExponent.Copy().Abs().Remainder(3).AsInt32();
        if (iszero && (adjustedExponent.compareTo(threshold) < 0 || scaleSign <
            0)) {
          if (intphase == 1) {
            if (adjExponentNegative) {
              decimalPointAdjust.Increment();
              newExponent.Increment();
            } else {
              decimalPointAdjust.AddInt(2);
              newExponent.AddInt(2);
            }
          } else if (intphase == 2) {
            if (!adjExponentNegative) {
              decimalPointAdjust.Increment();
              newExponent.Increment();
            } else {
              decimalPointAdjust.AddInt(2);
              newExponent.AddInt(2);
            }
          }
          threshold.Increment();
        } else {
          if (intphase == 1) {
            if (!adjExponentNegative) {
              decimalPointAdjust.Increment();
              newExponent.Decrement();
            } else {
              decimalPointAdjust.AddInt(2);
              newExponent.AddInt(-2);
            }
          } else if (intphase == 2) {
            if (adjExponentNegative) {
              decimalPointAdjust.Increment();
              newExponent.Decrement();
            } else {
              decimalPointAdjust.AddInt(2);
              newExponent.AddInt(-2);
            }
          }
        }
        adjustedExponent = newExponent;
      }
      if (mode == 2 || (adjustedExponent.compareTo(threshold) >= 0 &&
          scaleSign >= 0)) {
        if (scaleSign > 0) {
          FastInteger decimalPoint = thisExponent.Copy().Add(builderLength);
          int cmp = decimalPoint.CompareToInt(0);
          builder = null;
          if (cmp < 0) {
            FastInteger tmpFast = new FastInteger(mantissaString.length()).AddInt(6);
            builder = new StringBuilder(tmpFast.CompareToInt(Integer.MAX_VALUE) >
              0 ? Integer.MAX_VALUE : tmpFast.AsInt32());
            if (negative) {
              builder.append('-');
            }
            builder.append("0.");
            AppendString(builder, '0', decimalPoint.Copy().Negate());
            builder.append(mantissaString);
          } else if (cmp == 0) {
            FastInteger tmpFast = new FastInteger(mantissaString.length()).AddInt(6);
            builder = new StringBuilder(tmpFast.CompareToInt(Integer.MAX_VALUE) >
              0 ? Integer.MAX_VALUE : tmpFast.AsInt32());
            if (negative) {
              builder.append('-');
            }
            builder.append("0.");
            builder.append(mantissaString);
          } else if (decimalPoint.CompareToInt(mantissaString.length()) > 0) {
            FastInteger insertionPoint = builderLength;
            if (!insertionPoint.CanFitInInt32()) {
              throw new UnsupportedOperationException();
            }
            int tmpInt = insertionPoint.AsInt32();
            if (tmpInt < 0) {
              tmpInt = 0;
            }
            FastInteger tmpFast = new FastInteger(mantissaString.length()).AddInt(6);
            builder = new StringBuilder(tmpFast.CompareToInt(Integer.MAX_VALUE) >
              0 ? Integer.MAX_VALUE : tmpFast.AsInt32());
            if (negative) {
              builder.append('-');
            }
            builder.append(mantissaString, 0, tmpInt);
            AppendString(
              builder,
              '0',
              decimalPoint.Copy().SubtractInt(builder.length()));
            builder.append('.');
            builder.append(
              mantissaString, tmpInt, (tmpInt)+(mantissaString.length() - tmpInt));
          } else {
            if (!decimalPoint.CanFitInInt32()) {
              throw new UnsupportedOperationException();
            }
            int tmpInt = decimalPoint.AsInt32();
            if (tmpInt < 0) {
              tmpInt = 0;
            }
            FastInteger tmpFast = new FastInteger(mantissaString.length()).AddInt(6);
            builder = new StringBuilder(tmpFast.CompareToInt(Integer.MAX_VALUE) >
              0 ? Integer.MAX_VALUE : tmpFast.AsInt32());
            if (negative) {
              builder.append('-');
            }
            builder.append(mantissaString, 0, tmpInt);
            builder.append('.');
            builder.append(
              mantissaString, tmpInt, (tmpInt)+(mantissaString.length() - tmpInt));
          }
          return builder.toString();
        }
        if (mode == 2 && scaleSign < 0) {
          FastInteger negscale = thisExponent.Copy();
          builder = new StringBuilder();
          if (negative) {
            builder.append('-');
          }
          builder.append(mantissaString);
          AppendString(builder, '0', negscale);
          return builder.toString();
        }
        return (!negative) ? mantissaString : ("-" + mantissaString);
      } else {
        if (mode == 1 && iszero && decimalPointAdjust.CompareToInt(1) > 0) {
          builder = new StringBuilder();
          if (negative) {
            builder.append('-');
          }
          builder.append(mantissaString);
          builder.append('.');
          AppendString(
            builder,
            '0',
            decimalPointAdjust.Copy().Decrement());
        } else {
          FastInteger tmp = decimalPointAdjust.Copy();
          int cmp = tmp.CompareToInt(mantissaString.length());
          if (cmp > 0) {
            tmp.SubtractInt(mantissaString.length());
            builder = new StringBuilder();
            if (negative) {
              builder.append('-');
            }
            builder.append(mantissaString);
            AppendString(builder, '0', tmp);
          } else if (cmp < 0) {
            // Insert a decimal point at the right place
            if (!tmp.CanFitInInt32()) {
              throw new UnsupportedOperationException();
            }
            int tmpInt = tmp.AsInt32();
            if (tmp.signum() < 0) {
              tmpInt = 0;
            }
            FastInteger tmpFast = new FastInteger(mantissaString.length()).AddInt(6);
            builder = new StringBuilder(tmpFast.CompareToInt(Integer.MAX_VALUE) >
              0 ? Integer.MAX_VALUE : tmpFast.AsInt32());
            if (negative) {
              builder.append('-');
            }
            builder.append(mantissaString, 0, tmpInt);
            builder.append('.');
            builder.append(
              mantissaString, tmpInt, (tmpInt)+(mantissaString.length() - tmpInt));
          } else if (adjustedExponent.signum() == 0 && !negative) {
            return mantissaString;
          } else if (adjustedExponent.signum() == 0 && negative) {
            return "-" + mantissaString;
          } else {
            builder = new StringBuilder();
            if (negative) {
              builder.append('-');
            }
            builder.append(mantissaString);
          }
        }
        if (adjustedExponent.signum() != 0) {
          builder.append(adjustedExponent.signum() < 0 ? "E-" : "E+");
          adjustedExponent.Abs();
          StringBuilder builderReversed = new StringBuilder();
          while (adjustedExponent.signum() != 0) {
            int digit =
              adjustedExponent.Copy().Remainder(10).AsInt32();
            // Each digit is retrieved from right to left
            builderReversed.append((char)('0' + digit));
            adjustedExponent.Divide(10);
          }
          int count = builderReversed.length();
          String builderReversedString = builderReversed.toString();
          for (int i = 0; i < count; ++i) {
            builder.append(builderReversedString.charAt(count - 1 - i));
          }
        }
        return builder.toString();
      }
    }

    private static final class DecimalMathHelper implements IRadixMathHelper<EDecimal> {
      /**
       * This is an internal method.
       * @return A 32-bit signed integer.
       */
      public int GetRadix() {
        return 10;
      }

      /**
       * This is an internal method.
       * @param value An arbitrary-precision decimal number.
       * @return A 32-bit signed integer.
       */
      public int GetSign(EDecimal value) {
        return value.signum();
      }

      /**
       * This is an internal method.
       * @param value An arbitrary-precision decimal number.
       * @return An arbitrary-precision integer.
       */
      public EInteger GetMantissa(EDecimal value) {
        return value.unsignedMantissa.ToEInteger();
      }

      /**
       * This is an internal method.
       * @param value An arbitrary-precision decimal number.
       * @return An arbitrary-precision integer.
       */
      public EInteger GetExponent(EDecimal value) {
        return value.exponent.ToEInteger();
      }

      public FastIntegerFixed GetMantissaFastInt(EDecimal value) {
        return value.unsignedMantissa;
      }

      public FastIntegerFixed GetExponentFastInt(EDecimal value) {
        return value.exponent;
      }

      public FastInteger GetDigitLength(EInteger ei) {
        return FastInteger.FromBig(ei.GetDigitCountAsEInteger());
      }

      public IShiftAccumulator CreateShiftAccumulatorWithDigits(
        EInteger bigint,
        int lastDigit,
        int olderDigits) {
        return new DigitShiftAccumulator(bigint, lastDigit, olderDigits);
      }

      public IShiftAccumulator CreateShiftAccumulatorWithDigitsFastInt(
        FastIntegerFixed fastInt,
        int lastDigit,
        int olderDigits) {
        if (fastInt.CanFitInInt32()) {
          return new DigitShiftAccumulator(
              fastInt.AsInt32(),
              lastDigit,
              olderDigits);
        } else {
          return new DigitShiftAccumulator(
              fastInt.ToEInteger(),
              lastDigit,
              olderDigits);
        }
      }
      public FastInteger DivisionShift(
        EInteger num,
        EInteger den) {
        if (den.isZero()) {
          return null;
        }
        EInteger gcd = den.Gcd(EInteger.FromInt32(10));
        if (gcd.compareTo(EInteger.FromInt32(1)) == 0) {
          return null;
        }
        if (den.isZero()) {
          return null;
        }
        // Eliminate factors of 2
        EInteger elowbit = den.GetLowBitAsEInteger();
        den = den.ShiftRight(elowbit);
        // Eliminate factors of 5
        FastInteger fiveShift = new FastInteger(0);
        while (true) {
          EInteger bigrem;
          EInteger bigquo;
          {
            EInteger[] divrem = den.DivRem(EInteger.FromInt64(5));
            bigquo = divrem[0];
            bigrem = divrem[1];
          }
          if (!bigrem.isZero()) {
            break;
          }
          fiveShift.Increment();
          den = bigquo;
        }
        if (den.compareTo(EInteger.FromInt32(1)) != 0) {
          return null;
        }
        FastInteger fastlowbit = FastInteger.FromBig(elowbit);
        if (fiveShift.compareTo(fastlowbit) > 0) {
          return fiveShift;
        } else {
          return fastlowbit;
        }
      }

      public EInteger MultiplyByRadixPower(
        EInteger bigint,
        FastInteger power) {
        EInteger tmpbigint = bigint;
        if (tmpbigint.isZero()) {
          return tmpbigint;
        }
        boolean fitsInInt32 = power.CanFitInInt32();
        int powerInt = fitsInInt32 ? power.AsInt32() : 0;
        if (fitsInInt32 && powerInt == 0) {
          return tmpbigint;
        }
        EInteger bigtmp = null;
        if (tmpbigint.compareTo(EInteger.FromInt32(1)) != 0) {
          if (fitsInInt32) {
            if (powerInt <= 10) {
              bigtmp = NumberUtility.FindPowerOfTen(powerInt);
              tmpbigint = tmpbigint.Multiply(bigtmp);
            } else {
              bigtmp = NumberUtility.FindPowerOfFive(powerInt);
              tmpbigint = tmpbigint.Multiply(bigtmp);
              tmpbigint = tmpbigint.ShiftLeft(powerInt);
            }
          } else {
            bigtmp = NumberUtility.FindPowerOfTenFromBig(power.AsEInteger());
            tmpbigint = tmpbigint.Multiply(bigtmp);
          }
          return tmpbigint;
        }
        return fitsInInt32 ? NumberUtility.FindPowerOfTen(powerInt) :
          NumberUtility.FindPowerOfTenFromBig(power.AsEInteger());
      }

      /**
       * This is an internal method.
       * @param value An arbitrary-precision decimal number.
       * @return A 32-bit signed integer.
       */
      public int GetFlags(EDecimal value) {
        return value.flags;
      }

      /**
       * This is an internal method.
       * @param mantissa The parameter {@code mantissa} is a Numbers.EInteger object.
       * @param exponent The parameter {@code exponent} is an internal parameter.
       * @param flags The parameter {@code flags} is an internal parameter.
       * @return An arbitrary-precision decimal number.
       */
      public EDecimal CreateNewWithFlags(
        EInteger mantissa,
        EInteger exponent,
        int flags) {
        return CreateWithFlags(
            FastIntegerFixed.FromBig(mantissa),
            FastIntegerFixed.FromBig(exponent),
            flags);
      }

      public EDecimal CreateNewWithFlagsFastInt(
        FastIntegerFixed fmantissa,
        FastIntegerFixed fexponent,
        int flags) {
        return CreateWithFlags(fmantissa, fexponent, flags);
      }

      /**
       * This is an internal method.
       * @return A 32-bit signed integer.
       */
      public int GetArithmeticSupport() {
        return BigNumberFlags.FiniteAndNonFinite;
      }

      /**
       * This is an internal method.
       * @param val The parameter {@code val} is a 32-bit signed integer.
       * @return An arbitrary-precision decimal number.
       */
      public EDecimal ValueOf(int val) {
        return (val == 0) ? Zero : ((val == 1) ? One : FromInt64(val));
      }
    }

    /**
     * Returns one added to this arbitrary-precision decimal number.
     * @return The given arbitrary-precision decimal number plus one.
     */
    public EDecimal Increment() {
      return this.Add(1);
    }

    /**
     * Returns one subtracted from this arbitrary-precision decimal number.
     * @return The given arbitrary-precision decimal number minus one.
     */
    public EDecimal Decrement() {
      return this.Subtract(1);
    }

    // Begin integer conversions

    /**
     * Converts this number's value to a byte (from 0 to 255) if it can fit in a
     * byte (from 0 to 255) after converting it to an integer by discarding
     * its fractional part.
     * @return This number's value, truncated to a byte (from 0 to 255).
     * @throws ArithmeticException This value is infinity or not-a-number, or the
     * number, once converted to an integer by discarding its fractional
     * part, is less than 0 or greater than 255.
     */
    public byte ToByteChecked() {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      if (this.IsIntegerPartZero()) {
        return (byte)0;
      }
      if (this.exponent.CompareToInt(3) >= 0) {
        throw new ArithmeticException("Value out of range");
      }
      return this.ToEInteger().ToByteChecked();
    }

    /**
     * Converts this number's value to an integer by discarding its fractional
     * part, and returns the least-significant bits of its two's-complement
     * form as a byte (from 0 to 255).
     * @return This number, converted to a byte (from 0 to 255). Returns 0 if this
     * value is infinity or not-a-number.
     */
    public byte ToByteUnchecked() {
      return this.isFinite() ? this.ToEInteger().ToByteUnchecked() : (byte)0;
    }

    /**
     * Converts this number's value to a byte (from 0 to 255) if it can fit in a
     * byte (from 0 to 255) without rounding to a different numerical
     * value.
     * @return This number's value as a byte (from 0 to 255).
     * @throws ArithmeticException This value is infinity or not-a-number, is not
     * an exact integer, or is less than 0 or greater than 255.
     */
    public byte ToByteIfExact() {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      if (this.isZero()) {
        return (byte)0;
      }
      if (this.isNegative()) {
        throw new ArithmeticException("Value out of range");
      }
      if (this.exponent.CompareToInt(3) >= 0) {
        throw new ArithmeticException("Value out of range");
      }
      return this.ToEIntegerIfExact().ToByteChecked();
    }

    /**
     * Converts a byte (from 0 to 255) to an arbitrary-precision decimal number.
     * @param inputByte The number to convert as a byte (from 0 to 255).
     * @return This number's value as an arbitrary-precision decimal number.
     */
    public static EDecimal FromByte(byte inputByte) {
      int val = ((int)inputByte) & 0xff;
      return FromInt32(val);
    }

    /**
     * Converts this number's value to a 16-bit signed integer if it can fit in a
     * 16-bit signed integer after converting it to an integer by
     * discarding its fractional part.
     * @return This number's value, truncated to a 16-bit signed integer.
     * @throws ArithmeticException This value is infinity or not-a-number, or the
     * number, once converted to an integer by discarding its fractional
     * part, is less than -32768 or greater than 32767.
     */
    public short ToInt16Checked() {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      if (this.IsIntegerPartZero()) {
        return (short)0;
      }
      if (this.exponent.CompareToInt(5) >= 0) {
        throw new ArithmeticException("Value out of range: ");
      }
      return this.ToEInteger().ToInt16Checked();
    }

    /**
     * Converts this number's value to an integer by discarding its fractional
     * part, and returns the least-significant bits of its two's-complement
     * form as a 16-bit signed integer.
     * @return This number, converted to a 16-bit signed integer. Returns 0 if this
     * value is infinity or not-a-number.
     */
    public short ToInt16Unchecked() {
      return this.isFinite() ? this.ToEInteger().ToInt16Unchecked() : (short)0;
    }

    /**
     * Converts this number's value to a 16-bit signed integer if it can fit in a
     * 16-bit signed integer without rounding to a different numerical
     * value.
     * @return This number's value as a 16-bit signed integer.
     * @throws ArithmeticException This value is infinity or not-a-number, is not
     * an exact integer, or is less than -32768 or greater than 32767.
     */
    public short ToInt16IfExact() {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      if (this.isZero()) {
        return (short)0;
      }
      if (this.exponent.CompareToInt(5) >= 0) {
        throw new ArithmeticException("Value out of range");
      }
      return this.ToEIntegerIfExact().ToInt16Checked();
    }

    /**
     * Converts a 16-bit signed integer to an arbitrary-precision decimal number.
     * @param inputInt16 The number to convert as a 16-bit signed integer.
     * @return This number's value as an arbitrary-precision decimal number.
     */
    public static EDecimal FromInt16(short inputInt16) {
      int val = (int)inputInt16;
      return FromInt32(val);
    }

    /**
     * Converts this number's value to a 32-bit signed integer if it can fit in a
     * 32-bit signed integer after converting it to an integer by
     * discarding its fractional part.
     * @return This number's value, truncated to a 32-bit signed integer.
     * @throws ArithmeticException This value is infinity or not-a-number, or the
     * number, once converted to an integer by discarding its fractional
     * part, is less than -2147483648 or greater than 2147483647.
     */
    public int ToInt32Checked() {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      if (this.IsIntegerPartZero()) {
        return (int)0;
      }
      if (this.exponent.CompareToInt(10) >= 0) {
        throw new ArithmeticException("Value out of range: ");
      }
      return this.ToEInteger().ToInt32Checked();
    }

    /**
     * Converts this number's value to an integer by discarding its fractional
     * part, and returns the least-significant bits of its two's-complement
     * form as a 32-bit signed integer.
     * @return This number, converted to a 32-bit signed integer. Returns 0 if this
     * value is infinity or not-a-number.
     */
    public int ToInt32Unchecked() {
      return this.isFinite() ? this.ToEInteger().ToInt32Unchecked() : (int)0;
    }

    /**
     * Converts this number's value to a 32-bit signed integer if it can fit in a
     * 32-bit signed integer without rounding to a different numerical
     * value.
     * @return This number's value as a 32-bit signed integer.
     * @throws ArithmeticException This value is infinity or not-a-number, is not
     * an exact integer, or is less than -2147483648 or greater than
     * 2147483647.
     */
    public int ToInt32IfExact() {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      if (this.isZero()) {
        return (int)0;
      }
      if (this.exponent.CompareToInt(10) >= 0) {
        throw new ArithmeticException("Value out of range");
      }
      return this.ToEIntegerIfExact().ToInt32Checked();
    }

    /**
     * Converts this number's value to a 64-bit signed integer if it can fit in a
     * 64-bit signed integer after converting it to an integer by
     * discarding its fractional part.
     * @return This number's value, truncated to a 64-bit signed integer.
     * @throws ArithmeticException This value is infinity or not-a-number, or the
     * number, once converted to an integer by discarding its fractional
     * part, is less than -9223372036854775808 or greater than
     * 9223372036854775807.
     */
    public long ToInt64Checked() {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      if (this.IsIntegerPartZero()) {
        return 0L;
      }
      if (this.exponent.CompareToInt(19) >= 0) {
        throw new ArithmeticException("Value out of range: ");
      }
      return this.ToEInteger().ToInt64Checked();
    }

    /**
     * Converts this number's value to an integer by discarding its fractional
     * part, and returns the least-significant bits of its two's-complement
     * form as a 64-bit signed integer.
     * @return This number, converted to a 64-bit signed integer. Returns 0 if this
     * value is infinity or not-a-number.
     */
    public long ToInt64Unchecked() {
      return this.isFinite() ? this.ToEInteger().ToInt64Unchecked() : 0L;
    }

    /**
     * Converts this number's value to a 64-bit signed integer if it can fit in a
     * 64-bit signed integer without rounding to a different numerical
     * value.
     * @return This number's value as a 64-bit signed integer.
     * @throws ArithmeticException This value is infinity or not-a-number, is not
     * an exact integer, or is less than -9223372036854775808 or greater
     * than 9223372036854775807.
     */
    public long ToInt64IfExact() {
      if (!this.isFinite()) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      if (this.isZero()) {
        return 0L;
      }
      if (this.exponent.CompareToInt(19) >= 0) {
        throw new ArithmeticException("Value out of range");
      }
      return this.ToEIntegerIfExact().ToInt64Checked();
    }
    // End integer conversions
  }
