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
import com.vision.entity.TblUser;
import com.vision.repo.TblBillingFailedRepo;
import com.vision.repo.TblBillingSuccessRepo;
import com.vision.repo.TblSubscriptionRepo;
import com.vision.repo.TblUnsubRepo;
import com.vision.repo.TblUserRepo;

@Service
public class SaveOrUpdateService {

	@Autowired
	private TblUnsubRepo unsubRepo;
	@Autowired
	private TblSubscriptionRepo subRepo;
	@Autowired
	private TblBillingSuccessRepo billingSuccessRepo;
	@Autowired
	private TblBillingFailedRepo billingFailedRepo;
	@Autowired
	private TblUserRepo userRepo;
	
//	@Autowired
//	private SendSmsService sendSmsService;
//	
	
	private String flag;
	
	
	public String saveInUnsubscription(String msisdn, String operatorId, LocalDate subscriptionEnd,String pack, String serviceName)
	{
    	try {
    		
    		var user = TblUnsubscription.builder()   	
    			.ani(msisdn)
    			.operatorId(operatorId)
    			.unsubDateTime(subscriptionEnd)
    			.type("Unsub")    	
    			.serviceName(serviceName)
    			.dateTime(LocalDateTime.now())
    			.pack(pack)
    			.build();   		
        	unsubRepo.save(user);
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return flag;
    	}
    	return flag;
    	
		
	}
	
	public void deleteInSubscription(String ani, String operatorId)
	{
		try {
			
			List<TblSubscription> subList= subRepo.findByAni(ani, operatorId);
			if(!subList.isEmpty())
			{
				subList.forEach(user->subRepo.delete(user));			
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();;
		}
	}
	
	public void saveInSubscription(String transactionId, String msisdn, String operatorId, String price, LocalDate subscriptionEnd, String type,String pack,String password,String applicationId, String serviceName)
	{
		try {
			var user = TblSubscription.builder()
				.ani(msisdn)
				.price(price)
				.pack(pack)
				.subDateTime(LocalDateTime.now())
				.nextBilledDate(subscriptionEnd)
				.lastBilledDate(LocalDateTime.now())
				.type(type)	
				.operatorId(operatorId)
				.serviceName(serviceName)
				.transactionId(transactionId)
				.password(password)		
				.applicationId(applicationId)
				.build();
			subRepo.save(user);			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void saveInBillingSuccess(String msisdn,String type,String operatorId,String price,
			LocalDate subscriptionEnd,String pack, String applicationId, String serviceName)
	{
		try {
			var user = TblBillingSuccess.builder()
				.ani(msisdn)
				.type(type)
				.operatorId(operatorId)
				.price(price)
				.nextBilledDate(subscriptionEnd)
				.lastBilledDate(LocalDateTime.now())
				.processDatetime(LocalDateTime.now())
				.serviceName(serviceName)
				.pack(pack)
				.applicationId(applicationId)
				.build();		
			
			billingSuccessRepo.save(user);
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
	
	
	public void saveInTblUser(String ani, String operator) {
		
		try {
			Integer count = userRepo.findByAni(ani);
			if(count==0)
			{
				TblUser user = TblUser.builder()
						.ani(ani)
						.operator(operator)
						.build();
				userRepo.save(user);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
