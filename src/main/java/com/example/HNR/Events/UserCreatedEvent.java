package com.example.HNR.Events;

import com.example.HNR.Model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String newUserId;
    private String newUserName;
    private String newUserEmail;
    private Role newUserRole;
    private String prefecture;
    private String commune;
    private String creatorUserId;
}