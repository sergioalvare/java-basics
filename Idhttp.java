//Hay que pasarle orden.txt como parametro: java Idhttp orden.txt y hacemos enter en la consola de comandos

//package cosas;

import java.io.*;
import java.util.*;



public class Idhttp {
	public static void main (String [] args) throws IOException
	{
		
		String nomFich=args[0];
		
		//try{
			BufferedReader EntradaTexto=new BufferedReader(new FileReader(nomFich));//peticiones.txt
			//}catch(Exception e){System.out.println("error "+e);}
			
		String peticion=EntradaTexto.readLine();
		System.out.println("recibido "+peticion);//PARA PRUEBAS
		
		StringTokenizer tokens=new StringTokenizer(peticion);
		String comando=tokens.nextToken();//esto extrae el primer token que deberia ser un GET
		
		if(comando.equals("GET"))
		{
			comando=tokens.nextToken();
			if(comando.endsWith("/"))
				comando+="index.html";
				comando=comando.substring(1);//quita el slash inicial del nombre del fichero
				File fil=new File(comando);
				if(!fil.exists())
					cabecera_MIME("404 File Not Found",comando);
				else
				{
					cabecera_MIME("200 OK",comando);
					if(comando.endsWith(".html") || comando.endsWith(".htm"))
						ficherotextual(comando);
							
				}
		
		}
		else
		{
			System.out.println("no implementado");
		}
			//}catch(Exception e){System.out.println("error "+e);}
	}
		
		
	
	public static void cabecera_MIME(String resp,String nomFich)
	{
		System.out.println("HTTP/1.0 "+resp);
		Date now=new Date();//esto lee la fecha del sistema
		System.out.println("Date: "+now);
		System.out.println("Server: miSuperOrdenador");
		if(nomFich.endsWith(".html")||nomFich.endsWith(".htm"))
			{System.out.println("Content-type: text/html");}
		else 
			{System.out.println("Content-type: image/jpg");}
		
		System.out.println("\r\n");
		
	}
	
	public static void ficherotextual(String nomFich) throws IOException
	{
		//METER LO SIGUIENTE EN UN TRY CATCH!!!!
		FileReader fich=new FileReader(nomFich);//con esto abrimos el fichero
		
		//ahora vamos a leerlo linea a linea:
		BufferedReader fichBR=new BufferedReader(fich);
		
		//voy leyendo linea a linea:
		String linea=fichBR.readLine();
		while(linea!=null)
		{
			System.out.println(linea);
			linea=fichBR.readLine();
		}
		
		fichBR.close();
		}
	}
	


