import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	private int numClients = 1;

	Server(Consumer<Serializable> call){
		callback = call;
		server = new TheServer();
		server.start();
	}

	public class TheServer extends Thread{
		public void run() {
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		    while(true) {
				ClientThread c = new ClientThread(mysocket.accept(), numClients);
				callback.accept("Player has connected to server: " + "Player " + numClients);
				numClients += 1;
				clients.add(c);
				c.start();
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
		class ClientThread extends Thread{
			Socket connection;
			int count = 1;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public void updateClients(String message) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 t.out.writeObject(message);
					}
					catch(Exception e) {}
				}
			}


			
			public void run(){
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				updateClients("New client on server: Player "+count);
				 while(true) {
					    try {
					    	String data = in.readObject().toString();
					    	callback.accept("Player " + count + ": " + data);
					    	updateClients(data);
					    	}
					    catch(Exception e) {
					    	callback.accept("User " + count + " left the server!");
					    	updateClients("Player "+count+" has left the server!");
					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
		}//end of client thread
}