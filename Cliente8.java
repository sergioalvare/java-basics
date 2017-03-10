//Sergio Alvare Pelaez 2012

import java.io.*;
import java.util.*;
import java.net.*;

// import java.net.DatagramPacket;
// import java.net.DatagramSocket;
// import java.net.InetAddress;
// import java.net.MulticastSocket;


public class Cliente8 {
	
	public static void main (String args[]) throws Exception 
	{
	
		System.out.println("Cliente de hora corriendo en este equipo...");
		
		InetAddress grupo = InetAddress.getByName("228.6.6.6");
		MulticastSocket sckt = new MulticastSocket(7777);
		sckt.joinGroup(grupo);
	
		while(true){
			
			byte[] arrayDeBytes = new byte[1024];
			DatagramPacket paquete = new DatagramPacket(arrayDeBytes, arrayDeBytes.length);
			sckt.receive(paquete);
			System.out.println(new String(paquete.getData(),0,paquete.getLength()));
		
		}

		//Si hubiese condicion de ruptura aniadiriamos:
		//sckt.leaveGroup(group); sckt.close();
}}
