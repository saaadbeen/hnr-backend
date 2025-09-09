package com.example.HNR.Events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionCreatedEvent {
    private Long missionId;
    private String missionTitle;
    private String creatorUserId;
    private List<String> assignedUserIds;
    private String prefecture;
    private String commune;
    private String type;
}