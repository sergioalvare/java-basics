import java.io.*;
import java.net.*;


public class ServEcoAlterHilos extends Thread {
	

	DatagramSocket dsock;
	DatagramPacket dp;
	
	public ServEcoAlterHilos(DatagramPacket dp) {//constructor de la clase,
		//recibe como parámetro el DatagramPacket recibido
		
		this.dp=dp;
		
	}

	public void run(){
		
		try{

				//recibo la 1era vez a ver si recibo un '.'
				byte [] datos = dp.getData();//devuelve array de datos
				String dato = new String(datos, 1, dp.getLength() - 1);//obtengo los datos
				//creo el String con tods los bytes menos el primero
				
				dsock=new DatagramSocket(); //creo dsocket en puerto aleatorio
				
				
				do{
					
					datos = dp.getData(); // el primer byte del campo de datos del datagrama contiene 
					//el numero de secuencia y el resto de los bytes la linea de eco transmitida
					
					byte numlin = (byte)((datos[0] + 1) % 2);//obtengo el numero de secuencia,
					//y lo incremento en uno,es decir, el numero de secuencia que voy a enviar
					
					dato = new String(datos, 1, dp.getLength() - 1);//obtengo los datos,desde 1 al tamaño menos 1.
					
					byte [] arrayNumlin = {numlin};//meto datos en array d bytes
					
					String datosAEnviar = (new String(arrayNumlin)) + dato; ////////////////////////////////////!!!!!!!
					byte [] bufferAEnviar = datosAEnviar.getBytes();// creo el buffer de bytes a enviar
					
					
					if (!dato.equals(".")) {
						DatagramPacket dpSalida=new DatagramPacket(bufferAEnviar, bufferAEnviar.length, dp.getAddress(), dp.getPort());//creo el
						//DatagramPacket que voy a enviar.con el buffer,su tamaño,y con la direccion y el puerto del DatagramPacket
						dsock.send(dpSalida); //envio el eco
						dsock.receive(dp);//y vuelvo a recibir, la siguiente trama
					}

				}while(!dato.equals("."));//si recibo un '.' dejo de devolver el eco
				
				System.out.println("Hilo finalizado");//el hilo del servidor muere
			
		}catch(IOException e){
			e.printStackTrace();
		};	
	}
}
