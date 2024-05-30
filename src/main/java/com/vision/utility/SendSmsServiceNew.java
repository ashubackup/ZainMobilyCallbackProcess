package com.vision.utility;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class SendSmsServiceNew {
	
	public void sendMessage()
	{
		
	}
	
	public String generateRandomPassword(int length) {
		 String ALL_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";
	        if (length <= 0) {
	            throw new IllegalArgumentException("Password length must be greater than 0");
	        }

	        SecureRandom random = new SecureRandom();
	        StringBuilder password = new StringBuilder();

	        for (int i = 0; i < length; i++) {
	            int randomIndex = random.nextInt(ALL_CHARS.length());
	            password.append(ALL_CHARS.charAt(randomIndex));
	        }

	        return password.toString();
	    }

}
