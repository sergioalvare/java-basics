import java.io.IOException;
import java.net.*;

class frame implements cons {
	InetAddress a; // direccion IP
	int seqnum = 0; // numero de secuencia
	int remoteTID; // puerto del servidor
	DatagramSocket sock; // socket principal
	DatagramSocket auxsock; // socket auxiliar para tftpservidor
	DatagramPacket sent, rec; // Paquetes enviado y recibido

	boolean firstDATAframe() {
		return ((code(rec) == DATA) && (remoteTID == ServerPort));
	} // predicado para detectar si es la primera trama enviada
		// es decir, si el puerto del servidor es el 69

	String ch(int b) {
		byte[] bb = { (byte) b };
		return new String(bb);
	}

	// convierte el primer byte de un int a String

	int code(DatagramPacket d) {
		return ((((d.getData())[0]) << 8) | ((d.getData())[1]));
	} // devuelve como int código: el primer byte del campo
		// de datos de d desplazado a la izquierda junto con el segundo byte

	int seqnum(DatagramPacket d) {
		int a = d.getData()[2];
		int b = d.getData()[3];
		a = a < 0 ? a + 256 : a;
		b = b < 0 ? b + 256 : b;
		return a * 256 + b;
	} // devuelve el número de bloque de una trama de DATA o ACK
		// obtiene el número a partir del tercer y cuarto byte de la trama

	byte[] dat(DatagramPacket d) {
		byte[] b = new byte[(d.getLength() - 4)];
		System.arraycopy(d.getData(), 4, b, 0, (d.getLength() - 4));
		return b;
	} // devuelve el campo de datos de una trama como array de bytes

	String file(DatagramPacket d) {
		int i;
		byte[] b = d.getData(); // obtiene la trama como array de bytes
		for (i = 2; b[i] != 0; i++) {
			;
		}
		; // detección del delimitador
		return new String(b, 2, (i - 2)); // conversion ascii a String
	} // devuelve como String el nombre del fichero de una trama RRQ o WRQ

	String mode(DatagramPacket d) {
		int del, i;
		byte[] b = d.getData(); // obtiene la trama como array de bytes
		for (i = 2; b[i] != 0; i++) {
			;
		}
		; // detección del delimitador
		del = i;
		for (i = del + 1; b[i] != 0; i++) {
			;
		}
		; // segundo delimitador
		return new String(b, (del + 1), (i - (del + 1))); // ascii a String
	} // devuelve como String el mode de una trama RRQ o WRQ

	String errormsg(DatagramPacket d) {
		int i;
		byte[] b = d.getData();
		for (i = 4; b[i] != 0; i++) {
		}
		;
		return new String(b, 4, (i - 4));
	} // devuelve como String el mensaje de error de una trama de ERROR

	DatagramPacket RRQ(String file, String type) throws IOException {
		byte[] b = (ch(0) + ch(RRQ) + file + ch(0) + type + ch(0)).getBytes();
		return new DatagramPacket(b, b.length, a, remoteTID);
	} // construye un DatagramPacket con una trama RRQ

	DatagramPacket WRQ(String file, String type) throws IOException {
		byte[] b = (ch(0) + ch(WRQ) + file + ch(0) + type + ch(0)).getBytes();
		return new DatagramPacket(b, b.length, a, remoteTID);
	} // construye un DatagramPacket con una trama WRQ

	DatagramPacket ERROR(int code, String msg) throws IOException {
		byte[] b = (ch(0) + ch(ERROR) + ch(0) + ch(code) + msg + ch(0))
				.getBytes();
		return new DatagramPacket(b, b.length, a, remoteTID);
	} // construye un DatagramPacket con una trama ERROR

	DatagramPacket DATA(byte[] b, int l) throws IOException {
		b[0] = 0;
		b[1] = DATA;
		b[2] = (byte) (seqnum / 256);
		b[3] = (byte) (seqnum % 256);
		return new DatagramPacket(b, l, a, remoteTID);
	} // construye un DatagramPacket con una trama DATA

	DatagramPacket ACK() throws IOException {
		byte[] b = { 0, ACK, (byte) (seqnum / 256), (byte) (seqnum % 256) };
		return new DatagramPacket(b, b.length, a, remoteTID);
	} // construye un DatagramPacket con una trama ACK

	void SS(DatagramSocket s, DatagramPacket p) throws IOException {
		s.send(sent = p);
		// printframe(p);
	} // envía el DatagramPacket y lo guarda en sent para retransmisión

	void printframe(DatagramPacket dp) {
		byte[] b = dp.getData();
		if (code(dp) == RRQ) {
			System.out.println("RRQ" + " file=" + file(dp) + " mode="
					+ mode(dp) + " remotePort:" + dp.getPort() + " len:"
					+ dp.getLength() + " Addr:" + dp.getAddress());
		}
		;
		if (code(dp) == WRQ) {
			System.out.println("WRQ" + " file=" + file(dp));
		}
		; // /
		if (code(dp) == DATA) {
			System.out.println("DATA" + " seqnum=" + seqnum(dp));
		}
		;
		if (code(dp) == ACK) {
			System.out.println("ACK" + " seqnum=" + seqnum(dp));
		}
		;
		if (code(dp) == ERROR) {
			System.out.println("ERROR error code=" + seqnum(dp)
					+ " error message: " + errormsg(dp));
		}
		;
	}// printframe

	public String evento(int ev) {
		switch (ev) {
		case frame:
			return "frame";
		case close:
			return "close";
		case tout:
			return "time-out";
		default:
			return "evento no identificado";
		} // switch
	} // evento

	public String estado(int st) {
		switch (st) {
		case espera:
			return "espera";
		case recibiendo:
			return "recibiendo";
		case acabando:
			return "acabando";
		default:
			return "estado no identificado";
		} // switch
	} // estado
}