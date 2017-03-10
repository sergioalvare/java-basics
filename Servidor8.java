import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor8 {

	
	public static void main(String args[]) throws Exception
	{
			System.out.println("Servidor de hora corriendo en este equipo...");
			
			InetAddress grupo = InetAddress.getByName("228.6.6.6");
			MulticastSocket sckt = new MulticastSocket(7777);
			
			Timer t1=new Timer();
			t1.schedule(new Temporizador(sckt, grupo),5000,5000);
	}
	
}
