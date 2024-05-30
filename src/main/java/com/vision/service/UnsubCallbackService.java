package com.vision.service;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vision.entity.AllCallbacks;
import com.vision.repo.CallBackRepo;
import com.vision.utility.SaveOrUpdateService;

@Service
public class UnsubCallbackService {
	
	@Autowired
	private CallBackRepo callbackRepo;
	@Autowired
	private SaveOrUpdateService service;
	
	

	
	public void unsubCallbackProcess(String type)
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
		try {
			JSONObject innerCallback = new JSONObject(callback);
	        String msisdn = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("msisdn");
	        String applicationId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("applicationId");
	        String operatorId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("operatorId");
	        String subscriptionEndDate = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("subscriptionEnd");
	        System.out.println("SubscriptioneEnd Date : " + subscriptionEndDate);
	        LocalDate subscriptionEnd = service.dateTimeFormat(subscriptionEndDate);
	        
	        System.out.println("Application id====="+applicationId);
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
	        System.out.println("------Unsub callback----------");
	        service.saveInUnsubscription(msisdn,operatorId,subscriptionEnd,pack, serviceName);
            service.deleteInSubscription(msisdn, operatorId);	
        	
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
