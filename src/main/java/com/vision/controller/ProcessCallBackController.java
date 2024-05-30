package com.vision.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import com.vision.service.RenewalCallBackService;
import com.vision.service.SubCallbackService;
import com.vision.service.UnsubCallbackService;


//@RestController
@Component
public class ProcessCallBackController {
	
	@Autowired
	private SubCallbackService subService;
	@Autowired
	private UnsubCallbackService unsubService;
	@Autowired
	private RenewalCallBackService renService;
	
	//@GetMapping("/sub")
	@Scheduled(fixedDelay = 1000L)
	public void subCallBackProcess()
	{
		subService.subCallbackProcess("SUBSCRIBE", "16");
		
	}
	//@GetMapping("/sub")
	@Scheduled(fixedDelay = 2000L)
	public void subCallBackProcessZain()
	{
		subService.subCallbackProcess("SUBSCRIBE", "8");
		
	}
	
	//@GetMapping("/ren")
	@Scheduled(fixedDelay = 3000L)
	public void renCallBackProcess()
	{
		renService.renCallbackProcess("RENEWAL");
		
	}
	
	//@GetMapping("/unsub")
	@Scheduled(fixedDelay = 80000L)
	public void unsubCallBackProcess()
	{
		unsubService.unsubCallbackProcess("UNSUBSCRIBE");
		
	}
}
