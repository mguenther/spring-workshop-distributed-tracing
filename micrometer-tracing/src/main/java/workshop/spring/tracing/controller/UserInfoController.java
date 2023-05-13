package workshop.spring.tracing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RequestMapping("/user")
@RestController
public class UserInfoController {

    private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PostAuthorize("returnObject.name.equals('user')")
    UserPermission helloUser(Authentication authentication,
                             @RequestHeader Map<String, String> headers) {
        log.info("Returning user object for user '{}'", authentication.getName());
        final var authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return new UserPermission(authentication.getName(), authorities);
    }
}


record UserPermission(String name, List<String> authorities) {
}