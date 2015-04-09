package au.com.windyroad.servicegateway;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceGatewayKeyStoreManager {

	public ServiceGatewayKeyStoreManager(String keyStore,
			String keyStorePassword, String keyPassword, String domainName)
			throws Exception {
		createKeyStore(keyStore, keyStorePassword, keyPassword, domainName);
	}

	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private void createKeyStore(String keyStore, String keyStorePassword,
			String keyPassword, String domainName) throws Exception {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		ks.load(null, keyStorePassword.toCharArray());
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA",
				"BC");
		keyPairGenerator.initialize(2048, new SecureRandom());
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		ks.setKeyEntry(
				"selfsigned",
				keyPair.getPrivate(),
				keyPassword.toCharArray(),
				new java.security.cert.Certificate[] { createSelfSignedCertificate(
						keyPair, domainName) });
		// Store away the keystore.
		FileOutputStream fos = new FileOutputStream(keyStore);
		ks.store(fos, keyStorePassword.toCharArray());
		fos.close();
	}

	static {
		// adds the Bouncy castle provider to java security
		Security.addProvider(new BouncyCastleProvider());
	}

	private Certificate createSelfSignedCertificate(KeyPair keyPair,
			String domainName) throws Exception {
		// generate a key pair

		// see
		// http://www.bouncycastle.org/wiki/display/JA1/X.509+Public+Key+Certificate+and+Certification+Request+Generation

		Date startDate = new Date();
		Date expiryDate = new Date(System.currentTimeMillis()
				+ (1000L * 60 * 60 * 24));
		BigInteger serialNumber = BigInteger.valueOf(Math
				.abs(new SecureRandom().nextInt())); // serial number for
														// certificate

		X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
		X500Principal dnName = new X500Principal("CN=" + domainName);
		certGen.setSerialNumber(serialNumber);
		certGen.setIssuerDN(dnName);
		certGen.setNotBefore(startDate);
		certGen.setNotAfter(expiryDate);
		certGen.setSubjectDN(dnName); // note: same as issuer
		certGen.setPublicKey(keyPair.getPublic());
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
		X509Certificate cert = certGen.generate(keyPair.getPrivate(), "BC");

		KeyStore ks = KeyStore.getInstance("JKS");
		File trustFile = new File("build/truststore.jks");
		ks.load(null, null);
		ks.setCertificateEntry("selfsigned", cert);
		FileOutputStream fos = new FileOutputStream(trustFile);
		ks.store(fos, "changeit".toCharArray());
		fos.close();

		return cert;
	}
}
