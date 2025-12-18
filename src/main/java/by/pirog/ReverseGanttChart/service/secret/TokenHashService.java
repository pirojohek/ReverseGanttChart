package by.pirog.ReverseGanttChart.service.secret;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenHashService {

    private static final SecureRandom random = new SecureRandom();

    @Value("${app.security.algorithm}")
    private static String algorithm = "SHA-256";

    public static String generateToken(int sizeInBytes){
        byte[] bytes = new byte[sizeInBytes];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Todo алгоритм вынести в application.yml
    public static String hashToken(String token){
        try{
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyToken(String token, String storedHash)
            throws NoSuchAlgorithmException {
        String computedHash = hashToken(token);
        return computedHash.equals(storedHash);
    }
}
