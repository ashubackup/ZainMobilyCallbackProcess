package com.vision.service;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vision.entity.AllCallbacks;
import com.vision.entity.ServiceInfo;
import com.vision.entity.TblMessage;
import com.vision.entity.TblSubscription;
import com.vision.repo.CallBackRepo;
import com.vision.repo.ServiceInfoRepo;
import com.vision.repo.TblMessageRepo;
import com.vision.repo.TblSubscriptionRepo;
import com.vision.utility.SaveOrUpdateService;
import com.vision.utility.UtilityService2;


@Service
public class SubCallbackService {
	@Autowired
	private CallBackRepo callbackRepo;
	@Autowired
	private SaveOrUpdateService service;
	@Autowired
	private TblSubscriptionRepo subRepo;
	@Autowired
	private UtilityService2 utilityService2;
	@Autowired
	private TblMessageRepo messageRepo;
	@Autowired
	private ServiceInfoRepo infoRepo;

	
	public void subCallbackProcess(String type, String operator)
	{
		List<AllCallbacks> callbacklist = callbackRepo.findByOperator(type, operator);
		if(!callbacklist.isEmpty())
		{
			callbacklist.forEach(callback->{
				processJson(callback);
				callback.setStatus("1");
				callbackRepo.save(callback);		
			});
		}
	}
	
	
	public void processJson(AllCallbacks allCallbacks)
	{
		try {
			
			JSONObject callbackObject = new JSONObject(allCallbacks);
	        String dateTime = callbackObject.getString("dateTime");
            String callbackString = callbackObject.getString("callback");
            JSONObject innerCallback = new JSONObject(callbackString);
	        String requestId = innerCallback.getString("requestId");
	        String transactionId = innerCallback.getJSONObject("transaction").getString("transactionId");
	        String msisdn = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("msisdn");
	        String applicationId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("applicationId");
	        String operatorId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("operatorId");
	        String status = innerCallback.getJSONObject("transaction").getJSONObject("data").getJSONObject("action").getString("status");
	        String price = innerCallback.getJSONObject("transaction").getJSONObject("data").getJSONObject("action").getString("rate");
	        String subscriptionEndDate = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("subscriptionEnd");
	        System.out.println("SubscriptioneEnd Date : " + subscriptionEndDate);
	        LocalDate subscriptionEnd = service.dateTimeFormat(subscriptionEndDate);
	        
	        System.out.println("Application id====="+applicationId);
	        String serviceName="Kiddocraze";
	        if(applicationId.equalsIgnoreCase("194")) {
	        	serviceName="QuizBox";
	        }
	        
	        
	        String pack="Daily";
	        if(operatorId.equalsIgnoreCase("8"))
	        {
	        	pack="Weekly";
	        }
	        
	      
	        if(status.equalsIgnoreCase("OK"))
	     	{
	     		List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
	         	if(!subScription.isEmpty())
	         	{
	         		for(TblSubscription tblSubscription : subScription)
	         		{
	         			tblSubscription.setNextBilledDate(subscriptionEnd); 
	         			tblSubscription.setServiceName(serviceName);  
	         			tblSubscription.setOperatorId(operatorId);
	         			tblSubscription.setApplicationId(applicationId);
	         			tblSubscription.setPrice(price);        	
	         			subRepo.save(tblSubscription);
	         		}
	         	}else {                       
	         		service.saveInTblUser(msisdn, operatorId);
	         		service.saveInSubscription(transactionId,msisdn,operatorId,price,subscriptionEnd,"Sub",pack,"", applicationId,serviceName);
	         	}
	     	}
	        
	        if(status.equalsIgnoreCase("DELIVERED"))
	        {
	        	service.saveInBillingSuccess(msisdn,"Sub",operatorId,price,subscriptionEnd,pack,applicationId,serviceName);
	    		
	    		///-----------------------Send message-----------------
	    		String password = utilityService2.generateRandomPassword(12);
				System.out.println("Password---" + password);
				
				
				
				
				TblMessage message = messageRepo.findByStatus("0");
				ServiceInfo serviceInfo = infoRepo.findByOperatorid(operatorId,applicationId);
				String msg = message.getMessage().replace("<ani>", msisdn).replace("<pass>", password);
				System.out.println("Message is : " + msg);
				
				String decryptedSmsSignature = utilityService2.buildSignatureForSmsMobily(serviceInfo.getApiKey(), serviceInfo.getApiSecret(), serviceInfo.getApplicationId(), serviceInfo.getCountryId(),
						serviceInfo.getOperatorId(), serviceInfo.getCpId(), msisdn.toUpperCase(), dateTime, "EN", 
						serviceInfo.getShortcode(), msg, "SendSMS");
		
				System.out.println("DecryptedSignature sms : "+ decryptedSmsSignature);

				JSONObject jsonForSms = utilityService2.buildJSON(serviceInfo, msisdn,"EN", requestId, decryptedSmsSignature, msg, dateTime);
				
				System.out.println("Json for sms " + jsonForSms);
				
				utilityService2.sendSmsApiCall(jsonForSms, password, operatorId);
				
				List<TblSubscription> subScription = subRepo.findByAni(msisdn,operatorId);
	        	if(!subScription.isEmpty())
	        	{
	        		for (TblSubscription tblSubscription : subScription)
	        		{
	        			tblSubscription.setNextBilledDate(subscriptionEnd);
	        			tblSubscription.setPassword(password);
	        			tblSubscription.setServiceName(serviceName);  
	        			tblSubscription.setPrice(price);       			
	        			tblSubscription.setOperatorId(operatorId);
	        			tblSubscription.setApplicationId(applicationId);
	        			subRepo.save(tblSubscription);
						
					}
	        	}else {
	        		System.out.println("save new sub------");
	        		service.saveInTblUser(msisdn, operatorId);
	        		service.saveInSubscription(transactionId,msisdn,operatorId,price,subscriptionEnd,"Sub",pack,password,applicationId,serviceName);
	        	}
	     	}
		}catch(Exception e) {
			e.printStackTrace();		
		}
	}
}
