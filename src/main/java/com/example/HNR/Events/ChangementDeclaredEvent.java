package com.example.HNR.Events;

import com.example.HNR.Model.enums.TypeExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangementDeclaredEvent {
    private Long changementId;
    private TypeExtension extensionType;
    private String detectedByUserId;
    private String prefecture;
    private String commune;
    private Long douarId;
}