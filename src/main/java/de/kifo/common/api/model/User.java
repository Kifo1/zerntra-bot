package de.kifo.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private Long id;
    private String userName;
    private String password;
    private Long lastOnTime;

}
