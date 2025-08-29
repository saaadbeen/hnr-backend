package com.example.HNR.Model.enums;

public enum Role {
    AGENT_AUTORITE("Agent d'Autorité"),
    MEMBRE_DSI("Membre DSI"),
    GOUVERNEUR("Gouverneur");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}