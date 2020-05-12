package br.com.simob.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SegurancaUtil {
	
	private static final String HEXADECIMAL = "SHA-256";
	
	public static String criptografaSenha(String senha) 
		throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest algoritimo = MessageDigest.getInstance(HEXADECIMAL);
		byte[] senhaBytes = algoritimo.digest(senha.getBytes("UTF8"));
				
		StringBuilder senhaCriptografada = new StringBuilder();
		for(byte b : senhaBytes) {
			senhaCriptografada.append(String.format("%02X", 0xFF & b));
		}
		return senhaCriptografada.toString(); 
	}
}
