package com.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;
import com.socket.communicator.Communicator;
import com.socket.communicator.LengthSpecificCommunicator;
import com.socket.connectionhandler.ConnectionHandler;
import com.socket.connectionhandler.ConnectionHandlerManager;
import com.socket.connectionhandler.NormalConnectionHandlerManager;
import com.socket.executor.CommandExecutor;
import com.socket.executor.CommandExecutorJobAdd;
import com.socket.executor.CommandExecutorSensor;
import com.socket.requesthandler.CommandRequestHandler;

public class SimpleServer implements TCPServer {

	static Logger logger = Logger.getLogger(SimpleServer.class);

	private int threadID = 0;
	private ServerSocket server;
	private static boolean isClosed = false;
	private ConnectionHandlerManager connectionHandlerManager;

	public SimpleServer(){
		connectionHandlerManager = new NormalConnectionHandlerManager();
	}

	public static void main(String[] args) throws IOException{

		try{
			TCPServer server = new SimpleServer();
			server.startServer();
		}catch(Exception e){
			logger.info("main() error", e);
		}
	}

	/*
	 * start server
	 */
	public void startServer() throws Exception
	{
		printMessage("server port open..");
		server = new ServerSocket(5000); //5000

		//
		while( true ){

			try{
				printMessage(" client.");

				Socket connectedSocket = server.accept(); //
				printMessage("Client connected.");

				processService(connectedSocket, threadID++ ); //
			}catch(Exception e){
				if( !(e instanceof java.net.SocketException && isClosed == true) ){
					printMessage("startServer() error", e);
				}
				break;
			}
		}
	}

	/*
	 *  Thread.
	 */
	private void processService(Socket connectedSocket, int serviceID) throws IOException {

		CommandRequestHandler requestHandler = null;
		try{
			requestHandler = new CommandRequestHandler();
			requestHandler.addCommand("add", new CommandExecutorJobAdd());
			requestHandler.addCommand("exit", new CommandExecutorExit());
			requestHandler.addCommand("sensor", new CommandExecutorSensor());
		}catch(Exception e){
			e.printStackTrace();
		}

		ConnectionHandler connectionHandler = connectionHandlerManager.createConnectionHandler(serviceID);
		//connectionHandler.keepConnection(new CommunicatorUTF(connectedSocket), requestHandler);
		connectionHandler.keepConnection(new LengthSpecificCommunicator(connectedSocket), requestHandler);
	}

	/*
	 * 
	 */
	public void shutDownServer() throws Exception{
		isClosed = true;
		printMessage("Close socket server connect.");
		server.close();
		printMessage("Exit opened server service.");
		if(connectionHandlerManager != null) connectionHandlerManager.stop();

	}

	//(msg)
	private void printMessage(String msg){
		ServerLog.getInstance().info(this.getClass().getName(), msg);
	}
	//(msg, throwable)
	private void printMessage(String msg, Throwable e){
		ServerLog.getInstance().info(this.getClass().getName(), msg, e);
	}

	class CommandExecutorExit implements CommandExecutor{

		public void execute(String request, Communicator communicator) throws Exception {
			communicator.sendResponse("exit server");
			shutDownServer();
		}

		public int stop() {
			return Stopable.STAT_IMMEDIATE;
		}
	}
}
