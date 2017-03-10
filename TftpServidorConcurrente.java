import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class TftpServidorConcurrente extends frame implements cons, Runnable { //heredas de frame k tiene toos los metodos

	private PipedInputStream entradaPipe;
	private LinkedList<DatagramPacket> paquetesRecibidos;
	private DatagramSocket socketServidor;
	private HashMap<Integer, HiloServidor> tablaHilosServidor;//para guardar los puertos y el objeto hiloservior que esta atendiendo

	public TftpServidorConcurrente() {
		
		try {
			
			tablaHilosServidor = new HashMap<Integer, HiloServidor>();
			
			socketServidor = new DatagramSocket(ServerPort);
			paquetesRecibidos = new LinkedList<DatagramPacket>();
		
			PipedOutputStream salidaPipe = new PipedOutputStream();
			entradaPipe = new PipedInputStream(salidaPipe);

			new Line(salidaPipe, paquetesRecibidos, socketServidor);//recibe la cola de mensajes (paquetes recibidos)
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		try {
			while (true) {
			
				int event = entradaPipe.read(); //leo el evento del pipe,mientras no genere evento estas durmiendo aqui
				
				if (event == frame) { //si lo ke lees del pipe es frame (es un mensaje que te está mandando Line)
					//es un pakete (rrq,error,ack)
					DatagramPacket paqueteRecibido;
					synchronized (paquetesRecibidos) { //sincronizas sobre la lista de paquetes recibidos
						paqueteRecibido = paquetesRecibidos.removeFirst();//sacas el primer elto.
					}
					
					//miro si el puerto ya está en la tabla hash
					int port=paqueteRecibido.getPort();
					
					boolean contiene_clave=tablaHilosServidor.containsKey(port);//miro si el ya habia hilo encargado de atener a ese cliente
					
					if (contiene_clave){ //ya estaba atendido por un HiloServidor
						
						tablaHilosServidor.get(port).pasarPaquete(paqueteRecibido);//le pasamos el pakete para k lo procese
						//si ya conozco al cliente, llamo a PasarPaquete
					}
					else{//si no se ha creado, creas un HiloServidor nuevo
						
						
						HiloServidor s=new HiloServidor(paqueteRecibido, socketServidor);//le pasas el paquete inicial(el RRQ sera siempre salvo error)
						//y tambien el socket
						new Thread(s).start();//llama ala metodo run del runnable
						tablaHilosServidor.put(port, s); //le paso el puerto del cliente,y el hilo.						
					}
					
				}
				else {
					System.out.println("Error");
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
