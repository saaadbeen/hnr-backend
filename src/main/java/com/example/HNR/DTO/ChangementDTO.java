package com.example.HNR.DTO;

import com.example.HNR.Model.enums.TypeExtension;
import java.util.Date;

public class ChangementDTO {
    public Long   changementId;
    public TypeExtension type;
    public Date   dateBefore;
    public Date   dateAfter;
    public Double surface;
    public Long   douarId;            // relation -> id
    public String detectedByUserId;
    public Date   createdAt;
    public Date   updatedAt;

    public String pdfUrl;
}
