package org.example.service.approaches.feignclient.dto;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class UserCredentials {
    private String username;
    private String password;
}
