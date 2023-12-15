package com.vision.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.vision.entity.TblBillingFailed;
import com.vision.entity.TblBillingSuccess;
import com.vision.entity.TblSubscription;
import com.vision.entity.TblUnsubscription;
import com.vision.repo.TblBillingFailedRepo;
import com.vision.repo.TblBillingSuccessRepo;
import com.vision.repo.TblSubscriptionRepo;
import com.vision.repo.TblUnsubRepo;

@Service
public class UtilityService 
{
	@Autowired
	private TblUnsubRepo unsubRepo;
	@Autowired
	private TblSubscriptionRepo subRepo;
	@Autowired
	private TblBillingSuccessRepo billingSuccessRepo;
	@Autowired
	private TblBillingFailedRepo billingFailedRepo;
	
	@Autowired
	SendSmsService sendSmsService;
	
	
	private String flag;
	
	
	public String saveInUnsubscription(String requestId, String msisdn,
			String type,String countryId,
			String operatorId,String subType,String status,
			LocalDate subscriptionEnd, String operator)
	{
		TblUnsubscription unsub = new TblUnsubscription();
		flag="Failed";
		
    	try {
    		unsub.setAni(msisdn);
        	unsub.setDateTime(LocalDateTime.now());
        	unsub.setUnsubDateTime(subscriptionEnd);	
        	unsub.setRequestId(requestId);	
        	unsub.setCountryId(countryId);
        	unsub.setOperatorId(operatorId);
        	unsub.setType(type);
        	unsub.setSubType(subType);
        	unsub.setOperator(operator);    	
        	
        	if(status.equalsIgnoreCase("Ok")) {
        		unsub.setUnsubStatus("Success");
        		flag="OK";
        	}
        	else if(status.equalsIgnoreCase("DELIVERED")) {
        		unsub.setUnsubStatus("Success");
        		flag="DELIVERED";
        	}
        	else if(status.equalsIgnoreCase("FAILED")) {
        		unsub.setUnsubStatus("Failed");
        		flag="FAILED";
        	}
        		
        	unsubRepo.save(unsub);
        	System.out.println("Save in tbl_unsub");
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return flag;
    	}
    	return flag;
    	
		
	}
	
	public void deleteInSubscription(TblSubscription subscription, String operatorId)
	{
		try {
			
			List<TblSubscription> subList= subRepo.findByAni(subscription.getAni(), operatorId);
			if(!subList.isEmpty())
			{
				for (TblSubscription tblSubscription : subList) 
				{
					subRepo.delete(tblSubscription);
					System.out.println("Delete from tbl_sub");
					
				}
				
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();;
		}
		
	}
	
	public void saveInSubscription(String requestId, String transactionId, String msisdn,String type,
			String shortCode,String channelId,String applicationId,String countryId,
			String operatorId,String subType,String status,String price,String activityTime,
			LocalDate subscriptionEnd, String operator,String pack)
	{
		
		TblSubscription subscription = new TblSubscription();
		
		try {
			subscription.setAni(msisdn);
			subscription.setSubDateTime(LocalDateTime.now());       
			subscription.setLastBilledDate(LocalDateTime.now());
			subscription.setPrice(price);
			subscription.setRequestId(requestId);	
			subscription.setTransactionId(transactionId);
			subscription.setShortCode(shortCode);
			subscription.setChannelId(channelId);
			subscription.setApplicationId(applicationId);
			subscription.setCountryId(countryId);
			subscription.setOperatorId(operatorId);
			subscription.setType(type);
			subscription.setSubType(subType);
			subscription.setRecordStatus(status);
			subscription.setPack(pack);
			subscription.setProcessDatetime(LocalDateTime.now());
			subscription.setLanguage("En");
			subscription.setBillingType("Auto");
			subscription.setOperator(operator);		
			subscription.setStatus("0");	
			if(operatorId.equalsIgnoreCase("16"))
			{
				if(price.equalsIgnoreCase("0"))
				{
					subscription.setTrialEndDate(LocalDateTime.now().plusDays(1));
				}
			}
			
			
			if(status.equalsIgnoreCase("OK"))
        	{
				subscription.setNextBilledDate(subscriptionEnd);
				subscription.setBillingType("Pending");
			
			}else if(status.equalsIgnoreCase("DELIVERED"))
			{
				subscription.setNextBilledDate(subscriptionEnd);
				subscription.setBillingType("Billed");
			}else if(status.equalsIgnoreCase("FAILED"))
			{
				subscription.setNextBilledDate(subscriptionEnd);
				subscription.setBillingType("Failed");
			}
			subRepo.save(subscription);		
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveNewSubscription(String requestId, String transactionId, String msisdn,String type,
			String shortCode,String channelId,String applicationId,String countryId,
			String operatorId,String subType,String status,String price,String activityTime,
			LocalDate subscriptionEnd, String operator,String pack, String password)
	{
		
		TblSubscription subscription = new TblSubscription();
		
		try {
			subscription.setAni(msisdn);
			subscription.setSubDateTime(LocalDateTime.now());       
			subscription.setLastBilledDate(LocalDateTime.now());
			subscription.setPrice(price);
			subscription.setRequestId(requestId);	
			subscription.setTransactionId(transactionId);
			subscription.setShortCode(shortCode);
			subscription.setChannelId(channelId);
			subscription.setApplicationId(applicationId);
			subscription.setCountryId(countryId);
			subscription.setOperatorId(operatorId);
			subscription.setType(type);
			subscription.setSubType(subType);
			subscription.setRecordStatus(status);
			subscription.setPack(pack);
			subscription.setPassword(password);	
			subscription.setProcessDatetime(LocalDateTime.now());
			subscription.setLanguage("En");
			subscription.setBillingType("Auto");
			subscription.setOperator(operator);		
			subscription.setStatus("0");	
			if(price.equalsIgnoreCase("0"))
			{
				subscription.setTrialEndDate(LocalDateTime.now().plusDays(1));
			}
			
			if(status.equalsIgnoreCase("OK"))
        	{
				subscription.setNextBilledDate(subscriptionEnd);
				subscription.setBillingType("Pending");
			
			}else if(status.equalsIgnoreCase("DELIVERED"))
			{
				subscription.setNextBilledDate(subscriptionEnd);
				subscription.setBillingType("Billed");
			}else if(status.equalsIgnoreCase("FAILED"))
			{
				subscription.setNextBilledDate(subscriptionEnd);
				subscription.setBillingType("Failed");
			}
			subRepo.save(subscription);		
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveInBillingSuccess(String requestId, String transactionId, String msisdn,String type,
			String shortCode,String channelId,String applicationId,String countryId,
			String operatorId,String subType,String status,String price,String activityTime,
			LocalDate subscriptionEnd, String operator, String pack)
	{
		TblBillingSuccess billingSuccess = new TblBillingSuccess();
		try {
			billingSuccess.setAni(msisdn);
			billingSuccess.setSubDateTime(LocalDateTime.now());       
			billingSuccess.setLastBilledDate(LocalDateTime.now());
			billingSuccess.setNextBilledDate(subscriptionEnd);
			billingSuccess.setPrice(price);
			billingSuccess.setRequestId(requestId);	
			billingSuccess.setTransactionId(transactionId);
			billingSuccess.setShortCode(shortCode);
			billingSuccess.setChannelId(channelId);
			billingSuccess.setApplicationId(applicationId);
			billingSuccess.setCountryId(countryId);
			billingSuccess.setOperatorId(operatorId);
			billingSuccess.setType(type);
			billingSuccess.setSubType(subType);
			billingSuccess.setRecordStatus(status);
			billingSuccess.setPack(pack);
			billingSuccess.setProcessDatetime(LocalDateTime.now());
			billingSuccess.setLanguage("En");
			billingSuccess.setBillingType("Auto");
			billingSuccess.setBillingStatus("Success");
			billingSuccess.setStatus("0");	
			billingSuccess.setOperator(operator);	
			
			
			billingSuccessRepo.save(billingSuccess);	
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void deleteFromUnsub(String ani, String operatorId)
	{
		try {
			List<TblUnsubscription> unsubList = unsubRepo.findByAni(ani,operatorId);
			if(!unsubList.isEmpty())
			{
				for (TblUnsubscription tblUnsubscription : unsubList) 
				{
					unsubRepo.delete(tblUnsubscription);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateInBillingSuccess(String ani, String operatorId, LocalDate subscriptionEnd)
	{
		try {
			List<TblBillingSuccess> billingSuccessList = billingSuccessRepo.findByAni(ani,operatorId);
			if(!billingSuccessList.isEmpty())
			{
				for (TblBillingSuccess tblbillingSuccess : billingSuccessList) 
				{
					tblbillingSuccess.setNextBilledDate(subscriptionEnd);	
					billingSuccessRepo.save(tblbillingSuccess);
					
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveInBillingFailed(String ani, String countryId, String operatorId, String price, String pack, String subType, String recordStatus, String operator )
	{
		try {
			
			TblBillingFailed billingFailed = new TblBillingFailed();
			billingFailed.setAni(ani);
			billingFailed.setCountryId(countryId);
			billingFailed.setOperatorId(operatorId);
			billingFailed.setPrice(price);
			billingFailed.setPack(pack);
			billingFailed.setBillingType("Failed");
			billingFailed.setProcessDatetime(LocalDateTime.now());
			billingFailed.setRecordStatus(recordStatus);
			billingFailed.setSubType(subType);
			billingFailed.setOperator(operator);
			billingFailedRepo.save(billingFailed);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	
	}
	
	
	
	public LocalDate dateTimeFormat(String dateString)
	{
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        try {
	            LocalDate date = LocalDate.parse(dateString, formatter);
	            System.out.println("Parsed Date: " + date);
	            return date;
	        } catch (Exception e) {
	            System.err.println("Error parsing date: " + e.getMessage());
	            return null;	   
	        }
	}
	
}
