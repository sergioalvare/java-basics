
import java.io.*;
import java.net.*;
import java.util.*;

public class Temporizador extends TimerTask{
	
	MulticastSocket sckt;
	InetAddress grupo;
	
	public Temporizador(MulticastSocket scktParametro, InetAddress grupo)
	{
		this.grupo = grupo;
		sckt=scktParametro;
	}
	
	public void run()
	{
		String fechaYHora=(new Date()).toString();
		String hora=fechaYHora.substring(11,20);
		byte [] arrayDeBytes = hora.getBytes();
		DatagramPacket paquete = new DatagramPacket(arrayDeBytes, arrayDeBytes.length, grupo, 7777);
		try {
			sckt.send(paquete, (byte)1);//El segundo parametro es el numero de saltos
		} catch (IOException e) {
			System.out.println("");
		}
		
	}

}
