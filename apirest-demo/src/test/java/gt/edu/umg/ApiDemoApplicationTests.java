package gt.edu.umg;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

@SpringBootTest
class ApiDemoApplicationTests {

	private static final KeyPair KEY_PAIR = generateKeyPair();

	@DynamicPropertySource
	static void testProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", () -> "jdbc:h2:mem:clinic;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
		registry.add("spring.datasource.username", () -> "sa");
		registry.add("spring.datasource.password", () -> "");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
		registry.add("spring.flyway.enabled", () -> "false");
		registry.add("rsa.public-key-base64", ApiDemoApplicationTests::encodedPublicKey);
	}

	@Test
	void contextLoads() {
	}

	private static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			return generator.generateKeyPair();
		} catch (Exception exception) {
			throw new IllegalStateException("Could not generate the test RSA key", exception);
		}
	}

	private static String encodedPublicKey() {
		String pem = "-----BEGIN PUBLIC KEY-----\n"
				+ Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(KEY_PAIR.getPublic().getEncoded())
				+ "\n-----END PUBLIC KEY-----\n";
		return Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.UTF_8));
	}

}
