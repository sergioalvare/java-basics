//Sergio Alvare Pelaez 2012

import java.io.*;
import java.net.*;
import java.util.*;

public class servidorEco 
{
	
	public static void main (String args[]) throws Exception 
	{
		DatagramSocket ds = new DatagramSocket(4444);
		byte arrayDeBytes[] = new byte[1024];//es un buffer para recepcion de datos
		DatagramPacket dp = new DatagramPacket(arrayDeBytes, arrayDeBytes.length);
		int contador=0;
	
		System.out.println("Servidor de eco UDP corriendo en este equipo");
		
		while (true) 
		{
			try 
			{
				
				ds.receive(dp); //dp es como el parametro de salida del receive
				ds.send(dp);
				
				System.out.println("Se ha enviado el paquete numero: ");
				System.out.println(contador);
				contador++;
				
			} catch (Exception e) {System.out.println("Hubo un error en el bucle");}
		}
	}
}

//Lo unico que hace es devolver el paquete al cliente de eco.
