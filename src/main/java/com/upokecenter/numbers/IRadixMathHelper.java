package com.upokecenter.numbers;
/*
Written by Peter O.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/
If you like this, you should donate to Peter O.
at: http://peteroupc.github.io/
 */

  interface IRadixMathHelper<T> {
    int GetRadix();

    int GetArithmeticSupport();

    int GetSign(T value);

    int GetFlags(T value);

    EInteger GetMantissa(T value);

    EInteger GetExponent(T value);

    FastIntegerFixed GetMantissaFastInt(T value);

    FastIntegerFixed GetExponentFastInt(T value);

    T ValueOf(int val);

    T CreateNewWithFlags(EInteger mantissa, EInteger exponent, int flags);

    T CreateNewWithFlagsFastInt(
      FastIntegerFixed mantissa,
      FastIntegerFixed exponent,
      int flags);

    IShiftAccumulator CreateShiftAccumulatorWithDigits(
      EInteger value,
      int lastDigit,
      int olderDigits);

    IShiftAccumulator CreateShiftAccumulatorWithDigitsFastInt(
      FastIntegerFixed value,
      int lastDigit,
      int olderDigits);

    FastInteger DivisionShift(EInteger num, EInteger den);

    FastInteger GetDigitLength(EInteger ei);

    EInteger MultiplyByRadixPower(EInteger value, FastInteger power);

    FastIntegerFixed MultiplyByRadixPowerFastInt(
      FastIntegerFixed value,
      FastIntegerFixed power);
  }
