package mechat.group.security;

import io.jsonwebtoken.*;
import mechat.group.entity.Role;
import mechat.group.model.Result;
import mechat.group.model.ResultSucces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class JwtTokenProvider {
    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.validity}")
    private Long validityMilliSecond;

    @Autowired
    private UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }


    public String createToken(String username, Set<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityMilliSecond);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJwt = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            if (claimsJwt.getBody().getExpiration().before(new Date())) {
                return false;
            }
            return true;
        } catch (
                SignatureException e) {
//            logger.error("Invalid JWT signature -> Message: {} ", e.getMessage());
        } catch (MalformedJwtException e) {
//            logger.error("Invalid JWT token -> Message: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
//            logger.error("Expired JWT token -> Message: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
//            logger.error("Unsupported JWT token -> Message: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
//            logger.error("JWT claims string is empty -> Message: {}", e.getMessage());
        }
        return false;
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUser(token));
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (Exception e) {
            return null;
        }
    }

    public String getUser(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

}
