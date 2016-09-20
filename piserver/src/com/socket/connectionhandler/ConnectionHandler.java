package com.socket.connectionhandler;

import com.socket.Stopable;
import com.socket.communicator.Communicator;
import com.socket.requesthandler.RequestHandler;

public interface ConnectionHandler extends Stopable{

	public void keepConnection( Communicator communicator, RequestHandler requestHandler );

}
