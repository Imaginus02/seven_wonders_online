package com.reynaud.wonders.model;

public enum Age {
    AGE_I,
    AGE_II,
    AGE_III;

    public static Age getNextAge(Age currentAge) {
        if (currentAge == null) {
            return AGE_I;
        }
        switch (currentAge) {
            case AGE_I:
                return AGE_II;
            case AGE_II:
                return AGE_III;
            case AGE_III:
                return null; // No next age after AGE_III
            default:
                return null;
        }
    }
}