package com.example.HNR.Security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    //  Clé secrète pour signer et valider les tokens (ne jamais exposer en public)
    private final String SECRET_KEY = "test-test";

    //  Génère un token  contenant l'email attribut
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // identifiant du token (souvent l'email)
                .setIssuedAt(new Date()) // date de création
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // expire dans 10h
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // algorithme de signature + clé
                .compact(); // retourne le token final sous forme de String
    }

    //  Extrait l'email du token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //  Vérifie si le token n'est pas expiré(date)
    public boolean isTokenValid(String token) {
        return extractExpiration(token).after(new Date());
    }

    // Extrait la date d'expiration du token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 🎯 Méthode générique pour extraire n'importe quelle "claim"
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody());
    }
}