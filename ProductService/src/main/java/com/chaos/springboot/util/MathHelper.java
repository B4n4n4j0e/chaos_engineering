package com.chaos.springboot.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathHelper {
    /**
     * Rounds double
     *
     * @param value ProductDTO to be evaluated
     * @return rounded double
     */
    public static double round(double value, int digits) {
        if (digits < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(digits, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
