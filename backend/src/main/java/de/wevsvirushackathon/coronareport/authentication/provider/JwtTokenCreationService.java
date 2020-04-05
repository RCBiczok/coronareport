package de.wevsvirushackathon.coronareport.authentication.provider;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.wevsvirushackathon.coronareport.authentication.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * A class to create JWT token holding quarano specific user information
 * @author Patrick Otto
 *
 */
@Component
public class JwtTokenCreationService {

    private String secret;

    private Long expiration;
    private String roleClaimAttribute;

    public JwtTokenCreationService(@Value("${jwt.authentication"
    		+ ".secret}") String secret,
                           @Value("${jwt.provider.expiration}") Long expiration,
                           @Value("${jwt.authentication.claim.role}") String roleClaimAttribute) {
        this.secret = secret;
        this.expiration = expiration;
        this.roleClaimAttribute = roleClaimAttribute;
    }

    public String generateToken(String username, List<Role> roles ) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);
        
        // map roles to a list of rolenames
        Map<String, Object> claims = new HashMap<>();
        claims.put(roleClaimAttribute, roles.stream().map(Role::toString).collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }
}

