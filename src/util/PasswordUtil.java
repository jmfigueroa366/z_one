package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para encriptar contraseñas usando SHA-256.
 * NUNCA se guarda la contraseña en texto plano en la base de datos.
 *
 * @author Equipo Z-One
 */
public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Convierte una contraseña en texto plano a su hash SHA-256.
     *
     * @param password Contraseña en texto plano
     * @return Hash SHA-256 en formato hexadecimal (64 caracteres)
     * @throws RuntimeException si el algoritmo SHA-256 no está disponible
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash: " + e.getMessage());
        }
    }

    /**
     * Verifica si una contraseña coincide con su hash almacenado.
     *
     * @param password   Contraseña ingresada por el usuario
     * @param hashGuardado Hash SHA-256 almacenado en la BD
     * @return true si coinciden, false en caso contrario
     */
    public static boolean verificar(String password, String hashGuardado) {
        return hashPassword(password).equals(hashGuardado);
    }
}
