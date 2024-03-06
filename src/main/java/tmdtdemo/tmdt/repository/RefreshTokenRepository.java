package tmdtdemo.tmdt.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tmdtdemo.tmdt.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    RefreshToken findRefreshTokenByUserId(Long userId);
    RefreshToken findRefreshTokenByRefreshToken(String refreshToken);
}
