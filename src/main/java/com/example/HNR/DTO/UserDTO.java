package com.example.HNR.DTO;

import com.example.HNR.Model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userid;
    private String fullName;
    private String email;
    private Role role;
    private String prefecture;
    private String commune;
    private Date createdAt;

}