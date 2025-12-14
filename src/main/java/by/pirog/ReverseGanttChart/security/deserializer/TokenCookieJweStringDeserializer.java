package by.pirog.ReverseGanttChart.security.deserializer;

import by.pirog.ReverseGanttChart.exception.ExpiredTokenException;
import by.pirog.ReverseGanttChart.enums.TokenType;
import by.pirog.ReverseGanttChart.security.token.Token;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;


import java.text.ParseException;
import java.util.Date;
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

            Date now = new Date();
            if (claimsSet.getExpirationTime() != null && claimsSet.getExpirationTime().before(now)) {
                throw new ExpiredTokenException("Token is expired");
            }

            return new Token(UUID.fromString(claimsSet.getJWTID()), claimsSet.getSubject(),
                    authorities, claimsSet.getLongClaim("projectId"),
                    TokenType.valueOf(claimsSet.getClaimAsString("tokenType")),
                    claimsSet.getIssueTime().toInstant(), claimsSet.getExpirationTime().toInstant());

        } catch (ParseException | JOSEException exception) {
            throw new RuntimeException(exception);
        }
    }
}
