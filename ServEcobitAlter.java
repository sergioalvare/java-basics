import java.io.*;
import java.net.*;

public class ServEcobitAlter {
	
	public static void main(String args[]) throws IOException{
		
	
		DatagramSocket sock=new DatagramSocket(7); //DatagramSocket en el mimso puerto
		//que el cliente para recibir datos
	
		while(true){
			
			byte [] buf=new byte[1024]; //creo el buffer de datos
			DatagramPacket dpack=new DatagramPacket(buf,buf.length);//el datagram packet guardara datos sobre buffer

			sock.receive(dpack); //recibo y le paso como parametro el objeto donde guaro datos
			ServEcoAlterHilos hilo=new ServEcoAlterHilos(dpack);//creo los hilos
			hilo.start();//y los lanzo
		}		
	}
}
