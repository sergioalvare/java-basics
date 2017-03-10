//Sergio Alvare Pelaez 2012

import java.io.*;
import java.nio.*;  
import java.nio.channels.*;
import java.net.*;
import java.util.*; 


// import java.io.IOException;
// import java.nio.ByteBuffer;
// import java.nio.channels.SelectionKey;
// import java.nio.channels.SocketChannel;
// import java.util.TimerTask;

public class esclavoConexiones extends TimerTask {


	private servidor6 servidor;
		private SelectionKey key;

	public esclavoConexiones(SelectionKey key, servidor6 servidorParametro) {
		this.key = key;
		this.servidor = servidorParametro;
	}


	public void run() {

		SocketChannel cliente = (SocketChannel) key.channel();
		ByteBuffer arrayDeBytes = ByteBuffer.allocate(128);
		byte[] mensaje = servidor.getNumeroDeConexiones().toString().getBytes();
		arrayDeBytes.put(mensaje, 0, mensaje.length);
		arrayDeBytes.put((byte) '\r');
		arrayDeBytes.put((byte) '\n');
		arrayDeBytes.flip();
		try {
			cliente.write(arrayDeBytes);
		} catch (IOException e) {
			System.out.println("");
		}
	}

}
