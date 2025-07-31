package com.example.HNR.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;

@Document(collection = "changements")
@Data
@AllArgsConstructor //  constructeur avec tous les arguments
@NoArgsConstructor  // constructeur sans argument
@Builder
public class Changement {
    @Id
    private String id;

    private TypeExtension type;

    private Date date;

    private String idphotoAvant;
    private String idphotoApres;

    private double surface;
    private String idDouar;
}
