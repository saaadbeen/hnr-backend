package com.example.HNR.Events;

import com.example.HNR.Model.enums.TypeAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionCreatedEvent {
    private Long actionId;
    private TypeAction actionType;
    private String actionUserId;
    private String prefecture;
    private String commune;
    private Long douarId;
    private Long missionId;
}