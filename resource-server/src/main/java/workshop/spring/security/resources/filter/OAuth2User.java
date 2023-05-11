package workshop.spring.security.resources.filter;

public class OAuth2User {

    private final String name;

    private final String issuer;

    public OAuth2User(String name, String issuer) {
        this.name = name;
        this.issuer = issuer;
    }

    public String getName() {
        return name;
    }

    public String getIssuer() {
        return issuer;
    }
}
