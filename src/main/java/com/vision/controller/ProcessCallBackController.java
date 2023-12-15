package com.vision.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vision.service.CallBackProcessService;

//@RestController
@Component
public class ProcessCallBackController {
	
	@Autowired
	private CallBackProcessService callbackProcess;
	
	//@GetMapping("/get")
	@Scheduled(fixedDelay = 1000L)
	public void processCallBack()
	{
		callbackProcess.getCallback();
		
	}

}
