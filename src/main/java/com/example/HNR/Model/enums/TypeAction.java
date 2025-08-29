package com.example.HNR.Model.enums;

public enum TypeAction {
    DEMOLITION("Démolition"),
    SIGNALEMENT("Signalement"),
    NON_DEMOLITION("Non Démolition");

    private final String displayName;

    TypeAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
