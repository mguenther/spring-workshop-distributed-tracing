package workshop.spring.security.resources.filter;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws IOException, ServletException {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        if (hasBearerToken(authorizationHeader)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        UsernamePasswordAuthenticationToken token = createToken(authorizationHeader);

        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean hasBearerToken(String authorizationHeader) {
        return authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ");
    }

    private UsernamePasswordAuthenticationToken createToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return parseToken(token);
    }


    private UsernamePasswordAuthenticationToken parseToken(String token) {
        JWSObject jws;

        try {
            jws = JWSObject.parse(token);

            // Obviously we usually have to verify the signature against the JWKs of the issuer
            // but for this demo we skip this part
            var claims = JWTClaimsSet.parse(jws.getPayload().toJSONObject());
            return new UsernamePasswordAuthenticationToken(
                    new OAuth2User(claims.getSubject(), claims.getIssuer()),
                    null,
                    scopesToAuthorities(claims.getStringListClaim("scope")));

        } catch (ParseException e) {
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    private List<GrantedAuthority> scopesToAuthorities(List<String> scopes) {
        if (scopes == null) {
            return new ArrayList<>();
        }

        return scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
