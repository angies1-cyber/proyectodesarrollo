package gt.edu.umg.auth.util;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RsaKeyGeneratorUtil {

    public static void main(String[] args) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                    Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(privateKey.getEncoded()) +
                    "\n-----END PRIVATE KEY-----";

            String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(publicKey.getEncoded()) +
                    "\n-----END PUBLIC KEY-----";

            File authCertsDir = new File("src/main/resources/certs");
            if (!authCertsDir.exists()) authCertsDir.mkdirs();

            try (FileOutputStream out = new FileOutputStream(new File(authCertsDir, "private.pem"))) {
                out.write(privateKeyPem.getBytes());
            }
            try (FileOutputStream out = new FileOutputStream(new File(authCertsDir, "public.pem"))) {
                out.write(publicKeyPem.getBytes());
            }

            File demoCertsDir = new File("../apirest-demo/src/main/resources/certs");
            if (!demoCertsDir.exists()) demoCertsDir.mkdirs();

            try (FileOutputStream out = new FileOutputStream(new File(demoCertsDir, "public.pem"))) {
                out.write(publicKeyPem.getBytes());
            }

            System.out.println("✅ Llaves RSA generadas exitosamente en ambos proyectos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
