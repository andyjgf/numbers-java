package com.upokecenter.numbers;
/*
Written by Peter O. in 2013.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/
If you like this, you should donate to Peter O.
at: http://peteroupc.github.io/
 */

    /**
     * Contains parameters for controlling the precision, rounding, and exponent
     * range of arbitrary-precision numbers. (The "E" stands for "extended",
     * and has this prefix to group it with the other classes common to this
     * library, particularly EDecimal, EFloat, and ERational.). <p><b>Thread
     * safety:</b> With one exception, instances of this class are immutable
     * and are safe to use among multiple threads. The one exception
     * involves the <code>Flags</code> property. If the context's <code>HasFlags</code>
     * property (a read-only property) is <code>true</code>, the <code>Flags</code>
     * property is mutable, thus making the context mutable. This class
     * doesn't synchronize access to such mutable contexts, so applications
     * should provide their own synchronization if a context with the
     * <code>HasFlags</code> property set to <code>true</code> will be shared among
     * multiple threads and at least one of those threads needs to write the
     * <code>Flags</code> property (which can happen, for example, by passing the
     * context to most methods of <code>EDecimal</code> such as <code>Add</code>).</p>
     */
  public final class EContext {
    /**
     * Signals that the exponent was adjusted to fit the exponent range.
     */
    public static final int FlagClamped = 32;

    /**
     * Signals a division of a nonzero number by zero.
     */
    public static final int FlagDivideByZero = 128;

    /**
     * Signals that the result was rounded to a different mathematical value, but
     * as close as possible to the original.
     */
    public static final int FlagInexact = 1;

    /**
     * Signals an invalid operation.
     */
    public static final int FlagInvalid = 64;

    /**
     * Signals that an operand was rounded to a different mathematical value before
     * an operation.
     */
    public static final int FlagLostDigits = 256;

    /**
     * Signals that the result is non-zero and the exponent is higher than the
     * highest exponent allowed.
     */
    public static final int FlagOverflow = 16;

    /**
     * Signals that the result was rounded to fit the precision; either the value
     * or the exponent may have changed from the original.
     */
    public static final int FlagRounded = 2;

    /**
     * Signals that the result's exponent, before rounding, is lower than the
     * lowest exponent allowed.
     */
    public static final int FlagSubnormal = 4;

    /**
     * Signals that the result's exponent, before rounding, is lower than the
     * lowest exponent allowed, and the result was rounded to a different
     * mathematical value, but as close as possible to the original.
     */
    public static final int FlagUnderflow = 8;

    /**
     * A basic arithmetic context, 9 digits precision, rounding mode half-up,
     * unlimited exponent range. The default rounding mode is HalfUp.
     */

    public static final EContext Basic =
      EContext.ForPrecisionAndRounding(9, ERounding.HalfUp);

    /**
     * An arithmetic context for Java's BigDecimal format. The default rounding
     * mode is HalfUp.
     */

    public static final EContext BigDecimalJava =
      new EContext(0, ERounding.HalfUp, 0, 0, true)
      .WithExponentClamp(true).WithAdjustExponent(false)
      .WithBigExponentRange(
  EInteger.FromInt32(0).Subtract(EInteger.FromInt64(Integer.MAX_VALUE)),
  EInteger.FromInt32(1).Add(EInteger.FromInt64(Integer.MAX_VALUE)));

    /**
     * An arithmetic context for the IEEE-754-2008 binary128 format, 113 bits
     * precision. The default rounding mode is HalfEven.
     */

    public static final EContext Binary128 =
      EContext.ForPrecisionAndRounding(113, ERounding.HalfEven)
      .WithExponentClamp(true).WithExponentRange(-16382, 16383);

    /**
     * An arithmetic context for the IEEE-754-2008 binary16 format, 11 bits
     * precision. The default rounding mode is HalfEven.
     */

    public static final EContext Binary16 =
      EContext.ForPrecisionAndRounding(11, ERounding.HalfEven)
      .WithExponentClamp(true).WithExponentRange(-14, 15);

    /**
     * An arithmetic context for the IEEE-754-2008 binary32 format, 24 bits
     * precision. The default rounding mode is HalfEven.
     */

    public static final EContext Binary32 =
      EContext.ForPrecisionAndRounding(24, ERounding.HalfEven)
      .WithExponentClamp(true).WithExponentRange(-126, 127);

    /**
     * An arithmetic context for the IEEE-754-2008 binary64 format, 53 bits
     * precision. The default rounding mode is HalfEven.
     */

    public static final EContext Binary64 =
      EContext.ForPrecisionAndRounding(53, ERounding.HalfEven)
      .WithExponentClamp(true).WithExponentRange(-1022, 1023);

    /**
     * An arithmetic context for the .NET Framework decimal format (see {@link
     * com.upokecenter.numbers.EDecimal "Forms of numbers"}), 96 bits
     * precision, and a valid exponent range of -28 to 0. The default
     * rounding mode is HalfEven. (The <code>"Cli"</code> stands for "Common
     * Language Infrastructure", which defined this format as the .NET
     * Framework decimal format in version 1, but leaves it unspecified in
     * later versions.).
     */

    public static final EContext CliDecimal =
      new EContext(96, ERounding.HalfEven, 0, 28, true)
      .WithPrecisionInBits(true);

    /**
     * An arithmetic context for the IEEE-754-2008 decimal128 format. The default
     * rounding mode is HalfEven.
     */

    public static final EContext Decimal128 =
      new EContext(34, ERounding.HalfEven, -6143, 6144, true);

    /**
     * An arithmetic context for the IEEE-754-2008 decimal32 format. The default
     * rounding mode is HalfEven.
     */

    public static final EContext Decimal32 =
      new EContext(7, ERounding.HalfEven, -95, 96, true);

    /**
     * An arithmetic context for the IEEE-754-2008 decimal64 format. The default
     * rounding mode is HalfEven.
     */

    public static final EContext Decimal64 =
      new EContext(16, ERounding.HalfEven, -383, 384, true);

    /**
     * No specific (theoretical) limit on precision. Rounding mode HalfUp.
     */

    public static final EContext Unlimited =
      EContext.ForPrecision(0);

    /**
     * No specific (theoretical) limit on precision. Rounding mode HalfEven.
     */

    public static final EContext UnlimitedHalfEven =
      EContext.ForPrecision(0).WithRounding(ERounding.HalfEven);

    // TODO: Improve API's immutability (make all
    // fields except flags and traps readonly/final)
    // and include a construtor setting all fields
    private boolean adjustExponent;

    private EInteger bigintPrecision;

    private boolean clampNormalExponents;
    private EInteger exponentMax;

    private EInteger exponentMin;

    private int flags;

    private boolean hasExponentRange;
    private boolean hasFlags;

    private boolean precisionInBits;

    private ERounding rounding;

    private boolean simplified;

    private int traps;

    /**
     *
     * @param precision A 32-bit signed integer.
     * @param rounding An ERounding object.
     * @param exponentMinSmall Another 32-bit signed integer.
     * @param exponentMaxSmall A 32-bit signed integer. (3).
     * @param clampNormalExponents A Boolean object.
     */
    public EContext(
  int precision,
  ERounding rounding,
  int exponentMinSmall,
  int exponentMaxSmall,
  boolean clampNormalExponents) {
      if (precision < 0) {
        throw new IllegalArgumentException("precision (" + precision +
          ") is less than 0");
      }
      if (exponentMinSmall > exponentMaxSmall) {
        throw new IllegalArgumentException("exponentMinSmall (" + exponentMinSmall +
          ") is more than " + exponentMaxSmall);
      }
      this.bigintPrecision = precision == 0 ? EInteger.FromInt32(0) :
        EInteger.FromInt32(precision);
      this.rounding = rounding;
      this.clampNormalExponents = clampNormalExponents;
      this.hasExponentRange = true;
      this.adjustExponent = true;
      this.exponentMax = exponentMaxSmall == 0 ? EInteger.FromInt32(0) :
        EInteger.FromInt32(exponentMaxSmall);
      this.exponentMin = exponentMinSmall == 0 ? EInteger.FromInt32(0) :
        EInteger.FromInt32(exponentMinSmall);
    }

    /**
     *
     */
    public final boolean getAdjustExponent() {
        return this.adjustExponent;
      }

    /**
     *
     */
    public final boolean getClampNormalExponents() {
        return this.hasExponentRange && this.clampNormalExponents;
      }

    /**
     *
     */
    public final EInteger getEMax() {
        return this.hasExponentRange ? this.exponentMax : EInteger.FromInt32(0);
      }

    /**
     *
     */
    public final EInteger getEMin() {
        return this.hasExponentRange ? this.exponentMin : EInteger.FromInt32(0);
      }

    /**
     *
     */
    public final int getFlags() {
        return this.flags;
      }
public final void setFlags(int value) {
        if (!this.getHasFlags()) {
          throw new IllegalStateException("Can't set flags");
        }
        this.flags = value;
      }

    /**
     *
     */
    public final boolean getHasExponentRange() {
        return this.hasExponentRange;
      }

    /**
     *
     */
    public final boolean getHasFlags() {
        return this.hasFlags;
      }

    /**
     *
     */
    public final boolean getHasMaxPrecision() {
        return !this.bigintPrecision.isZero();
      }

    /**
     *
     */
    public final boolean isPrecisionInBits() {
        return this.precisionInBits;
      }

    /**
     *
     */
    public final boolean isSimplified() {
        return this.simplified;
      }

    /**
     *
     */
    public final EInteger getPrecision() {
        return this.bigintPrecision;
      }

    /**
     *
     */
    public final ERounding getRounding() {
        return this.rounding;
      }

    /**
     *
     */
    public final int getTraps() {
        return this.traps;
      }

    /**
     *
     * @param precision Not documented yet.
     * @return An EContext object.
     */
    public static EContext ForPrecision(int precision) {
      return new EContext(
  precision,
  ERounding.HalfUp,
  0,
  0,
  false).WithUnlimitedExponents();
    }

    /**
     *
     * @param precision Not documented yet.
     * @param rounding Not documented yet.
     * @return An EContext object.
     */
    public static EContext ForPrecisionAndRounding(
      int precision,
      ERounding rounding) {
      return new EContext(
  precision,
  rounding,
  0,
  0,
  false).WithUnlimitedExponents();
    }

    private static final EContext ForRoundingHalfEven = new EContext(
  0,
  ERounding.HalfEven,
  0,
  0,
  false).WithUnlimitedExponents();

    private static final EContext ForRoundingDown = new EContext(
  0,
  ERounding.Down,
  0,
  0,
  false).WithUnlimitedExponents();

    /**
     *
     * @param rounding Not documented yet.
     * @return An EContext object.
     */
    public static EContext ForRounding(ERounding rounding) {
      if (rounding == ERounding.HalfEven) {
        return ForRoundingHalfEven;
      }
      if (rounding == ERounding.Down) {
        return ForRoundingDown;
      }
      return new EContext(
  0,
  rounding,
  0,
  0,
  false).WithUnlimitedExponents();
    }

    /**
     *
     * @return An EContext object.
     */
    public EContext Copy() {
      EContext pcnew = new EContext(
        0,
        this.rounding,
        0,
        0,
        this.clampNormalExponents);
      pcnew.hasFlags = this.hasFlags;
      pcnew.precisionInBits = this.precisionInBits;
      pcnew.adjustExponent = this.adjustExponent;
      pcnew.simplified = this.simplified;
      pcnew.flags = this.flags;
      pcnew.exponentMax = this.exponentMax;
      pcnew.exponentMin = this.exponentMin;
      pcnew.hasExponentRange = this.hasExponentRange;
      pcnew.bigintPrecision = this.bigintPrecision;
      pcnew.rounding = this.rounding;
      pcnew.clampNormalExponents = this.clampNormalExponents;
      return pcnew;
    }

    /**
     *
     * @param exponent Not documented yet.
     * @return A Boolean object.
     * @throws NullPointerException The parameter is null.
     */
    public boolean ExponentWithinRange(EInteger exponent) {
      if (exponent == null) {
        throw new NullPointerException("exponent");
      }
      if (!this.getHasExponentRange()) {
        return true;
      }
      if (this.bigintPrecision.isZero()) {
        // Only check EMax, since with an unlimited
        // precision, any exponent less than EMin will exceed EMin if
        // the mantissa is the right size
        return exponent.compareTo(this.getEMax()) <= 0;
      } else {
        EInteger bigint = exponent;
        if (this.adjustExponent) {
          bigint = bigint.Add(this.bigintPrecision);
          bigint = bigint.Subtract(EInteger.FromInt32(1));
        }
        return (bigint.compareTo(this.getEMin()) >= 0) &&
          (exponent.compareTo(this.getEMax()) <= 0);
      }
    }

    /**
     *
     * @return A string object.
     */
    @Override public String toString() {
      return "[PrecisionContext ExponentMax=" + this.exponentMax +
        ", Traps=" + this.traps + ", ExponentMin=" + this.exponentMin +
        ", HasExponentRange=" + this.hasExponentRange + ", BigintPrecision=" +
        this.bigintPrecision + ", Rounding=" + this.rounding +
        ", ClampNormalExponents=" + this.clampNormalExponents + ", Flags=" +
        this.flags + ", HasFlags=" + this.hasFlags + "]";
    }

    /**
     *
     * @param adjustExponent Not documented yet.
     * @return An EContext object.
     */
    public EContext WithAdjustExponent(boolean adjustExponent) {
      EContext pc = this.Copy();
      pc.adjustExponent = adjustExponent;
      return pc;
    }

    /**
     *
     * @param exponentMin Not documented yet.
     * @param exponentMax Not documented yet.
     * @return An EContext object.
     * @throws NullPointerException The parameter is null.
     */
    public EContext WithBigExponentRange(
      EInteger exponentMin,
      EInteger exponentMax) {
      if (exponentMin == null) {
        throw new NullPointerException("exponentMin");
      }
      if (exponentMax == null) {
        throw new NullPointerException("exponentMax");
      }
      if (exponentMin.compareTo(exponentMax) > 0) {
        throw new IllegalArgumentException("exponentMin greater than exponentMax");
      }
      EContext pc = this.Copy();
      pc.hasExponentRange = true;
      pc.exponentMin = exponentMin;
      pc.exponentMax = exponentMax;
      return pc;
    }

    /**
     *
     * @param bigintPrecision Not documented yet.
     * @return An EContext object.
     * @throws NullPointerException The parameter is null.
     */
    public EContext WithBigPrecision(EInteger bigintPrecision) {
      if (bigintPrecision == null) {
        throw new NullPointerException("bigintPrecision");
      }
      if (bigintPrecision.signum() < 0) {
        throw new IllegalArgumentException("bigintPrecision's sign (" +
          bigintPrecision.signum() + ") is less than 0");
      }
      EContext pc = this.Copy();
      pc.bigintPrecision = bigintPrecision;
      return pc;
    }

    /**
     *
     * @return An EContext object.
     */
    public EContext WithBlankFlags() {
      EContext pc = this.Copy();
      pc.hasFlags = true;
      pc.flags = 0;
      return pc;
    }

    /**
     *
     * @param clamp Not documented yet.
     * @return An EContext object.
     */
    public EContext WithExponentClamp(boolean clamp) {
      EContext pc = this.Copy();
      pc.clampNormalExponents = clamp;
      return pc;
    }

    /**
     *
     * @param exponentMinSmall Not documented yet.
     * @param exponentMaxSmall Not documented yet.
     * @return An EContext object.
     */
    public EContext WithExponentRange(
      int exponentMinSmall,
      int exponentMaxSmall) {
      if (exponentMinSmall > exponentMaxSmall) {
        throw new IllegalArgumentException("exponentMinSmall (" + exponentMinSmall +
          ") is more than " + exponentMaxSmall);
      }
      EContext pc = this.Copy();
      pc.hasExponentRange = true;
      pc.exponentMin = EInteger.FromInt32(exponentMinSmall);
      pc.exponentMax = EInteger.FromInt32(exponentMaxSmall);
      return pc;
    }

    /**
     *
     * @return An EContext object.
     */
    public EContext WithNoFlags() {
      EContext pc = this.Copy();
      pc.hasFlags = false;
      pc.flags = 0;
      return pc;
    }

    /**
     *
     * @param precision Not documented yet.
     * @return An EContext object.
     */
    public EContext WithPrecision(int precision) {
      if (precision < 0) {
        throw new IllegalArgumentException("precision (" + precision +
          ") is less than 0");
      }
      EContext pc = this.Copy();
      pc.bigintPrecision = EInteger.FromInt32(precision);
      return pc;
    }

    /**
     *
     * @param isPrecisionBits Not documented yet.
     * @return An EContext object.
     */
    public EContext WithPrecisionInBits(boolean isPrecisionBits) {
      EContext pc = this.Copy();
      pc.precisionInBits = isPrecisionBits;
      return pc;
    }

    /**
     *
     * @param rounding Not documented yet.
     * @return An EContext object.
     */
    public EContext WithRounding(ERounding rounding) {
      EContext pc = this.Copy();
      pc.rounding = rounding;
      return pc;
    }

    /**
     *
     * @param simplified Not documented yet.
     * @return An EContext object.
     */
    public EContext WithSimplified(boolean simplified) {
      EContext pc = this.Copy();
      pc.simplified = simplified;
      return pc;
    }

    /**
     *
     * @param traps Not documented yet.
     * @return An EContext object.
     */
    public EContext WithTraps(int traps) {
      EContext pc = this.Copy();
      pc.hasFlags = true;
      pc.traps = traps;
      return pc;
    }

    /**
     *
     * @return An EContext object.
     */
    public EContext WithUnlimitedExponents() {
      EContext pc = this.Copy();
      pc.hasExponentRange = false;
      return pc;
    }
  }
