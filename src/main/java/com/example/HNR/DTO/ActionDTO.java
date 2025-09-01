package com.example.HNR.DTO;

import com.example.HNR.Model.enums.TypeAction;
import lombok.*;

import java.util.Date;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ActionDTO {
    public Long actionId;
    public TypeAction type;
    public String prefecture;
    public String commune;
    public Long douarId;           // relation -> id
    public Long missionId;
    public Long pvId;              // relation -> id
    public String userId;
    public Date dateAction;
    public Date createdAt;
    public Date updatedAt;
    private String photoAvantUrl;
    private String photoApresUrl;
}
