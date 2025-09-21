package org.kiribyte.authservice.util;

import org.mindrot.jbcrypt.BCrypt;

public class TokenHash {
    public static String hashToken(String token) {
        return BCrypt.hashpw(token, BCrypt.gensalt());
    }

    public static boolean verifyToken(String token, String hashedToken) {
        return BCrypt.checkpw(token, hashedToken);
    }
}
