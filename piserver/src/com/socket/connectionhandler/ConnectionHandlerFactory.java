package com.socket.connectionhandler;

public interface ConnectionHandlerFactory {

	public ConnectionHandler createConnectionHandler(int serviceID);

}
