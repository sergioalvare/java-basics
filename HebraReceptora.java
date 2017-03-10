//Sergio Alvare Pelaez 2012

import java.io.*;
import java.net.*;
import java.util.*;

public class HebraReceptora extends Thread  
{

	DatagramSocket elDatagramSocket;
	
	public HebraReceptora(DatagramSocket parametro)
	{
		elDatagramSocket=parametro;
		start();
	}
	
	public void run(){
		
		try 
		{
			
			while(true)
			{
			
				//Reservando el array siguiente en cada iteracion del while, eviamos los
				//problemas de sobreescritura parcial de los mensajes, "interferencia entre los mismos"
				//que se observaria si se reservase una unica vez justo despues del try antes de este while.
				byte arrayDeBytesARecibir[] = new byte[100];
				DatagramPacket paquete = new DatagramPacket(arrayDeBytesARecibir, arrayDeBytesARecibir.length);
				elDatagramSocket.receive(paquete);
				System.out.println(new String(paquete.getData()));
				//paquete.getData (donde paquete es un datagrampacket) devuelve un array de bytes
				//Si haces un casting a String tienes una cadena de caracteres unos y ceros
				//pero lo que quiero es interpretarlo como una codificacion de caracteres
				//que por defecto debe de ser ASCII...
				
			}
		
		} catch (IOException e) {System.out.println("Error en el bucle de la hebra receptora");}

	}
}
