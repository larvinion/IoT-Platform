package com.socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServerLog {
	private static ServerLog serverLog = null;
	private Logger logger;

	private ServerLog(){
		PropertyConfigurator.configure("/home/pi/IoT-Platform/piserver/bin/com/socket/log4j.properties");
		logger = (Logger)Logger.getInstance("ServerLog");
	}

	public static ServerLog getInstance(){
		if(serverLog == null){
			serverLog = initLog();
		}
		return serverLog;
	}

	private static ServerLog initLog(){
		serverLog = new ServerLog();
		return serverLog;
	}

	public void error(String subTitle, String message){
		logger.error("[" + subTitle + "] : " + message );
	}

	public void error(String subTitle, String message, Throwable e){
		logger.error("[" + subTitle + "] : " + message, e );
	}

	public void debug(String subTitle, String message){
		logger.debug("[" + subTitle + "] : " + message );
	}

	public void debug(String subTitle, String message, Throwable e){
		logger.debug("[" + subTitle + "] : " + message, e );
	}

	public void info(String subTitle, String message){
		logger.info("[" + subTitle + "] : " + message );
	}

	public void info(String subTitle, String message, Throwable e){
		logger.info("[" + subTitle + "] : " + message, e );
	}
}
