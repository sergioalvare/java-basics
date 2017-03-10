import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

public class ServidorEcoL
{

	
	public int numeroDeHebrasDelServidor;
	public Socket conexion;



	//Constructor:
	ServidorEcoL()
	{

		numeroDeHebrasDelServidor=3;//Porque en el enunciado se pide contar con tres hebras
	}
	
	//Metodo que contiene todo lo que hace el servidor una vez se arranca
	//desde el main:
	void aTrabajar()
	{
		try
		{
			
			LinkedList<Socket> listaGlobal=new LinkedList<Socket>();
			
			int puertoServidor=7;
			//Creamos el socket del servidor:
			ServerSocket serv=new ServerSocket(puertoServidor);
			System.out.println("Servidor escuchando en el puerto 7");
			
			//Declaramos las 3 hebras.
			for(int i=0;i<numeroDeHebrasDelServidor;i++)
			{
				HebraEcoL hebra=new HebraEcoL(i,listaGlobal,this);//hay que pasarle un numero de identificador
			}
			System.out.println("Hebras construidas");
			
			//Permanecemos a la espera de una conexion
			while(true)
			{	
				Socket conexion=serv.accept();//si se pasa de esta linea es porque se ha recibido una conexion.
				//Escribo el socket en una lista compartida por el servidor y todas las hebras:
				System.out.println("Conexion recibida");
				synchronized(listaGlobal)
				{
					listaGlobal.add(conexion);
					System.out.println("Conexion en cola...");
				}
				
			}
			
		}catch(Exception e) {System.out.println("Hubo un error pero la vida sigue");}
	}
	
	//El main crea un objeto servidor y lo echa a andar usando la funcion "aTrabajar"
	public static void main(String args[])
	{
		ServidorEcoL servidorDeEco=new ServidorEcoL();
		servidorDeEco.aTrabajar();
	}

	
}