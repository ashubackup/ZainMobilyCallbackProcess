package com.vision.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONException;
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
import com.vision.repo.TblUnsubRepo;
import com.vision.utility.SendSmsService;
import com.vision.utility.UtilityService;
import com.vision.utility.UtilityService2;

@Service
public class CallBackProcessService 
{
	@Autowired
	private CallBackRepo callbackRepo;
	
	@Autowired
	private TblSubscriptionRepo subRepo;
	
	@Autowired
	private TblUnsubRepo unsubRepo;
	
	@Autowired
	private TblMessageRepo messageRepo;
	
	@Autowired
	private UtilityService utilityService;
	@Autowired
	private UtilityService2 utilityService2;
	@Autowired
	private SendSmsService smsService;
	@Autowired
	private ServiceInfoRepo infoRepo;
	
	private String flag;
	
	public void getCallback()
	{
		
		List<AllCallbacks> callbackList = callbackRepo.findByStatus("0");
		if(!callbackList.isEmpty())
		{
			for (AllCallbacks allCallbacks : callbackList) 
			{
				try {
		            JSONObject callbackObject = new JSONObject(allCallbacks);

		            String dateTime = callbackObject.getString("dateTime");
		            String callbackString = callbackObject.getString("callback");

		            JSONObject innerCallback = new JSONObject(callbackString);

		            String requestId = innerCallback.getString("requestId");
		            String transactionId = innerCallback.getJSONObject("transaction").getString("transactionId");
		            String msisdn = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("msisdn");
		            String type = innerCallback.getJSONObject("transaction").getJSONObject("data").getJSONObject("action").getString("type");
		            String shortCode = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("shortcode");
		            String channelId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("channelId");
		            String applicationId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("applicationId");
		            String countryId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("countryId");
		            String operatorId = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("operatorId");
		            String subType = innerCallback.getJSONObject("transaction").getJSONObject("data").getJSONObject("action").getString("subType");
		            String status = innerCallback.getJSONObject("transaction").getJSONObject("data").getJSONObject("action").getString("status");
		            String price = innerCallback.getJSONObject("transaction").getJSONObject("data").getJSONObject("action").getString("rate");
		            String activityTime = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("activityTime");
		            String subscriptionEndDate = innerCallback.getJSONObject("transaction").getJSONObject("data").getString("subscriptionEnd");
		            System.out.println("SubscriptioneEnd Date : " + subscriptionEndDate);
		            LocalDate subscriptionEnd = utilityService.dateTimeFormat(subscriptionEndDate);
		            
		            
//		            System.out.println("DateTime :"+dateTime);
//		            System.out.println("Request ID: " +requestId);
//		            System.out.println("Transaction ID: " + transactionId);
//		            System.out.println("Shortcode: " + shortCode);
//		            System.out.println("ChannelId: " + channelId);
//		            System.out.println("ApplicationId: " + applicationId);
//		            System.out.println("CountryId: " + countryId);
//		            System.out.println("OperatorId: " + operatorId );
//		            System.out.println("Type : "+ type);
//		            System.out.println("subType: " + subType);
//		            System.out.println("status: " + status );
//		            System.out.println("rate: " + price);
//		            System.out.println("activityTime: " +activityTime);
//		            System.out.println("Subscription End: " + subscriptionEnd);
		            
		            String operator="";
		            String pack="";
		            if(operatorId.equalsIgnoreCase("16"))
		            {
		            	operator="Mobily";
		            	pack="Daily";
		            	System.out.println("---------------------Mobily process start-------------------");
		            	if(subType.equalsIgnoreCase("UNSUBSCRIBE"))
			            {
		            		System.out.println("------Unsub callback----------");
			            	List<TblSubscription> subscriptionList = subRepo.findByAni(msisdn,operatorId);
			            	if(!subscriptionList.isEmpty())
			            	{
			            		for (TblSubscription subscription : subscriptionList) 
			            		{
			            			
					            	flag =utilityService.saveInUnsubscription(requestId,msisdn,type,
					            			countryId,operatorId,subType,status,subscriptionEnd, operator);
					            	
					            	if(flag.equalsIgnoreCase("OK") || flag.equalsIgnoreCase("DELIVERED")){
					            		if(subscription != null)
					            			utilityService.deleteInSubscription(subscription, operatorId);	
					            		else
					            			System.out.println("User Not Found in tbl_sub");
					            	}
								}
			            	}
			            }else if(subType.equalsIgnoreCase("SUBSCRIBE")) 
			            {
			            	System.out.println("-----------------Subscrib callback-----------");
			            	if(status.equalsIgnoreCase("OK"))
			            	{
			            		List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for(TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd); 
				            			tblSubscription.setRecordStatus(status);	
				            			tblSubscription.setBillingType("Pending");
				            			tblSubscription.setOperator(operator);         
				            			subRepo.save(tblSubscription);
				            		}
				            	}else {
				            		utilityService.saveInSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator,pack);
				            		
				            		utilityService.deleteFromUnsub(msisdn, operatorId);
				            	}
			            	}else if(status.equalsIgnoreCase("DELIVERED"))
			            	{
			            		utilityService.saveInBillingSuccess(requestId,transactionId,msisdn,type,shortCode,channelId,
				            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd, operator,pack);
			            		
			            		
			            		///-----------------------Send message-----------------
			            		String password = utilityService2.generateRandomPassword(12);
								System.out.println("Password---" + password);
								TblMessage message = messageRepo.findByStatus("0");
								ServiceInfo serviceInfo = infoRepo.findByStatus("0");
								String msg = message.getMessage().replace("<ani>", msisdn).replace("<pass>", password);
								System.out.println("Message is : " + msg);
								
								String dencryptedSmsSignature = utilityService2.buildSignatureForSmsMobily(serviceInfo.getApiKey(), serviceInfo.getApiSecret(), serviceInfo.getApplicationId(), serviceInfo.getCountryId(),
										serviceInfo.getOperatorId(), serviceInfo.getCpId(), msisdn.toUpperCase(), dateTime, "EN", 
										serviceInfo.getShortcode(), msg, "SendSMS");
								
								
								System.out.println("DecryptedSignature sms : "+ dencryptedSmsSignature);
			
								JSONObject jsonForSms = utilityService2.buildJSON(serviceInfo, msisdn,"EN", requestId, dencryptedSmsSignature, msg, dateTime);
								
								System.out.println("Json for sms " + jsonForSms);
								
								utilityService2.sendSmsApiCall(jsonForSms, password, operator);
								
								List<TblSubscription> subScription =  subRepo.findByAni(msisdn,operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for (TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd);
				            			tblSubscription.setPassword(password);
				            			tblSubscription.setRecordStatus(status);
				            			tblSubscription.setBillingType("Success");
				            			
				            			System.out.println("update in tbl sub-----");
				            			subRepo.save(tblSubscription);
										
									}
				            	}else {
				            		System.out.println("save new sub------");
				            		utilityService.saveNewSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd, operator, pack, password);
				            	}
				            	
				            	utilityService.deleteFromUnsub(msisdn, operatorId);
				            	
			            	}else if(status.equalsIgnoreCase("FAILED"))
			    			{
			    				List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for (TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd);
				            			tblSubscription.setRecordStatus(status);
				            			tblSubscription.setBillingType("Failed");
				            			System.out.println("save in tbl sub-----");
				            			subRepo.save(tblSubscription);
										
									}
				            	}else 
				            	{
				            		utilityService.saveInSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator,pack);
				            	}
			    			}
			            		
			            }else if(subType.equalsIgnoreCase("RENEWAL"))
			            {
			            	if(status.equalsIgnoreCase("OK"))
			            	{
			            		List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for(TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd); 
				            			tblSubscription.setRecordStatus(status);	
				            			tblSubscription.setBillingType("Pending");
				            			tblSubscription.setOperator(operator);         
				            			subRepo.save(tblSubscription);
				            		}
				            	}else {
				            		utilityService.saveInSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator,pack);
				            	}
				            	utilityService.deleteFromUnsub(msisdn, operatorId);
				            	utilityService.updateInBillingSuccess(msisdn, operatorId, subscriptionEnd);            	
			            	}
			            	else if(status.equalsIgnoreCase("DELIVERED"))
			            	{
			            		List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for (TblSubscription tblSubscription : subScription) 
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd);  
				            			tblSubscription.setOperator(operator);		            
				            			subRepo.save(tblSubscription);
									}
				            		
				            	}else {
					            	utilityService.saveInBillingSuccess(requestId,transactionId,msisdn,type,shortCode,channelId,
				            				applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator, pack);
				            	}
				            	utilityService.updateInBillingSuccess(msisdn, operatorId, subscriptionEnd); 
				            	utilityService.deleteFromUnsub(msisdn, operatorId);
			            		
			            	}else if(status.equalsIgnoreCase("FAILED"))
			    			{
			    				List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for (TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd);
				            			tblSubscription.setRecordStatus(status);
				            			tblSubscription.setOperator(operator);
				            			tblSubscription.setBillingType("Failed");
				            			System.out.println("save in tbl sub-----");
				            			subRepo.save(tblSubscription);
										
									}
				            	}else 
				            	{
				            		utilityService.saveInSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator,pack);
				            	}
				            	utilityService.updateInBillingSuccess(msisdn, operatorId, subscriptionEnd); 
				            	utilityService.saveInBillingFailed(msisdn,countryId,operatorId,price,pack,subType,status,operator);
			    			}
			            	
			            }
		            	
		            }else if(operatorId.equalsIgnoreCase("8"))
		            {
		            	System.out.println("---------------Zain callback process--------------");
		            	operator="Zain";
		            	pack="Weekly";
		            	if(subType.equalsIgnoreCase("UNSUBSCRIBE"))
			            {
			            	List<TblSubscription> subscriptionList = subRepo.findByAni(msisdn,operatorId);
			            	
			            	if(!subscriptionList.isEmpty())
			            	{
			            		for (TblSubscription subscription : subscriptionList) 
			            		{
			            			
					            	flag =utilityService.saveInUnsubscription(requestId,msisdn,type,
					            			countryId,operatorId,subType,status,subscriptionEnd, operator);
					            	
					            	if(flag.equalsIgnoreCase("OK") || flag.equalsIgnoreCase("DELIVERED")){
					            		if(subscription != null)
					            			utilityService.deleteInSubscription(subscription, operatorId);	
					            		else
					            			System.out.println("User Not Found in tbl_sub");
					            	}
								}
			            	}
			            }else if(subType.equalsIgnoreCase("SUBSCRIBE")) 
			            {
			            	if(status.equalsIgnoreCase("OK"))
			            	{
			            		List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for(TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd); 
				            			tblSubscription.setRecordStatus(status);	
				            			tblSubscription.setBillingType("Pending");
				            			tblSubscription.setOperator(operator);         
				            			subRepo.save(tblSubscription);
				            		}
				            	}else {
				            		utilityService.saveInSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator,pack);
				            		
				            		utilityService.deleteFromUnsub(msisdn, operatorId);
				            	}
			            	}else if(status.equalsIgnoreCase("DELIVERED"))
			            	{
			            		utilityService.saveInBillingSuccess(requestId,transactionId,msisdn,type,shortCode,channelId,
				            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd, operator,pack);
			            		
			            		
			            		///-----------------------Send message-----------------
			            		String password = utilityService2.generateRandomPassword(12);
								System.out.println("Password---" + password);
								TblMessage message = messageRepo.findByStatus("0");
								ServiceInfo serviceInfo = infoRepo.findByStatus("1");
								String msg = message.getMessage().replace("<ani>", msisdn).replace("<pass>", password);
								System.out.println("Message is : " + msg);
								
								String dencryptedSmsSignature = utilityService2.buildSignatureForSms(serviceInfo.getApiKey(), serviceInfo.getApiSecret(), serviceInfo.getApplicationId(), serviceInfo.getCountryId(),
										serviceInfo.getOperatorId(), serviceInfo.getCpId(), msisdn.toUpperCase(), dateTime, "EN", 
										serviceInfo.getShortcode(), msg, "SendSMS");
								
								
								System.out.println("DecryptedSignature sms : "+ dencryptedSmsSignature);
			
								JSONObject jsonForSms = utilityService2.buildJSON(serviceInfo, msisdn,"EN", requestId, dencryptedSmsSignature, msg, dateTime);
								
								System.out.println("Json for sms " + jsonForSms);
								
								utilityService2.sendSmsApiCall(jsonForSms, password, operator);
								
								List<TblSubscription> subScription =  subRepo.findByAni(msisdn,operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for (TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd);
				            			tblSubscription.setPassword(password);
				            			tblSubscription.setRecordStatus(status);
				            			tblSubscription.setOperator(operator);				    
				            			tblSubscription.setBillingType("Success");
				            			
				            			System.out.println("save in tbl sub-----");
				            			subRepo.save(tblSubscription);
										
									}
				            	}else {
				            		System.out.println("save new sub------");
				            		utilityService.saveNewSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd, operator, pack, password);
				            	}
				            	
				            	utilityService.deleteFromUnsub(msisdn, operatorId);
				            	
			            	}else if(status.equalsIgnoreCase("FAILED"))
			    			{
			    				List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for (TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd);
				            			tblSubscription.setRecordStatus(status);
				            			tblSubscription.setOperator(operator);
				            			tblSubscription.setBillingType("Failed");
				            			System.out.println("save in tbl sub-----");
				            			subRepo.save(tblSubscription);
										
									}
				            	}else 
				            	{
				            		utilityService.saveInSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator,pack);
				            	}
			    			}
			            		
			            }else if(subType.equalsIgnoreCase("RENEWAL"))
			            {
			            	if(status.equalsIgnoreCase("OK"))
			            	{
			            		List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for(TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd); 
				            			tblSubscription.setRecordStatus(status);	
				            			tblSubscription.setBillingType("Pending");
				            			tblSubscription.setOperator(operator);         
				            			subRepo.save(tblSubscription);
				            		}
				            	}else {
				            		utilityService.saveInSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator,pack);
				            	}
				            	utilityService.deleteFromUnsub(msisdn, operatorId);
				            	utilityService.updateInBillingSuccess(msisdn, operatorId, subscriptionEnd);            	
			            	}
			            	else if(status.equalsIgnoreCase("DELIVERED"))
			            	{
			            		List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for (TblSubscription tblSubscription : subScription) 
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd);  
				            			tblSubscription.setOperator(operator);		            
				            			subRepo.save(tblSubscription);
									}
				            		
				            	}else {
					            	utilityService.saveInBillingSuccess(requestId,transactionId,msisdn,type,shortCode,channelId,
				            				applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator, pack);
				            	}
				            	utilityService.updateInBillingSuccess(msisdn, operatorId, subscriptionEnd); 
				            	utilityService.deleteFromUnsub(msisdn, operatorId);
			            		
			            	}else if(status.equalsIgnoreCase("FAILED"))
			    			{
			    				List<TblSubscription> subScription = subRepo.findByAni(msisdn, operatorId);
				            	if(!subScription.isEmpty())
				            	{
				            		for (TblSubscription tblSubscription : subScription)
				            		{
				            			tblSubscription.setNextBilledDate(subscriptionEnd);
				            			tblSubscription.setRecordStatus(status);
				            			tblSubscription.setOperator(operator);
				            			tblSubscription.setBillingType("Failed");
				            			System.out.println("save in tbl sub-----");
				            			subRepo.save(tblSubscription);
										
									}
				            	}else 
				            	{
				            		utilityService.saveInSubscription(requestId,transactionId,msisdn,type,shortCode,channelId,
					            			applicationId,countryId,operatorId,subType,status,price,activityTime,subscriptionEnd,operator,pack);
				            	}
				            	utilityService.updateInBillingSuccess(msisdn, operatorId, subscriptionEnd); 
				            	utilityService.saveInBillingFailed(msisdn,countryId,operatorId,price,pack,subType,status,operator);
			    			}
			            }
		            	
		            }
		          
		            allCallbacks.setStatus("1");		
		            callbackRepo.save(allCallbacks);
		            
		        } catch (JSONException e) {
		            e.printStackTrace();
		        }catch(Exception e) {
		        	e.printStackTrace();
		        }
			}
		}else {
			System.out.println("-----Callbacks size is 0---");
		}
	}
}
