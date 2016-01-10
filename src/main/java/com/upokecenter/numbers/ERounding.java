package com.upokecenter.numbers;
/*
Written in 2013 by Peter O.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/
If you like this, you should donate to Peter O.
at: http://upokecenter.dreamhosters.com/articles/donate-now-2/
 */

    /**
     * Specifies the mode to use when &#x22;shortening&#x22; numbers that otherwise
     * can&#x27;t fit a given number of digits, so that the shortened number
     * has about the same value. This &#x22;shortening&#x22; is known as
     * rounding.
     */
  public enum ERounding {
    /**
     * Indicates that rounding will not be used. If rounding is required, the
     * rounding operation will report an error.
     */
    None,

    /**
     * If there is a fractional part, the number is rounded to the closest
     * representable number away from zero.
     */
    Up,

    /**
     * The fractional part is discarded (the number is truncated).
     */
    Down,

    /**
     * Rounded to the nearest number; if the fractional part is exactly half, the
     * number is rounded to the closest representable number away from zero.
     * This is the most familiar rounding mode for many people.
     */
    HalfUp,

    /**
     * Rounded to the nearest number; if the fractional part is exactly half, it is
     * discarded.
     */
    HalfDown,

    /**
     * Rounded to the nearest number; if the fractional part is exactly half, the
     * number is rounded to the closest representable number that is even.
     * This is sometimes also known as &#x22;banker&#x27;s rounding&#x22;.
     */
    HalfEven,

    /**
     * If there is a fractional part, the number is rounded to the highest
     * representable number that&#x27;s closest to it.
     */
    Ceiling,

    /**
     * If there is a fractional part, the number is rounded to the lowest
     * representable number that&#x27;s closest to it.
     */
    Floor,

    /**
     * If there is a fractional part and the whole number part is even, the number
     * is rounded to the closest representable odd number away from zero.
     */
    Odd,

    /**
     * If there is a fractional part and if the last digit before rounding is 0 or
     * half the radix, the number is rounded to the closest representable
     * number away from zero; otherwise the fractional part is discarded. In
     * overflow, the fractional part is always discarded.
     */
    ZeroFiveUp,

    /**
     * For binary floating point numbers, this is the same as Odd. For other bases
     * (including decimal numbers), this is the same as ZeroFiveUp. This
     * rounding mode is useful for rounding intermediate results at a
     * slightly higher precision (at least 2 bits more for binary) than the
     * final precision.
     */
    OddOrZeroFiveUp
  }
