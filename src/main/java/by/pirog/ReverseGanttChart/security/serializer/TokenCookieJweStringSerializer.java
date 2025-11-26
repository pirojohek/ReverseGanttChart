package by.pirog.ReverseGanttChart.security.serializer;

import by.pirog.ReverseGanttChart.security.token.Token;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Setter;

import java.util.Date;
import java.util.function.Function;

public class TokenCookieJweStringSerializer implements Function<Token, String> {

    private final JWEEncrypter jweEncrypter; // алгоритм симметрического шифрования

    @Setter
    private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;

    @Setter
    private EncryptionMethod encryptionMethod = EncryptionMethod.A128GCM;

    public TokenCookieJweStringSerializer(JWEEncrypter jweEncrypter) {
        this.jweEncrypter = jweEncrypter;
    }

    public TokenCookieJweStringSerializer(JWEEncrypter jweEncrypter, JWEAlgorithm jweAlgorithm, EncryptionMethod encryptionMethod) {
        this.jweEncrypter = jweEncrypter;
        this.jweAlgorithm = jweAlgorithm;
        this.encryptionMethod = encryptionMethod;
    }

    @Override
    public String apply(Token token) {
        var jweHeader = new JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod)
                .keyID(token.id().toString())
                .build();

        var claimsSet = new JWTClaimsSet.Builder()
                .jwtID(token.id().toString())
                .subject(token.subject())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiresAt()))
                .claim("authorities", token.authorities())
                .claim("projectId", token.projectId())
                .claim("tokenType", token.tokenType())
                .build();

        var encryptedJWT = new EncryptedJWT(jweHeader, claimsSet);
        try {
            encryptedJWT.encrypt(this.jweEncrypter);
            return encryptedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
            // Todo подумать какая здесь ошибка должна быть
        }
    }

}
