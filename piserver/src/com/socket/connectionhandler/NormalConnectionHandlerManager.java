package com.socket.connectionhandler;

import java.util.Enumeration;
import java.util.Hashtable;
import com.socket.Stopable;

public class NormalConnectionHandlerManager implements ConnectionHandlerManager, ExitReceiver
{

	private Hashtable connectionHandlers_;

	public NormalConnectionHandlerManager(){
		connectionHandlers_ = new Hashtable();
	}

	public ConnectionHandler createConnectionHandler(int serviceID) {
		ConnectionHandler handler = new NormalConnectionHandler(serviceID, this);
		connectionHandlers_.put(handler, handler);
		return handler;
	}

	public int stop() {
		Enumeration en = this.connectionHandlers_.keys();
		int retStopStatus = Stopable.STAT_IMMEDIATE;

		while(en.hasMoreElements()){
			ConnectionHandler handler = (ConnectionHandler)en.nextElement();
			int stopStatus = handler.stop();
			if(retStopStatus == Stopable.STAT_NO || stopStatus == Stopable.STAT_NO){
				retStopStatus = Stopable.STAT_NO;
			}
			else if(retStopStatus == Stopable.STAT_SOON || stopStatus == Stopable.STAT_SOON){
				retStopStatus = Stopable.STAT_SOON;
			}
		}
		return retStopStatus;
	}

	public void reportWhenExit(Object exitObject) {

		ConnectionHandler handler = (ConnectionHandler)connectionHandlers_.get(exitObject);
		if(handler != null){
			connectionHandlers_.remove(handler);
		}
	}
}
