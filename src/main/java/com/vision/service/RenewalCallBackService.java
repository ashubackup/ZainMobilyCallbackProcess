package com.vision.service;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vision.entity.AllCallbacks;
import com.vision.entity.TblSubscription;
import com.vision.repo.CallBackRepo;
import com.vision.repo.TblSubscriptionRepo;
import com.vision.utility.SaveOrUpdateService;

@Service
public class RenewalCallBackService {
	
	@Autowired
	private CallBackRepo callbackRepo;
	@Autowired
	private SaveOrUpdateService service;
	@Autowired
	private TblSubscriptionRepo subRepo;
	
	
	
	public void renCallbackProcess(String type)
	{
		List<AllCallbacks> callbacklist = callbackRepo.findByType(type);
		if(!callbacklist.isEmpty())
		{
			callbacklist.forEach(callback->{
				processJson(callback.getCallback());
				
				callback.setStatus("1");
				callbackRepo.save(callback);		
			});
		}
	}
	
	public void processJson(String callback)
	{
		//JSONObject callbackObject = new JSONObject(callback);
		//String callbackString = callbackObject.getString("callback");
		JSONObject innerCallback = new JSONObject(callback);
        String transactionId = innerCallback.getJSONObject("transaction").getString("transactionId");
        String msisdn = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("msisdn");
        String operatorId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("operatorId");
        String applicationId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("applicationId");
        String status = innerCallback.getJSONObject("transaction").getJSONObject("data").getJSONObject("action").getString("status");
        String price = innerCallback.getJSONObject("transaction").getJSONObject("data").getJSONObject("action").getString("rate");
        String subscriptionEndDate = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("subscriptionEnd");
        System.out.println("SubscriptioneEnd Date : " + subscriptionEndDate);
        LocalDate subscriptionEnd = service.dateTimeFormat(subscriptionEndDate);
        
        
        
        String serviceName="Kiddocraze";
        if(applicationId.equalsIgnoreCase("193")){
        	serviceName="Kiddocraze";
        }else if(applicationId.equalsIgnoreCase("194")) {
        	serviceName="QuizBox";
        }
        
        String pack="Daily";
        if(operatorId.equalsIgnoreCase("8"))
        {
        	pack="Weekly";
        }
       
		
		if(status.equalsIgnoreCase("OK") || status.equalsIgnoreCase("DELIVERED"))
    	{
    		List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
        	if(!subScription.isEmpty())
        	{
        		for(TblSubscription tblSubscription : subScription)
         		{
         			tblSubscription.setNextBilledDate(subscriptionEnd); 
         			tblSubscription.setServiceName(serviceName);  
         			tblSubscription.setOperatorId(operatorId);
         			tblSubscription.setTransactionId(transactionId);
         			tblSubscription.setPrice(price);
         			subRepo.save(tblSubscription);
         			
         		}
        	}else {
        		service.saveInSubscription(transactionId,msisdn,operatorId,price,subscriptionEnd,"Ren",pack,"",applicationId, serviceName);
        	}
        	service.saveInBillingSuccess(msisdn,"Ren",operatorId,price,subscriptionEnd,pack,applicationId, serviceName);            	
    	}
		
	}
}
