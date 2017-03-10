//Esta clase HebraBuscadoraPalabras es utilizada por DirSearch.java

import java.io.*;
import java.util.*;
import java.lang.*;

public class HebraBuscadoraPalabras extends Thread
{
	String palabraABuscar;
	File ficheroAExplorar;
	LinkedList<String> listaDeLaHebra;
	DirSearchVersion4 programaQueMeHaCreado;
	File ficheroResultadosHebra;
	
	//Lo siguiente es para avisar al programa principal de que las hebras han acabado
	//y asi puede volcar la lista en el fichero e imprimirlo por pantalla.
	//Para ello me he basado en laspaginas 14 y 15 de scbprac02.pdf

	//
	
	//Constructor:
	HebraBuscadoraPalabras(String palabra,File ficheroQueHayQueExplorar,LinkedList<String> listaGlobal,DirSearchVersion4 padre,File ficheroResultados)
	{
		try{
		//Las siguientes dos lineas se basan en la pagina 13 de scbprac02.pdf:
		this.palabraABuscar=palabra;
		this.ficheroAExplorar=ficheroQueHayQueExplorar;
		this.listaDeLaHebra=listaGlobal;
		this.programaQueMeHaCreado=padre;	
		this.ficheroResultadosHebra=ficheroResultados;
		//"ASI CONSEGUIMOS QUE LOS PUNTEROS LOCALES APUNTEN AL GLOBAL"

		}catch(Exception e1){System.out.println("Hubo un fallo pero la vida sigue a partir de e1");}
	}
	
	public void run()
	{
		try{
		int numeroDeLinea=1;//es un contador
		String nombreFichero=ficheroAExplorar.getName();
		
		BufferedReader EntradaTexto=new BufferedReader(new FileReader(ficheroAExplorar));
		/*
		BufferedReader EntradaTexto=new BufferedReader(new FileReader(nombreFichero));//abro el fichero a explorar
		//}catch(Exception e){System.out.println("Hubo un fallo pero lavida sigue";}
		*/
		//Ahora vamos leyendo el fichero linea a linea
		boolean salirDelBucle=false;
		synchronized(listaDeLaHebra)
					{
		do{
		String lineaDelFichero=EntradaTexto.readLine();

		//System.out.println("ESTAMOS BUSCANDO "+palabraABuscar);
		
		if(lineaDelFichero!=null)
		{
			//System.out.println("se leyo: "+lineaDelFichero);
			StringTokenizer tokens=new StringTokenizer(lineaDelFichero);
			//for(int i=0;i<tokens.length;i++)
			while(tokens.hasMoreTokens())
			{
				String palabraDelFichero=tokens.nextToken();
				//System.out.println("palabra leida "+palabraDelFichero);
				
				////
				boolean coincidencia=palabraDelFichero.equals(palabraABuscar);
				//System.out.println("coincidencia "+coincidencia);
				///
				
				if(coincidencia==true)
				{
					String numeroDeLineaEnString=Integer.toString(numeroDeLinea);
					String datosAEscribir1=nombreFichero+" linea "+numeroDeLineaEnString;
					String datosAEscribir2=lineaDelFichero;
					//System.out.println("datosAEscribir1 :"+datosAEscribir1);
					//System.out.println("datosAEscribir2 :"+datosAEscribir2);
					
					
					
					//Ahora volcamos ambos Strings en la lista, con la precaucion de reservarla para
					//esta hebra y que no se ponga otra hebra a escrbir en medio
					//pues ambos Strings se dice en el enunciado de la practica, deben ir
					//consecutivos.
					synchronized(listaDeLaHebra)
					{
						listaDeLaHebra.add(datosAEscribir1);
						
						listaDeLaHebra.add(datosAEscribir2);
						;//escribimos sobre la lista que es un 
						//atributo de esta hebra. Esta lista esta apuntando a la lista global creo por eso lo //del this. lo que sea, que creo que es para direccionar el parametro local al parametro
						//que se haya creado (la lista global) en el proceso maestro (el main de DirSearch)
						//Preguntar en clase a ver si esta bien pensao
					
					
					synchronized(ficheroResultadosHebra)
					{
						String nombreFicheroResultados=ficheroResultadosHebra.getName();
						volcarDeListaAFichero(listaDeLaHebra,ficheroResultadosHebra);
						
					
					}
					
					}
					
				
				}
				
				//else {System.out.println("No hubo coincidencia --> no hacemos nada");}
			}
		}
			
		else
		{
			salirDelBucle=true;
		}
		
		numeroDeLinea++;
		
		}while(salirDelBucle==false);
		}
	
	}catch(Exception e10){System.out.println("Hubo un fallo pero la vida sigue");}
	programaQueMeHaCreado.finalizoUnaHebra();
	}
	
	public static void volcarDeListaAFichero(LinkedList<String> lista, File ficheroResultadosHebra) throws IOException
{
	
	/* No lopodemos hacer con lo siguiente:
	BufferedWriter bw=new BufferedWriter(new FileWriter(nombreFicheroResultados));
	Porque el BufferedWriter empieza a escribir desde laprimera linea del fichero.
	Para controlarlo, lo mejor es pasar de leer la primera linea a null y escribir en ella
	y, en su lugar, usar la instruccion printwriter.
	*/
	
	try
	{
	
	FileWriter w = new FileWriter(ficheroResultadosHebra,true);
	BufferedWriter bw = new BufferedWriter(w);
	PrintWriter wr = new PrintWriter(bw);  
	
	
	//Vease pag 45 del pdf de la practica 1 para la siguiente instruccion:
	//FileOutputStream ficheroResultados=new FileOutputStream(nombreFicheroResultados);
	//Para lo siguiente, vease la pagina 12 de scbprac02.pdf
	while(!lista.isEmpty())
	{
		String cadenaAEscribir=lista.remove(0);
		//bw.write(cadenaAEscribir);
		
		wr.println(cadenaAEscribir);
			//wr.write(cadenaAEscribir);//escribimos en el archivo
			//wr.append(" - y aqui continua"); //concatenamos en el archivo sin borrar lo existente
			//ahora cerramos los flujos de canales de datos, al cerrarlos el archivo quedará guardado con información escrita
			//de no hacerlo no se escribirá nada en el archivo

		
	}
	//bw.close();
	
	wr.close();
	bw.close();
	
	}catch(IOException e){};
	
	//System.out.println("IMPORTANTE: VOLCADO DE LISTA A FICHERO: FINALIZADO");
}
	
}