package com.example.HNR.Model.enums;

public enum StatutDouar {
    ERADIQUE("Éradiqué"),
    NON_ERADIQUE("Non Éradiqué");

    private final String displayName;

    StatutDouar(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

