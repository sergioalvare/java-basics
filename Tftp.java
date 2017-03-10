import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

public class Tftp {
	public static void main(String args[]) throws IOException { //lanza servidor concurrente y un cliente
		if (args.length != 3) {
			throw (new RuntimeException("Sintaxis: host get fichero"));
		} else if (args[1].equals("get")) {
			PipedOutputStream o = new PipedOutputStream();
			PipedInputStream i = new PipedInputStream(o);
			LinkedList<DatagramPacket> listdp = new LinkedList<DatagramPacket>();
			DatagramSocket sock = new DatagramSocket();
			//tftpservidor ts = new tftpservidor(); // Test server
			TftpServidorConcurrente ts=new TftpServidorConcurrente();
			new Thread(ts).start();
			Timer ti = new Timer(o); // Timer manager
			TftpCliente tf = new TftpCliente(args[2], args[0], i, ti, listdp,
					sock);
			Line l = new Line(o, listdp, sock); // Line Manager
		} else {
			System.out.println("comando desconocido: " + args[0] + args[1]
					+ args[2]);
		}
	} // main
} // tftp