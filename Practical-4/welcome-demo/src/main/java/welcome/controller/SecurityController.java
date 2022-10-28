package welcome.controller;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public class SecurityController {

    private final String CIPHER_INSTANCE = "RSA";
    private final Integer CIPHER_SIZE = 4096;
    private final String KEY_DIRECTORY = "src/main/resources/.ssl/";
    private final String PUBLIC_KEY_PATH = KEY_DIRECTORY + "public_key.pem";
    private final String PRIVATE_KEY_PATH = KEY_DIRECTORY + "private_key.pem";

    private final byte[] API_KEY_CLIENT_FULL;
    private final byte[] API_KEY_CLIENT_RESTRICTED;

    /**
     * This controller handles the encryption and decryption of data,
     * as well as generating and validating API keys.
     */
    public SecurityController() {
        generateKeyPair();
        this.API_KEY_CLIENT_FULL = encrypt(createApiKey());
        this.API_KEY_CLIENT_RESTRICTED = encrypt(createApiKey());
    }

    public String getAPI_KEY_CLIENT_FULL() {
        return decrypt(API_KEY_CLIENT_FULL);
    }

    public String getAPI_KEY_CLIENT_RESTRICTED() {
        return decrypt(API_KEY_CLIENT_RESTRICTED);
    }

    /**
     * This will generate a pseudorandom API key on each call.
     * @return an API token string.
     */
    public String createApiKey() {
        String uuid = UUID.randomUUID().toString();
        String salt = String.valueOf(System.currentTimeMillis() / 1000L);
        String key = uuid + salt;
        return Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * This method will check if an API token is a valid full or restricted instance.
     * @param auth the API token to check.
     * @return a boolean value to its overall validity.
     */
    public Boolean validateApiKey(String auth) {
        return validateApiKeyFullAccess(auth) || validateApiKeyRestrictedAccess(auth);
    }

    /**
     * This method will check if an API token is the full instance.
     * @param auth the API token to check.
     * @return a boolean value to its full access validity.
     */
    public Boolean validateApiKeyFullAccess(String auth) {
        return Objects.equals(auth, decrypt(API_KEY_CLIENT_FULL));
    }

    /**
     * This method will check if an API token is the restricted instance.
     * @param auth the API token to check.
     * @return a boolean value to its restricted validity.
     */
    public Boolean validateApiKeyRestrictedAccess(String auth) {
        return Objects.equals(auth, decrypt(API_KEY_CLIENT_RESTRICTED));
    }

    /**
     * This method will get the file as a byte array from a given filepath.
     * @param filePath The path to the file which should be read.
     * @return a byte array of the file data.
     */
    private byte[] getFile(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            byte[] keyBytes = new byte[(int) file.length()];
            dataInputStream.readFully(keyBytes);
            dataInputStream.close();

            return keyBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will get the public key from the specified path {@link #PUBLIC_KEY_PATH}.
     * @return a RSAPublicKey.
     */
    private RSAPublicKey getPublicKey() {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getFile(PUBLIC_KEY_PATH));
            KeyFactory keyFactory = KeyFactory.getInstance(CIPHER_INSTANCE);

            return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will get the generated private key from the specified path {@link #PRIVATE_KEY_PATH}.
     * @return a RSAPrivateKey.
     */
    private RSAPrivateKey getPrivateKey() {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(getFile(PRIVATE_KEY_PATH));
            KeyFactory keyFactory = KeyFactory.getInstance(CIPHER_INSTANCE);

            return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will generate a key pair that will be output to two respective files for public
     * and private keys.
     */
    private void generateKeyPair() {
        try {
            // Generate key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CIPHER_INSTANCE);
            keyPairGenerator.initialize(CIPHER_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Create directory if it doesn't exist.
            Files.createDirectories(Path.of(KEY_DIRECTORY));

            // Write key pair to file
            FileOutputStream privateFileOutputStream = new FileOutputStream(PRIVATE_KEY_PATH);
            privateFileOutputStream.write(keyPair.getPrivate().getEncoded());
            privateFileOutputStream.close();

            FileOutputStream publicFileOutputStream = new FileOutputStream(PUBLIC_KEY_PATH);
            publicFileOutputStream.write(keyPair.getPublic().getEncoded());
            publicFileOutputStream.close();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This will take a message and encrypt it.
     * @param message a plaintext message to encrypt.
     * @return The encrypted message using the {@link #CIPHER_INSTANCE} scheme.
     */
    private byte[] encrypt(String message) {
        try {
            Cipher encrypt = Cipher.getInstance(CIPHER_INSTANCE);
            encrypt.init(Cipher.ENCRYPT_MODE, getPublicKey());
            return encrypt.doFinal(message.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This will take an encrypted byte array and decrypt it to give the string representation.
     * @param encrypted a {@link #CIPHER_INSTANCE} encrypted byte array.
     * @return The decrypted plaintext message.
     */
    private String decrypt(byte[] encrypted) {
        try {
            Cipher decrypted = Cipher.getInstance(CIPHER_INSTANCE);
            decrypted.init(Cipher.DECRYPT_MODE, getPrivateKey());

            return new String(decrypted.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
