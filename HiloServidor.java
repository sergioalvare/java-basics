import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

public class HiloServidor extends frame implements cons, Runnable { //heredas de frame, luego no lo haces Thread(o heredas de Thread o de frame)
	//la creas runnable y en el Thread le pasas el Runnable como parametro, y te lanza el hilo usano el run del Runnable
	
	private int enviando = 3;
	
	private PipedInputStream entradaPipe;
	private PipedOutputStream salidaPipe;
	private Timer timer;
	private LinkedList<DatagramPacket> paquetesRecibidos;
	
	public HiloServidor(DatagramPacket paqueteInicial, DatagramSocket socket){

		try {

			salidaPipe = new PipedOutputStream();
			entradaPipe = new PipedInputStream(salidaPipe);
		
			timer = new Timer(salidaPipe);
			
			paquetesRecibidos = new LinkedList<DatagramPacket>();
			
			rec = paqueteInicial; //guardo en rec el paquete inicial para usarlo despues
			sock = socket; 
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pasarPaquete(DatagramPacket paquete) { //cuando recibes paquete en el padre, coges y recibes el pakete
		//se llama desde el servidor principal
		
		try {
			synchronized (paquetesRecibidos) {
				paquetesRecibidos.addLast(paquete); //apila pakete en la lista, y luego cuando el pipe gener un evento lo recoje(hace un remove)
			}
			salidaPipe.write((byte)frame); //escribe frame al pipe
			salidaPipe.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		
		try {
			
			FileInputStream entradaFichero; //leer fichero
			int estado;
			byte [] buffer = new byte[516]; //crea el buffer para guardar info al cliente
			int leido;
			
			remoteTID = rec.getPort(); //obtienes el puerto del clinete para saber a donde enviar paketes
			a = rec.getAddress(); //direccion dle cliente
			
			
			
			//PARA EL PRIMER PAKETE (que fue enviado en el constructor)
			if (code(rec) == RRQ) { //si mensaje es RRQ
				
				String nombreFichero = file(rec); //lees nombre de fichero,está en frame
				String modo = mode(rec); //y el modo del fichero
				
				File fichero = new File(nombreFichero); //abres el fichero
				if (fichero.exists()) { //si existe
					entradaFichero = new FileInputStream(fichero); //creo el file inpout stream para leer dl fichero
					estado = enviando; //y pongo erstado a envindo
					
					leido = entradaFichero.read(buffer, 4, 512); //lees 1er pakete y lo envio
					
					seqnum = 1; //numero de secuencia del pakete, data 1,2...
					sent = DATA(buffer, leido + 4); //creas el pakete de datos y lo envias
					SS(sock, sent);
					
					timer.startTimers(); //inicias el Timer por si no recibes el ack
					if (leido != 512) {  //si menor, pasas a estado de espera
						estado = acabando;
					}
				}
				else { // si no existe el fichero
					sent = ERROR(1, "File not found");
					SS(sock, sent);
					return;
				}
			}
			else { //si el primer pakete no fue un RRQ
				System.out.println("Error");
				return;
			}
			
			//PARA LOS SIGUIENTE PAKETES
			while (estado != espera) {

				int evento = entradaPipe.read(); //LEES QUE PASÓ
			
				switch (evento) {
				
				case frame: //SI SE RECIBIO PAQUETE VOY A ENVIAR EL RESTO
					
					synchronized (paquetesRecibidos) {
						rec = paquetesRecibidos.removeFirst(); //sacas de la cola el primer pakete pendiente de la cola. 
					}

					if (code(rec) == ACK) {//si el pakete es ack envio el siguiente 
						
						if (estado == acabando) { //si es el ACK del ultimo, paro el timer y finalizo,pongo estado a espera
							estado = espera;
							timer.stopTimers();
							break;
						}
						
						if ((seqnum(rec)) == seqnum) // si no es acabando le envio el siguiente pakete, y miro si este es el ultimo pakete o no para poner estado=acabando
						{
							seqnum = seqnum(rec) + 1;
							leido = entradaFichero.read(buffer, 4, 512);//lees el siguiente pake te del fichero
							if (leido != 512) {
								estado = acabando;
							}
						}//si numero de secuencia estuviese mal seria un reenvio
						//envias :
						sent = DATA(buffer, leido + 4);
						SS(sock, sent);
						timer.startTimers();

					} 

					if (code(rec) == ERROR) {
						estado = espera;
						timer.stopTimers();
					}
					break;// procesa la trama
					
				case tout: //reenvio
					sent = DATA(buffer, leido + 4);
					SS(sock, sent);
					timer.startTimers();
					break;
					
				}
			}
			
			entradaFichero.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
