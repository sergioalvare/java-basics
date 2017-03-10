import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

//Hay que hacer que una hebra, una vez se haya lanzado 
//al inicio, pida una conexion utilizando
//un metodo del servidor
//
//Se hizo algo parecido en la practica 2 para que la hebra
//avisase al servidor de que habia terminado y asi el servidor
//pudiera iniciar el volcado de los datos de la lista compartida 
//al fichero de resultados.

public class HebraEcoL extends Thread
{
	public int identificadorNumerico;
	//public Socket socketHebra;
	public boolean conexionDisponible;
	public LinkedList<Socket> lista;
	public ServidorEcoL maestro;
	public String theLine;
	public Socket socketHebra;
	public LineNumberReader netIn;
	public PrintWriter netOut;
	
	HebraEcoL(int i,LinkedList<Socket> listaGlobal,ServidorEcoL padre)
	{
		identificadorNumerico=i;
		this.lista=listaGlobal;
		this.maestro=padre;
		conexionDisponible=false;
				
		this.start();
	}
	
	public void run()
	{
		
		while(true)
		{
			
			synchronized(lista)
			{
				if(!lista.isEmpty())
				{


					try{
					Socket socketHebra=new Socket();
					socketHebra=lista.remove(0);
					
					netIn = new LineNumberReader(new InputStreamReader(socketHebra.getInputStream()));
					netOut = new PrintWriter(socketHebra.getOutputStream(), true);
					
					
					conexionDisponible=true;
					System.out.println("Conexion lista para ser atendida...");
					
					}catch(Exception socketycanales){System.out.println("Hubo un error con los canales");}
							
					// try{
					// LineNumberReader netIn = new LineNumberReader(new InputStreamReader(socketHebra.getInputStream()));
					// PrintWriter netOut = new PrintWriter(socketHebra.getOutputStream(), true);
					// }catch(Exception e){System.out.println("Hubo un fallo al abrir canales");}
					
				}
				
				else
				{
					conexionDisponible=false;
				}
				
				
			}//synch
			
			if(conexionDisponible==true)
			{
				
				
				System.out.println("Helloserver: connection accepted.");
				System.out.println("Le atiende la hebra");
				System.out.println(identificadorNumerico);
					
				
				boolean conversacion=true;
				while (conversacion==true) 
					{
						try{
						theLine = netIn.readLine();
						}catch(Exception exceptionconversacion){conversacion=false;System.out.println("Finalizo el bucle de conversacion");break;}
						/*if (theLine.equals(".")) 
							{
								conversacion=false;
								
							}
							*/
						netOut.println(theLine);
						//System.out.println(netIn.readLine());
					}
				
				
				conexionDisponible=false;
				

				
				//socketHebra.close();
				
				
				
			}
					
		}
			//}//syn
		

			
	}
}

