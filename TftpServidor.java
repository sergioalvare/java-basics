import java.net.*;

import java.io.*;
import java.util.*;

class tftpservidor extends frame implements Runnable {
	FileInputStream i;
	DatagramPacket r = new DatagramPacket(new byte[516], 516);

	tftpservidor() {
		Thread ts = new Thread(this);
		ts.start();
	}

	int sendDATA() throws IOException {
		int len;
		byte b[] = new byte[516];
		len = i.read(b, 4, 512);
		SS(auxsock, DATA(b, (len + 4)));
		this.seqnum++;
		return len;
	} // envía tramas de datos del fichero

	public void run() {
		try {

			seqnum = 1; // inicializar número de secuencia
			rec = new DatagramPacket(new byte[516], 516);
			sock = new DatagramSocket(ServerPort);
			// socket para RRQ
			auxsock = new DatagramSocket(); // socket para resto de com.

			sock.receive(r); // espera primer RRQ
			i = new FileInputStream(file(r));
			remoteTID = r.getPort();
			a = r.getAddress();
			sock.receive(r); // espera siguiente retransmision de RRQ
			i = new FileInputStream(file(r));
			int l = sendDATA(); // envía datos
			auxsock.receive(r); // espera ACK ( no envia DATA)
			auxsock.receive(r); // espera siguiente retransmision de ACK
			l = sendDATA(); // envía datos
			auxsock.receive(r); // espera ACK
			l = sendDATA(); // envía datos
			auxsock.receive(r); // espera ACK
			l = sendDATA(); // envia datos // sequencias de pruebas
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	/*	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		

	}
}