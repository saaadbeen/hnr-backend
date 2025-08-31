package com.example.HNR.DTO;

import com.example.HNR.Model.enums.TypeAction;
import lombok.Data;
import java.util.Date;

@Data
public class ActionDTO {
    private Long id;
    private String titre;
    private String description;
    private TypeAction type;
    private String prefecture;
    private String commune;
    public Long douarId;           // relation -> id
    public Long missionId;
    public Long pvId;              // relation -> id

    public String userId;
    private Date createdAt;
    private Date updatedAt;
}
