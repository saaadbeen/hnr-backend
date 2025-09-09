package com.example.HNR.Config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    // ⚠️ fournis un vrai secret via properties/env; défaut seulement pour dev
    @Value("${jwt.secret:myDefaultSecretKeyForJwtTokenGenerationThatShouldBeAtLeast64Characters_LongEnough_ForHS512!}")
    private String secret;

    // secondes (3600 = 1h). Ne mets PAS des millisecondes ici.
    @Value("${jwt.expiration:3600}")
    private long jwtExpirationInSeconds;

    private SecretKey getSigningKey() {
        byte[] keyBytes;

        // Support "base64:<valeur>"
        if (secret != null && secret.startsWith("base64:")) {
            keyBytes = Decoders.BASE64.decode(secret.substring("base64:".length()));
        } else {
            keyBytes = (secret != null ? secret : "").getBytes(StandardCharsets.UTF_8);
        }

        // HS512 => ≥ 64 octets (512 bits)
        if (keyBytes.length < 64) {
            String msg = "JWT secret too short for HS512: need >= 64 bytes, got " + keyBytes.length +
                    ". Provide a stronger key (e.g. base64:...)";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getUserIdFromToken(String token) { // alias
        return getUsernameFromToken(token);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> (String) claims.get("role"));
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String username) {
        return createToken(new HashMap<>(), username);
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        if (role != null && !role.isBlank()) claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (jwtExpirationInSeconds * 1000L));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    public boolean validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
        return !isTokenExpired(token);
    }

    public long getExpirationTimeInSeconds(String token) {
        Date expiration = getExpirationDateFromToken(token);
        long delta = (expiration.getTime() - System.currentTimeMillis()) / 1000L;
        return Math.max(0, delta);
    }

    public boolean isTokenValidAndNotExpired(String token) {
        return validateToken(token) && !isTokenExpired(token);
    }
}
