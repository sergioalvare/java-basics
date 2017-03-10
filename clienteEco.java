//Sergio Alvare Pelaez 2012

import java.io.*;
import java.net.*;
import java.util.*;

public class clienteEco {
	
	
	public static void main (String args[]) throws Exception 
	{
		DatagramSocket ds = new DatagramSocket();
		HebraReceptora laHebraReceptora=new HebraReceptora(ds);
		HebraEmisora laHebraEmisora=new HebraEmisora(ds);
	}	
}

//Es necesario que utilice un hilo para enviar y otro para recibir porque el envio con UDP es no fiable, y si usasemos un solo
//hilo que alternase entre envio y recepcion, ante la perida de un paquete que deberia haber sido recibido
//por el cliente, el programa quedaria bloqueado. Con dos hilos se evita ese posible bloqueo.