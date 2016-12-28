package com.dlnapps.main;

import org.fourthline.cling.support.model.DIDLContent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApplication {

    private ApplicationContext applicationContext;

    private static MainApplication instance;

    public static DIDLContent didlContent;

    public static int didlContentCount;
    
    private MainApplication(ApplicationContext applicationContext) {
	this.applicationContext = applicationContext;
    }

    public static MainApplication getInstance() {

	if (instance == null) {

	    instance = new MainApplication(new ClassPathXmlApplicationContext("classpath:/META-INF/applicationContext.xml"));
	}

	return instance;
    }


    public ApplicationContext getApplicationContext() {
	return applicationContext;
    }
    
}
