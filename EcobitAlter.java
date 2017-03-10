import java.io.*;
import java.net.*;

//El protocolo utiliza 2 tipos de tramas:datos y asentimiento
//Trama de datos numeradas consecutivamente 
//   -Trama de asentimiento  asiente con el número de secuencia 
//	de la próxima trama esperada
//	-Número de secuencia:entero módulo 2. Primera trama lleva número de secuencia 0.
//  -Tamaño de ventana 1: sólo puede haber una trama sin asentir.
//  -Emisor retransmite al cabo de t segundos si no recibe asentimiento

public class EcobitAlter {

	static byte Idle = 0, WaitAck = 1;

	public static void main(String args[]) {
		boolean salir = false;
		String host = "localhost";
		int remotePort = 7; // puerto inicial del servidor
		byte state = 0; // al principio state=Idle
		int numlin = 0;
		if (args.length > 0) {
			host = args[0];
		}
		try {
			InetAddress a = InetAddress.getByName(host);
			DatagramSocket theSocket = new DatagramSocket();
			DatagramPacket dpout = null;
			BufferedReader lin = new BufferedReader(new InputStreamReader(
					System.in));
			while (!salir) {
				if (state == Idle) { // estado de envio
					System.out.println("introduzca linea (acabar con '.'):");
					String line = lin.readLine();
					if (line.equals(".")) {//envias el punto y sales en la siguiente iteración,el punto lo envias igual
						salir = true; // envía al servidor la línea final

					}
					byte[] dataenv = { (byte) (numlin % 2) }; // numero de
																// secuencia en
																// modulo 2
					line = (new String(dataenv)) + line; //creas String con el numero de linea y lo que leiste de teclado
					dataenv = line.getBytes(); //pasa el String a un array de bytes
					dpout = new DatagramPacket(dataenv, dataenv.length, a,
							remotePort);
					theSocket.send(dpout); // envia un datagrama al servidor y
					state = WaitAck; // cambia de estado para esperar
										// asentimiento
				} else {

					try {
						theSocket.setSoTimeout(2000); // genera una excepción
														// tras 2 seg.
						// de espera por un datagrama de asentimiento
						DatagramPacket dpin = new DatagramPacket(new byte[256],
								256);
						theSocket.receive(dpin);//recibes el echo del servidor(esperas el ack y los datos)
						remotePort = dpin.getPort(); // puerto de la hebra del
														// servidor
						dpout.setPort(remotePort); // que atiende a ese cliente
						byte[] datarec = dpin.getData();
						if (datarec[0] == (byte) ((numlin + 1) % 2)) { // Ack
																		// esperado
							System.out.println("recibido ack esperado: "
									+ datarec[0]);
							String s = new String(datarec, 1,
									dpin.getLength() - 1);
							System.out.println("eco " + numlin + " de "
									+ dpin.getAddress() + " : " + s);
							numlin++;
							state = Idle;
						}

						else { // Ack no esperado
							System.out.println("recibido ack NO esperado: "
									+ datarec[0] + " - retranmision");
							theSocket.send(dpout);		//vuelves a retransmitir
						}
					} catch (SocketTimeoutException e) { // no llega
															// asentimiento en 2
															// seg.
						System.out.println("timeout - retranmision");
						theSocket.send(dpout); //si salta excepcion vuelves a enviar el mismo paquete
					}
				}
			}

			theSocket.close();
		} catch (UnknownHostException e) {
			System.err.println(e);
		} catch (IOException e) {
		}
	}
}
