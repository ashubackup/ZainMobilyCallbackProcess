package com.vision.utility;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


import com.vision.entity.ServiceInfo;
import com.vision.entity.TblSmsLogs;
import com.vision.entity.TblSubscriptionLogs;
import com.vision.repo.TblSmsLogsRepo;
import com.vision.repo.TblSubscriptionLogsRepo;

@Service
public class UtilityService2 {
	


	@Autowired
	private TblSmsLogsRepo smsLogsRepo;
	@Autowired
	private UtilityService utilityService;
	
	@Autowired
	private TblSubscriptionLogsRepo subLogsRepo;
	

	
	//private static String requestId;
	
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface MethodOverloadingInfo {
	    String value() default ""; 
	}
	
	
	
	@MethodOverloadingInfo("This method will build signature for request pin and subscription")
	public String buildSignature(String apiKey, String apiSecret, String applicationId, String countryId,
	        String operatorId, String cpId, String msisdn, String timestamp,
	        String lang, String shortcode, String method)
	        throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
		
		System.out.println("api key --"+ apiKey);
		System.out.println("api key --"+ apiSecret);
		
		System.out.println("Application id " + applicationId);
		System.out.println("Country id " + countryId);
		System.out.println("Operator id " + operatorId);
		System.out.println("Cpid " + cpId);
		System.out.println("Msisdn " + msisdn);
		System.out.println("time " + timestamp);
		System.out.println("Lang " + lang);
		System.out.println("Short code " + shortcode);                          
		System.out.println("method "+ method);
		
		
	    StringBuilder decryptedSignature = new StringBuilder();

	    decryptedSignature.append("ApiKey=").append(customURLEncoder(apiKey)).append("&");
	    decryptedSignature.append("ApiSecret=").append(customURLEncoder(apiSecret)).append("&");
	    decryptedSignature.append("ApplicationId=").append(customURLEncoder(applicationId.toString())).append("&");
	    decryptedSignature.append("CountryId=").append(customURLEncoder(countryId.toString())).append("&");
	    decryptedSignature.append("OperatorId=").append(customURLEncoder(operatorId.toString())).append("&");
	    decryptedSignature.append("CpId=").append(customURLEncoder(cpId.toString())).append("&");
	    decryptedSignature.append("MSISDN=").append(customURLEncoder(msisdn.toUpperCase())).append("&");
	    decryptedSignature.append("Timestamp=").append(customURLEncoder(timestamp.toUpperCase())).append("&");
	    decryptedSignature.append("Lang=").append(customURLEncoder(lang.toUpperCase())).append("&");
	    decryptedSignature.append("ShortCode=").append(customURLEncoder(shortcode.toUpperCase())).append("&");
	    decryptedSignature.append("Method=").append(customURLEncoder(method));
	    
	    System.out.println("Signature of request pin :--- " + decryptedSignature);

	    // Uncomment the following block if channelId is required
	    // if (channelId != null) {
	    //     decryptedSignature.append("&channelid=").append(URLEncoder.encode(channelId, "UTF-8"));
	    // }
	   

	   // String encryptedSignature = hmacService.hmacSha256(apiSecret, decryptedSignature.toString());
	    String encryptedSignature = calculateHMACSHA256(apiSecret, decryptedSignature.toString());
	    System.out.println("Encrypted Signature----> " + encryptedSignature);
	    return encryptedSignature;
	}
	
	
	@MethodOverloadingInfo("This method will build signature for valid pin")
	public String buildSignature(String apiKey, String apiSecret, String applicationId, String countryId,
	        String operatorId, String cpId, String msisdn, String timestamp,
	        String lang, String shortcode, String code, String method)
	        throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
		

	    StringBuilder decryptedSignature = new StringBuilder();

	    decryptedSignature.append("ApiKey=").append(customURLEncoder(apiKey)).append("&");
	    decryptedSignature.append("ApiSecret=").append(customURLEncoder(apiSecret)).append("&");
	    decryptedSignature.append("ApplicationId=").append(customURLEncoder(applicationId.toString())).append("&");
	    decryptedSignature.append("CountryId=").append(customURLEncoder(countryId.toString())).append("&");
	    decryptedSignature.append("OperatorId=").append(customURLEncoder(operatorId.toString())).append("&");
	    decryptedSignature.append("CpId=").append(customURLEncoder(cpId.toString())).append("&");
	    decryptedSignature.append("MSISDN=").append(customURLEncoder(msisdn.toUpperCase())).append("&");
	    decryptedSignature.append("Timestamp=").append(customURLEncoder(timestamp.toUpperCase())).append("&");
	    decryptedSignature.append("Lang=").append(customURLEncoder(lang.toUpperCase())).append("&");
	    decryptedSignature.append("ShortCode=").append(customURLEncoder(shortcode.toUpperCase())).append("&");
	    decryptedSignature.append("Code=").append(customURLEncoder(code.toUpperCase())).append("&");
	    decryptedSignature.append("Method=").append(customURLEncoder(method));

	    System.out.println("Signature of valid pin:----- " + decryptedSignature);

	    // Uncomment the following block if channelId is required
	    // if (channelId != null) {
	    //     decryptedSignature.append("&channelid=").append(URLEncoder.encode(channelId, "UTF-8"));
	    // }
	    // calculateHMACSHA256
	    String encryptedSignature = calculateHMACSHA256(apiSecret, decryptedSignature.toString());
	    return encryptedSignature;
	}
	
	public String buildSignatureForSms(String apiKey, String apiSecret, String applicationId, String countryId,
	        String operatorId, String cpId, String msisdn, String timestamp,
	        String lang, String shortcode, String message, String method)
	        throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

	    StringBuilder decryptedSignature = new StringBuilder();

	    decryptedSignature.append("ApiKey=").append(customURLEncoder(apiKey)).append("&");
	    decryptedSignature.append("ApiSecret=").append(customApiSecretURLEncoder(apiSecret)).append("&");
	    decryptedSignature.append("ApplicationId=").append(customURLEncoder(applicationId.toString())).append("&");
	    decryptedSignature.append("CountryId=").append(customURLEncoder(countryId.toString())).append("&");
	    decryptedSignature.append("OperatorId=").append(customURLEncoder(operatorId.toString())).append("&");
	    decryptedSignature.append("CpId=").append(customURLEncoder(cpId.toString())).append("&");
	    decryptedSignature.append("MSISDN=").append(customURLEncoder(msisdn.toUpperCase())).append("&");
	    decryptedSignature.append("Timestamp=").append(customURLEncoder(timestamp.toUpperCase())).append("&");
	    decryptedSignature.append("Lang=").append(customURLEncoder(lang.toUpperCase())).append("&");
	    decryptedSignature.append("ShortCode=").append(customURLEncoder(shortcode.toUpperCase())).append("&");
	    decryptedSignature.append("MsgText=").append(customSmsURLEncoder(message.toUpperCase())).append("&");
	    decryptedSignature.append("Method=").append(customURLEncoder(method));

	    System.out.println("Signature of send sms:----- " + decryptedSignature);
	    
	    System.out.println("custom sms endocer " + customSmsURLEncoder(message.toUpperCase()));

	    // Uncomment the following block if channelId is required
	    // if (channelId != null) {
	    //     decryptedSignature.append("&channelid=").append(URLEncoder.encode(channelId, "UTF-8"));
	    // }

	    String encryptedSignature = calculateHMACSHA256(apiSecret, decryptedSignature.toString());
	    return encryptedSignature;
	}
	
	public String buildSignatureForSmsMobily(String apiKey, String apiSecret, String applicationId, String countryId,
	        String operatorId, String cpId, String msisdn, String timestamp,
	        String lang, String shortcode, String message, String method)
	        throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

	    StringBuilder decryptedSignature = new StringBuilder();

	    decryptedSignature.append("ApiKey=").append(customURLEncoder(apiKey)).append("&");
	    decryptedSignature.append("ApiSecret=").append(customURLEncoder(apiSecret)).append("&");
	    decryptedSignature.append("ApplicationId=").append(customURLEncoder(applicationId.toString())).append("&");
	    decryptedSignature.append("CountryId=").append(customURLEncoder(countryId.toString())).append("&");
	    decryptedSignature.append("OperatorId=").append(customURLEncoder(operatorId.toString())).append("&");
	    decryptedSignature.append("CpId=").append(customURLEncoder(cpId.toString())).append("&");
	    decryptedSignature.append("MSISDN=").append(customURLEncoder(msisdn.toUpperCase())).append("&");
	    decryptedSignature.append("Timestamp=").append(customURLEncoder(timestamp.toUpperCase())).append("&");
	    decryptedSignature.append("Lang=").append(customURLEncoder(lang.toUpperCase())).append("&");
	    decryptedSignature.append("ShortCode=").append(customURLEncoder(shortcode.toUpperCase())).append("&");
	    decryptedSignature.append("MsgText=").append(customSmsURLEncoder(message.toUpperCase())).append("&");
	    decryptedSignature.append("Method=").append(customURLEncoder(method));

	    System.out.println("Signature of send sms:----- " + decryptedSignature);
	    
	    System.out.println("custom sms endocer " + customSmsURLEncoder(message.toUpperCase()));

	    

	    String encryptedSignature = calculateHMACSHA256(apiSecret, decryptedSignature.toString());
	    return encryptedSignature;
	}
	
//	public String customURLEncoder(String value) throws UnsupportedEncodingException {
//	    return URLEncoder.encode(value, "UTF-8");
//	}
	public String customURLEncoder(String value) throws UnsupportedEncodingException {
	    return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20").replaceAll("%2B", "%2b");
	}
	
	public String customSmsURLEncoder(String value) throws UnsupportedEncodingException {
	    return URLEncoder.encode(value, "UTF-8");
	}
	
	public String customApiSecretURLEncoder(String value) throws UnsupportedEncodingException {
	    return URLEncoder.encode(value, "UTF-8").replaceAll("%2F", "%2f");
	}
	

	public String calculateHMACSHA256(String key, String data)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte[] hashedBytes = sha256HMAC.doFinal(data.getBytes("UTF-8"));
        return bytesToHex(hashedBytes);
    }
	public String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                hexStringBuilder.append('0');
            }
            hexStringBuilder.append(hex);
        }
        return hexStringBuilder.toString();
    }
	
//	@MethodOverloadingInfo("This method will build data in json format for request pin & Subscription")
//	public JSONObject buildJSON(ServiceInfo serviceInfo, WebInfo webInfo, String host, String signature, String dateTime)
//	{
//		
//		JSONObject json = new JSONObject();
//		json.put("MSISDN", webInfo.getMobileNumber());
//		json.put("apiKey", serviceInfo.getApiKey());
//		json.put("signature", signature);
//		json.put("cpId", serviceInfo.getCpId());
//		json.put("ipAddress", host);
//		json.put("shortcode", serviceInfo.getShortcode());
//		json.put("lpUrl", "https://kiddocraze.thehappytubes.com");
//		json.put("requestId", webInfo.getEvinaRequestId().toString());
//		json.put("timestamp", dateTime);
//		json.put("applicationId", serviceInfo.getApplicationId());
//		json.put("countryId", serviceInfo.getCountryId());
//		json.put("operatorId", serviceInfo.getOperatorId());
//		json.put("lang", webInfo.getLanguage().toLowerCase());
//		json.put("pubId", "");
//		json.put("adpartnername", "");
//		
//		return json;
//	}
	
//	@MethodOverloadingInfo("This method will build data in json format for valid pin")
//	public JSONObject buildJSON(ServiceInfo serviceInfo, WebInfo webInfo, String signature, String dateTime)
//	{
//		
//		JSONObject json = new JSONObject();
//		json.put("MSISDN", webInfo.getMobileNumber());
//		json.put("apiKey", serviceInfo.getApiKey());
//		json.put("signature", signature);
//		json.put("cpId", serviceInfo.getCpId());
//		json.put("shortcode", serviceInfo.getShortcode());
//		json.put("requestId", webInfo.getEvinaRequestId().toString());
//		json.put("timestamp", dateTime);
//		json.put("code", webInfo.getCode());
//		json.put("applicationId", serviceInfo.getApplicationId());
//		json.put("countryId", serviceInfo.getCountryId());
//		json.put("operatorId", serviceInfo.getOperatorId());
//		json.put("lang", webInfo.getLanguage().toLowerCase());
//		json.put("pubId", "");
//		json.put("adpartnername", "");
//		
//		return json;
//	}
	
	@MethodOverloadingInfo("This method will build data in json format for send sms")
	public JSONObject buildJSON(ServiceInfo serviceInfo, String msisdn, String language, String evinaRequestId, String signature, String message, String dateTime)
	{
		
		JSONObject json = new JSONObject();
		json.put("MSISDN", msisdn);
		json.put("apiKey", serviceInfo.getApiKey());
		json.put("signature", signature);
		json.put("cpId", serviceInfo.getCpId());
		json.put("shortcode", serviceInfo.getShortcode());
		json.put("requestId", evinaRequestId.toString());
		json.put("timestamp", dateTime);
		json.put("msgText", message);
		json.put("applicationId", serviceInfo.getApplicationId());
		json.put("countryId", serviceInfo.getCountryId());
		json.put("operatorId", serviceInfo.getOperatorId());
		json.put("lang", language.toLowerCase());
	
		return json;
	}
	
	
//	public String requestPinApiCall(JSONObject json)
//	{
//		System.out.println("Request pin json is : ------" + json);
//        RestTemplate restTemplate = new RestTemplate();
//        String apiUrl = "http://ksg.intech-mena.com/MSG/v1.1/API/RequestPinCode";
//        
//        System.out.println("MSISDN : " + json.get("MSISDN"));
//        
//        try {
//        	
//        	HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            //headers.set("Signature", json.getString("signature"));
//            
//           // System.out.println("New ------"+json.getString("signature"));
//    		//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//    		
//			HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
//			System.out.println("Request entity---: " + entity);
//
//	        ResponseEntity<String > responseEntity = restTemplate.postForEntity(apiUrl, entity, String.class);
//	        String body = responseEntity.getBody();
//	        
//	        System.out.println("Body ---: "+ body);
//	        System.out.println("ResponseEntity is ----:" + responseEntity);
//	        
//	        RequestPinLogs requestPin = new RequestPinLogs();
//	        requestPin.setMsisdn(json.getString("MSISDN"));
//	        requestPin.setDateTime(LocalDateTime.now());
//	        requestPin.setRequest(entity.toString()); 
//	        requestPin.setResponse(responseEntity.toString());
//	        requestPin.setStatusCode(responseEntity.getStatusCode().toString());
//	        requestPin.setStatus("0");
//	        System.out.println("Request Pin : " + requestPin);
//	        
//	        requestRepo.save(requestPin);
//	        
//	        if (responseEntity.getStatusCode().is2xxSuccessful()) {
//	            System.out.println("API call successful");
//	            if(body.contains("100") || body.contains("Your request has been processed successfully"))
//	            {
//	            	System.out.println("Inside request pin success ===========");
//	            	return "Success";
//	            }else if(body.contains("4") || body.contains("User already subscribed to the service")) {
//	            	return "4";
//	            }else if(body.contains("14") || body.contains("Not enough balance"))
//	            {
//	            	return "Not enough balance";
//	            }else if(body.contains("R3") || body.contains("You already requested a pin. Please try again later.")) {
//	            	return "You already requested a pin. Please try again later.";
//	            }else {
//	            	return "Something went wrong. Please try again.";
//	            }
//	        } else {
//	            System.out.println("API call failed");
//	            return "Failed";
//	        }
//		}catch (HttpServerErrorException.InternalServerError e) {
//		    // Log the details of the 500 Internal Server Error
//		    System.out.println("HTTP Status Code:---- " + e.getStatusCode());
//		    System.out.println("Response Body:---- " + e.getResponseBodyAsString());
//		    e.printStackTrace();
//		    
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        return "Failed";
//	}
	
//	public String validatePinApi(JSONObject json)
//	{
//        RestTemplate restTemplate = new RestTemplate();
//        String apiUrl = "http://ksg.intech-mena.com/MSG/v1.1/API/ValidatePinCode";
//        System.out.println("MSISDN : " + json.get("MSISDN"));
//        try {
//        	HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//    		//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//    		
//    		
//			HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
//			System.out.println("Request : " + entity);
//
//	        ResponseEntity<String > responseEntity = restTemplate.postForEntity(apiUrl, entity, String.class);
//	        String body = responseEntity.getBody();
//	        System.out.println("Body : "+ body);
//	        System.out.println("ResponseEntity is :" + responseEntity);
//	        
//	        ValidationPinLogs validPin = new ValidationPinLogs();
//	        validPin.setMsisdn(json.getString("MSISDN"));
//	        validPin.setDateTime(LocalDateTime.now());
//	        validPin.setRequest(entity.toString()); 
//	        validPin.setResponse(responseEntity.toString());
//	        validPin.setStatusCode(responseEntity.getStatusCode().toString());
//	        validPin.setPinCode(json.getString("code"));
//	        validPin.setStatus("0");
//	        System.out.println("Request Pin : " + validPin);
//	        
//	        validRepo.save(validPin);
//	        
//	        if (responseEntity.getStatusCode().is2xxSuccessful()) 
//	        {
//	        	System.out.println("API call successful");
//	        	 if(body.contains("100") || body.contains("Your request has been processed successfully"))
//	        	 {
//	        		System.out.println("Inside validPin success ===========");
//		            return "Success";
//		         }else if(body.contains("4") || body.contains("User already subscribed to the service")) {
//		            	return "4";
//	             }else if(body.contains("14") || body.contains("Not enough balance")){
//	            	return "Not enough balance";
//	             }else if(body.contains("R3") || body.contains("You already requested a pin. Please try again later.")) {
//	            	return "You already requested a pin. Please try again later.";
//	             }else {
//	            	return "Something went wrong. Please try again.";
//	             }
//	        } else {
//	            System.out.println("API call failed");
//	            return "Failed";
//	        }
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        return "Failed";
//	}
	
	
	public String subscriptionApiCall(JSONObject json)
	{
		System.out.println("Request pin json is : ------" + json);
        RestTemplate restTemplate = new RestTemplate();
       
        String apiUrl = "http://ksg.intech-mena.com/MSG/v1.1/API/Subscribe";
        
        System.out.println("MSISDN : " + json.get("MSISDN"));
        
        try {
        	
        	HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            //headers.set("Signature", json.getString("signature"));
            
           // System.out.println("New ------"+json.getString("signature"));
    		//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    		
			HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
			System.out.println("Request entity---: " + entity);

	        ResponseEntity<String > responseEntity = restTemplate.postForEntity(apiUrl, entity, String.class);
	        String body = responseEntity.getBody();
	        
	        System.out.println("Body for sub request  ---: "+ body);
	        
	        System.out.println("ResponseEntity is  sub request----:" + responseEntity);
	        
	        TblSubscriptionLogs subRequest = new TblSubscriptionLogs();
	        subRequest.setMsisdn(json.getString("MSISDN"));
	        subRequest.setDateTime(LocalDateTime.now());
	        subRequest.setRequest(entity.toString()); 
	        subRequest.setResponse(responseEntity.toString());
	        subRequest.setStatus("0");
	        System.out.println("Request Pin : " + subRequest);
	        
	        subLogsRepo.save(subRequest);
	        
	        if (responseEntity.getStatusCode().is2xxSuccessful()) {
	            System.out.println("API call successful");
//	            if(body.contains("SUBSCRIBE") || body.contains("OK"))
//	            {
//	            	System.out.println("Save in tblSub ===========");
//	            	return "Ok";
//	            }else if(body.contains("SUBSCRIBE") || body.contains("DELIVERED")) {
//	            	System.out.println("Save in tblbillingSuccess ===========");
//	            	return "DELIVERED";
//	            }else if(body.contains("SUBSCRIBE ") || body.contains("FAILED"))
//	            {
//	            	return "FAILED";
//	            }else {
//	            	return "Something went wrong. Please try again.";
//	            }
	            return"Success";
	            
	            
	        } else {
	            System.out.println("API call failed");
	            return "Failed";
	        }
		}catch (HttpServerErrorException.InternalServerError e) {
		    // Log the details of the 500 Internal Server Error
		    System.out.println("HTTP Status Code:---- " + e.getStatusCode());
		    System.out.println("Response Body:---- " + e.getResponseBodyAsString());
		    e.printStackTrace();
		    return "Failed";
		    
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}
	
	
	public String sendSmsApiCall(JSONObject json, String password, String operator)
	{
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://ksg.intech-mena.com/MSG/v1.1/API/SendSMS";
        System.out.println("MSISDN : " + json.get("MSISDN"));
        try {
        	HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
    		//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    		HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
			System.out.println("Request : " + entity);

	        ResponseEntity<String > responseEntity = restTemplate.postForEntity(apiUrl, entity, String.class);
	        String body = responseEntity.getBody();
	        System.out.println("Body : "+ body);
	        System.out.println("ResponseEntity is :" + responseEntity);
	        
	        TblSmsLogs smsLogs = new TblSmsLogs();
	        smsLogs.setAni(json.getString("MSISDN"));
	        smsLogs.setDateTime(LocalDateTime.now());
	        smsLogs.setRequest(entity.toString());
	        smsLogs.setResponse(responseEntity.toString());
	        smsLogs.setBody(body);
	        smsLogs.setMsg(json.getString("msgText")); 
	        smsLogs.setPassword(password); 
	        smsLogs.setOperator(operator);      
	        smsLogsRepo.save(smsLogs);
	        
	        if(responseEntity.getStatusCode().is2xxSuccessful()) 
	        {
	        	System.out.println("API call successful");
	            if(body.contains("Success") || body.contains("success") || body.contains("1"))
	            {
	            	System.out.println("Inside success in sms send api =====");
	            	return "Success";
	            }else {
	            	System.out.println("Inside Failed in sms send api =====");
	            	return "Failed";
	            }
	        } else {
	            System.out.println("API call failed");
	            return "Failed";
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "Failed";
	}
	
	 public static String generateRequestId() 
	 {
		 UUID uuid = UUID.randomUUID();
		 return uuid.toString();
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
