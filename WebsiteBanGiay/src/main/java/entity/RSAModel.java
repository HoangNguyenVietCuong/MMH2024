package entity;
import javax.crypto.Cipher;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.util.Base64;

public class RSAModel {

    KeyPair keypair;
    private PrivateKey pvkey;
    private PublicKey pubkey;


    public KeyPair genkey() throws Exception{
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(1024);
        keypair = keygen.generateKeyPair();
        setPubkey(keypair.getPublic());
        setPvkey(keypair.getPrivate());
        return keypair;
    }

    public void savePrivateKey(String filename,PrivateKey pvkey) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(pvkey);
        }
    }

    public void savePublicKey(String filename,PublicKey pubkey) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(pubkey);
        }
    }
    public void loadPrivateKey(String filename) throws Exception {
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            setPvkey((PrivateKey) ois.readObject());
        }
    }


    public void loadPublicKey(String filename) throws Exception {
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            setPubkey((PublicKey) ois.readObject());
        }
    }

    public String encryptRSABase64(String data,PublicKey pubkey) throws Exception {
        return Base64.getEncoder().encodeToString(encryptRSA(data,pubkey));
    }
    public byte[] encryptRSA(String data,PublicKey pubkey) throws  Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        cipher.init(Cipher.ENCRYPT_MODE,pubkey);
        return cipher.doFinal(bytes);
    }

    public String decryptRSA(byte[] data,PrivateKey pvkey) throws  Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,pvkey);
        return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
    }

	public PrivateKey getPvkey() {
		return pvkey;
	}

	public void setPvkey(PrivateKey pvkey) {
		this.pvkey = pvkey;
	}

	public PublicKey getPubkey() {
		return pubkey;
	}

	public void setPubkey(PublicKey pubkey) {
		this.pubkey = pubkey;
	}

}