package org.example.security.dto;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class UserCredentials {
    private String username;
    private String password;
}
