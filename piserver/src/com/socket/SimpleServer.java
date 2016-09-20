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
	 * 소켓서버를 실행한다.
	 */
	public void startServer() throws Exception
	{
		printMessage("소켓 서버를 실행합니다..");
		server = new ServerSocket(5000); //5000번 포트로 리슨

		//클라이언트와의 연결 대기 루프
		while( true ){

			try{
				printMessage("새로운 client의 연결요청을 기다립니다.");

				Socket connectedSocket = server.accept(); //
				printMessage("Client와 연결이 이루어지고 서비스를 시작합니다.");

				processService(connectedSocket, threadID++ ); //별도의 쓰레드를 생성해서 서비스 처리를 위임한다.
			}catch(Exception e){
				if( !(e instanceof java.net.SocketException && isClosed == true) ){
					printMessage("startServer() error", e);
				}
				break;
			}
		}
	}

	/*
	 * 신규 클라이언트 접속시마다 새로운 Thread를 생성해서 서비스 처리를 위임한다.
	 */
	private void processService(Socket connectedSocket, int serviceID) throws IOException {

		CommandRequestHandler requestHandler = null;
		try{
			requestHandler = new CommandRequestHandler();
			requestHandler.addCommand("add", new CommandExecutorJobAdd());
			requestHandler.addCommand("exit", new CommandExecutorExit());
		}catch(Exception e){
			e.printStackTrace();
		}

		ConnectionHandler connectionHandler = connectionHandlerManager.createConnectionHandler(serviceID);
		//connectionHandler.keepConnection(new CommunicatorUTF(connectedSocket), requestHandler);
		connectionHandler.keepConnection(new LengthSpecificCommunicator(connectedSocket), requestHandler);
	}

	/*
	 * ���Ϲ��� �����Ѵ�.
	 */
	public void shutDownServer() throws Exception{
		isClosed = true;
		printMessage("Close socket server connect.");
		server.close();
		printMessage("Exit opened server service.");
		if(connectionHandlerManager != null) connectionHandlerManager.stop();

	}

	//�α� ���� �Լ�(msg)
	private void printMessage(String msg){
		ServerLog.getInstance().info(this.getClass().getName(), msg);
	}
	//�α� ���� �Լ�(msg, throwable)
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
