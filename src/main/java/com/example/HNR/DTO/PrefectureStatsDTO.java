package com.example.HNR.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrefectureStatsDTO {
    private String prefecture;
    private long totalDouars;
    private long douarsEradiques;
    private long totalActions;
    private long totalChangements;
    private double pourcentageEradication;
}