package com.vision.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collections;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vision.entity.ServiceInfo;
import com.vision.entity.TblSubscription;
import com.vision.repo.ServiceInfoRepo;

@Service
public class SendSmsService {
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ServiceInfoRepo infoRepo;
	
	
	public String sendSms(TblSubscription request)
	{
		
		String uri="http://ksg.intech-mena.com/MSG/v1.1/API/SendSMS";
		
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			
			HttpEntity<TblSubscription> entity = new HttpEntity<>(request, headers);
			
			ResponseEntity<String > response = restTemplate.postForEntity(uri, entity, String.class);
			
			
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return null;
		
	}
	
	public String urlEncode(String value) {
	    try {
	        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
	                .replace("+", "%20");
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	        return null;
	    }catch(Exception e) {
	    	e.printStackTrace();
	        return null;
	    }
	}
	
	public String decreptedSignatureService(String msisdn, String language)
	{
		ServiceInfo serviceInfo = infoRepo.findByStatus("0");
		System.out.println(serviceInfo);
		String decryptedSignature;
		try {
			
			if(serviceInfo != null ) 
			{
				decryptedSignature = "ApiKey=" + urlEncode(serviceInfo.getApiKey()) +
						"&ApiSecret=" + urlEncode(serviceInfo.getApiSecret()) +
						"&ApplicationId=" + urlEncode(serviceInfo.getApplicationId()) +
						"&CountryId=" + urlEncode(serviceInfo.getCountryId()) +
						"&OperatorId=" + urlEncode(serviceInfo.getOperatorId()) +
						"&CpId=" + urlEncode(serviceInfo.getCpId()) +
						"&MSISDN=" + urlEncode(msisdn.toUpperCase()) +
						"&Timestamp=" + urlEncode(LocalDateTime.now().toString())+
						"&Lang=" + urlEncode(language.toUpperCase()) +
						"&ShortCode=" + urlEncode(serviceInfo.getShortcode()) +
						"&Method=" + "RequestPinCode";
				return decryptedSignature;
			}else {
				System.out.println("Service info data is null");
				return null;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	public String calculateHMACSHA256(String key, String data) 
	{
		
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			return new String(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes("UTF-8"))));
			
			} catch (NoSuchAlgorithmException e) { 
				e.printStackTrace();
			} catch (InvalidKeyException e){
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) { 
				e.printStackTrace();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		
		return null;
	}
	
	

}
