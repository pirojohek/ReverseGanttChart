package by.pirog.ReverseGanttChart.security.deserializer;

import by.pirog.ReverseGanttChart.security.enums.TokenType;
import by.pirog.ReverseGanttChart.security.token.Token;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;


import java.text.ParseException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class TokenCookieJweStringDeserializer implements Function<String, Token> {

    private final JWEDecrypter jweDecrypter;

    public TokenCookieJweStringDeserializer(JWEDecrypter jweDecrypter) {
        this.jweDecrypter = jweDecrypter;
    }

    @Override
    public Token apply(String s) {
        try{
            var encryptedJwt = EncryptedJWT.parse(s);
            encryptedJwt.decrypt(this.jweDecrypter);

            var claimsSet = encryptedJwt.getJWTClaimsSet();
            List<String> authorities = claimsSet.getStringListClaim("authorities");

            return new Token(UUID.fromString(claimsSet.getJWTID()), claimsSet.getSubject(),
                    authorities, claimsSet.getLongClaim("projectId"),
                    TokenType.valueOf(claimsSet.getClaimAsString("tokenType")),
                    claimsSet.getIssueTime().toInstant(), claimsSet.getExpirationTime().toInstant());

        } catch (ParseException | JOSEException exception) {
            throw new RuntimeException(exception);
        }
    }
}
