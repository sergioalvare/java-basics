import java.net.*;
import java.io.*;
import java.util.*;

class Line implements Runnable, cons { //recibe paquetes,los guarda en una lista y genera evento a través del pipe de k se recibieron
	LinkedList<DatagramPacket> listrec;
	DatagramSocket sock;
	PipedOutputStream o;

	Line(PipedOutputStream ot, LinkedList<DatagramPacket> ll, DatagramSocket s) {
		o = ot;
		listrec = ll;
		sock = s;
		Thread l = new Thread(this);
		l.start();
	} // Line constructor

	public void run() {
		while (true) {
			try {
				DatagramPacket rec = new DatagramPacket(new byte[516], 516);
				sock.receive(rec); // espera llegada de datagrama
				synchronized (listrec) {
					listrec.addLast(rec); // deja datagrama recibido en la lista
				}
				o.write((byte) frame); // envia evento “frame”
				o.flush();
			} catch (IOException e) {
				System.out.println("Line: " + e);
			}
		} // while
	} // run
} // Line