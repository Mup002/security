package tmdtdemo.tmdt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tokens")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    private String refreshToken;
    private String refreshExpiration;
    private boolean status;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
