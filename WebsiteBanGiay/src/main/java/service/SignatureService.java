package service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import dao.DAO;

public class SignatureService {
	private DAO dao;

	public SignatureService(DAO dao) {
		this.dao = dao;

	}

	public boolean verifySig(String data, String signatureBase64, PublicKey pubKey) {
		if (pubKey == null) {
			return false; // Không tìm thấy public key
		}

		try {
			// Giải mã chữ ký từ Base64 thành mảng byte
			byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

			// Tạo đối tượng Signature với thuật toán RSA
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(pubKey);
			signature.update(data.getBytes("UTF-8"));

			// Kiểm tra chữ ký
			return signature.verify(signatureBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static PublicKey getPublicKeyFromBase64(String publicKeyBase64) {
		try {
			// Giải mã Base64 thành mảng byte
			byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

			// Tạo đối tượng X509EncodedKeySpec từ mảng byte
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

			// Sử dụng KeyFactory để tạo PublicKey từ X509EncodedKeySpec
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
