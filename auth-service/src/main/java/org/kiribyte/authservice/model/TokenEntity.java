package org.kiribyte.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens", schema = "auth_service_schema")
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "token")
    private String token;
    @Column(name = "expired_at")
    private Date expiredAt;
}
