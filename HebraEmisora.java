import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.*;

public class HebraEmisora extends Thread{
	
	DatagramSocket elDatagramSocket;

	
	public HebraEmisora(DatagramSocket parametro){
		
		elDatagramSocket=parametro;
		start();
		
	}
	
	public void run()
	{
		
		LineNumberReader entradaTeclado = new LineNumberReader(new InputStreamReader(System.in));
		
		while(true){
			
			try {
				String linea = entradaTeclado.readLine();
				byte arrayParaEnviar [] = linea.getBytes();
				
				//El servidor escucha en la maquina local (localhost) y el puerto 4444:
				InetSocketAddress direccion=new InetSocketAddress("localhost", 4444);
				
				DatagramPacket paquete = new DatagramPacket(arrayParaEnviar, arrayParaEnviar.length, direccion);

				elDatagramSocket.send(paquete);
			} catch (IOException e) {System.out.println("Error en el bucle de la hebra emisora");}
			
		}
		
	}

}
