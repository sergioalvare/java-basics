import java.net.*;
import java.io.*;
import java.util.*;

class TftpCliente extends frame implements Runnable, cons {
	String file, host; // parámetros de activación
	FileOutputStream o;
	PipedInputStream i;
	Timer t; // referencia al gestor de timers
	LinkedList<DatagramPacket> listrec; // lista de paquetes recibidos
	int event, state = espera; // variable de evento y estado

	TftpCliente(String f, String h, PipedInputStream inp, Timer ti,
			LinkedList<DatagramPacket> ll, DatagramSocket s){
		file = f;
		host = h;
		i = inp;
		t = ti;
		listrec = ll;
		sock = s;
		try {
			o = new FileOutputStream("copiaencliente_" + file);
			a = InetAddress.getByName(host);
			Thread tc = new Thread(this);
			tc.start();
		} catch (UnknownHostException e) {
			System.out.println("tftpcliente excep.: " + e);
		} catch (IOException e) {
			System.out.println("tftpcliente excepcion: " + e);
		}
	} // constructoritftpcliente

	public void run() { //el cliente envia el RRQ,EL FICHERO ,ASCI
		//LUEGO SI RECIBE PAKETE DE MENOS DE 512 OCTETOS ENVIA ACK DL ULTIMO PAKETE Y ACABA

		try {
			remoteTID = ServerPort; // primera transición

			SS(sock, (sent = RRQ(file, "netascii")));
			t.startTimers();
			state = recibiendo;

			while (state != espera) { // bucle de espera de eventos. ESPERA----ACABO
				event = i.read();

				switch (event) {
				case frame:
					synchronized (listrec) {
						rec = listrec.removeFirst();
					}

					if (firstDATAframe()) {
						remoteTID = rec.getPort();
					}

					// guarda nuevo TID remoto
					if (rec.getPort() != remoteTID) {
						SS(sock, ERROR(0, "wrong TID"));
						break;
					}

					// trama no llega del servidor
					if (code(rec) == DATA) {
						if ((seqnum(rec)) != seqnum) // trama de datos nueva
						{
							seqnum = seqnum(rec);
							t.startTimers();
							o.write(dat(rec));
						}
						SS(sock, (sent = ACK())); // envía (re)asentimiento
						if (state == recibiendo) {
							t.startTout();
						}
						if (rec.getLength() < 516) { //512 + 4 BYTES DEL PROTOCOLO
							state = acabando;
							t.stopTout();
						}
					} // última trama de datos

					if (code(rec) == ERROR) {
						state = espera;
						t.stopTimers();
					}
					break;// procesa la trama
				case close: // retorna a estado inicial,si pasa mucho tiempo sin recibir el ack
					state = espera;
					break;
				case tout: // retransmite
					if (state == recibiendo) {
						SS(sock, sent);
						t.startTout();
					}
					break; // reenvío de trama
				default:
					break;
				}
			}
			System.out.println("Cerrar fichero");
			o.close(); //cierras fichero
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // run

}
