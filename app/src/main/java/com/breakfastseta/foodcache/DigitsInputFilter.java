package com.breakfastseta.foodcache;

import android.text.InputFilter;
import android.text.Spanned;

/*
TO USE
mEditText.setFilters(DigitsInputFilter.INTEGER_FILTER);
modified from https://stackoverflow.com/questions/17423483/how-to-limit-edittext-length-to-7-integers-and-2-decimal-places/21802109
 */

public class DigitsInputFilter implements InputFilter {

    private final String DOT = ".";

    private int mMaxIntegerDigitsLength;
    private int mMaxDigitsAfterLength;
    private double mMax;
    private boolean isInteger;

    public static InputFilter[] INTEGER_FILTER = new InputFilter[]{new DigitsInputFilter(4, 0, true)};
    public static InputFilter[] DOUBLE_FILTER = new InputFilter[]{new DigitsInputFilter(4, 2, false)};

    public DigitsInputFilter(int maxDigitsBeforeDot, int maxDigitsAfterDot, boolean isInteger) {
        mMaxIntegerDigitsLength = maxDigitsBeforeDot;
        mMaxDigitsAfterLength = maxDigitsAfterDot;
        mMax = Integer.MAX_VALUE;
        this.isInteger = isInteger;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String allText = getAllText(source, dest, dstart);
        String onlyDigitsText = getOnlyDigitsPart(allText);

        if (source.toString().equals(".")) {
            if (isInteger) {
                return "";
            } else {
                if (dest.toString().length() == 0) {
                    return "0.";
                }
            }
        }

        if (allText.isEmpty()) {
            return null;
        } else {
            double enteredValue;
            try {
                enteredValue = Double.parseDouble(onlyDigitsText);
            } catch (NumberFormatException e) {
                return "";
            }
            return checkMaxValueRule(enteredValue, onlyDigitsText);
        }
    }


    private CharSequence checkMaxValueRule(double enteredValue, String onlyDigitsText) {
        if (enteredValue > mMax) {
            return "";
        } else {
            return handleInputRules(onlyDigitsText);
        }
    }

    private CharSequence handleInputRules(String onlyDigitsText) {
        if (isDecimalDigit(onlyDigitsText)) {
            return checkRuleForDecimalDigits(onlyDigitsText);
        } else {
            return checkRuleForIntegerDigits(onlyDigitsText.length());
        }
    }

    private boolean isDecimalDigit(String onlyDigitsText) {
        return onlyDigitsText.contains(DOT);
    }

    private CharSequence checkRuleForDecimalDigits(String onlyDigitsPart) {
        String afterDotPart = onlyDigitsPart.substring(onlyDigitsPart.indexOf(DOT), onlyDigitsPart.length() - 1);

        if (afterDotPart.length() > mMaxDigitsAfterLength) {
            return "";
        }
        return null;
    }

    private CharSequence checkRuleForIntegerDigits(int allTextLength) {
        if (allTextLength > mMaxIntegerDigitsLength) {
            return "";
        }
        return null;
    }

    private String getOnlyDigitsPart(String text) {

        return text.replaceAll("[^0-9?!\\.]", "");
    }

    private String getAllText(CharSequence source, Spanned dest, int dstart) {
        String allText = "";
        if (!dest.toString().isEmpty()) {
            if (source.toString().isEmpty()) {
                allText = deleteCharAtIndex(dest, dstart);
            } else {
                allText = new StringBuilder(dest).insert(dstart, source).toString();
            }
        }
        return allText;
    }

    private String deleteCharAtIndex(Spanned dest, int dstart) {
        StringBuilder builder = new StringBuilder(dest);
        builder.deleteCharAt(dstart);
        return builder.toString();
    }
}