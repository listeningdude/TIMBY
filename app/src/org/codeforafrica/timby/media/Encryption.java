package org.codeforafrica.timby.media;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.codeforafrica.timby.AppConstants;

public class Encryption  {
  public static void main(String[] args) throws Exception{
    Cipher cipher = createCipher(Cipher.ENCRYPT_MODE);
    applyCipher("file_to_encrypt", "encrypted_file", cipher);

    cipher = createCipher(Cipher.DECRYPT_MODE);
    applyCipher("file_to_decrypt", "decrypted_file", cipher);
  }
  

  
  public static Cipher createCipher(int mode) throws Exception {
    PBEKeySpec keySpec = new PBEKeySpec("test".toCharArray());
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(AppConstants.ENCRYPTION_KEY);
    SecretKey key = keyFactory.generateSecret(keySpec);
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update("input".getBytes());
    byte[] digest = md.digest();
    byte[] salt = new byte[8];
    for (int i = 0; i < 8; ++i)
      salt[i] = digest[i];
    PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);
    Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
    cipher.init(mode, key, paramSpec);
    return cipher;
  }

  public static void applyCipher(String inFile, String outFile, Cipher cipher) throws Exception {
    CipherInputStream in = new CipherInputStream(new FileInputStream(inFile), cipher);
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
    int BUFFER_SIZE = 8;
    byte[] buffer = new byte[BUFFER_SIZE];
    int numRead = 0;
    do {
      numRead = in.read(buffer);
      if (numRead > 0)
        out.write(buffer, 0, numRead);
    } while (numRead == 8);
    	out.close();
  }

}