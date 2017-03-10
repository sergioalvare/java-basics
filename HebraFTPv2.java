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

public class HebraFTPv2 extends Thread
{
	public int identificadorNumerico;
	public boolean conexionDisponible;
	public LinkedList<Socket> lista;
	public ServidorFTP maestro;//!!!! esto requiere que esa clase exista obviamente
	public String theLine;
	public Socket socketHebra;
	private DataInputStream EntradaBytes;
	private BufferedOutputStream SalidaBytes;
	private BufferedReader EntradaTexto;
	private PrintWriter SalidaTexto;
	
	HebraFTPv2(int i,LinkedList<Socket> listaGlobal,ServidorFTP padre)
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
					

					
					EntradaBytes = new DataInputStream(socketHebra.getInputStream());
					SalidaBytes= new BufferedOutputStream(
					socketHebra.getOutputStream());
					// obtener los flujos de entrada y salida del socket
					// para la transferencia de texto
					EntradaTexto = new BufferedReader(
					new InputStreamReader(socketHebra.getInputStream()));
					SalidaTexto = new PrintWriter(socketHebra.getOutputStream(), true);
					System.out.println("La hebra ha podido abrir los canales de bytes y texto");
					conexionDisponible=true;
					System.out.println("Conexion lista... para ser atendida...");
					}catch(Exception socketycanales){System.out.println("Hubo un error con los canales");}
				}
				
				else
				{
					conexionDisponible=false;
				}
				
				
			}//synch
			
			if(conexionDisponible==true)
			{
				
				
				System.out.println("HebraFTP: connection accepted.");
				System.out.println("Le atiende la hebra");
				System.out.println(identificadorNumerico);
				
				String solicitud;				
				String orden;
				String nombrefichero;


				try{
				SalidaTexto.println("hebraEnEsperaDeSolicitud");//
				solicitud = EntradaTexto.readLine();
				System.out.println("Recibido... "+solicitud);
				
				String separador=" ";
				String solicitud_a_trozos[]=solicitud.split(separador);
				orden=solicitud_a_trozos[0];
				System.out.println(orden);
				String nombreFichero="";//no necesario para DIR e instrucciones sin un nombre 
				if(solicitud_a_trozos.length>1)
				{
					nombreFichero=solicitud_a_trozos[1];//en caso de que no sea un dir, vienen dos parametros separados por un espacio en solicitud
					System.out.println(nombreFichero);
				}	
				
				
			
				
				if (orden.equalsIgnoreCase("DIR")) {
				
				System.out.println("Se solicito un DIR");
				ListarFTP();
				
				}
				if (orden.equalsIgnoreCase("PUT")) {RecibirFichero(nombreFichero);}//Ante un PUT, el servidor es quien recibe el fichero
				if (orden.equalsIgnoreCase("GET")) {EnviarFichero(nombreFichero);}//Ante un GET, el servidor el quien envia el fichero
				
				
				
			
				//conversacion=false;
				
			
			
			conexionDisponible=false;


				
				//socketHebra.close();
				}catch(Exception bucleconversacion){System.out.println("Hubo un error en el bucle de conversacion o logica de negocio");}
					//}//while
					
			}
		
	}
	
	}
	
	
	
	
void ListarFTP() {

System.out.println("Listando...");
int i;
File nomdir= new File(".");
File[] listfich= nomdir.listFiles();
try {
// enviar la lista de ficheros
for(i= 0; i < listfich.length; i++)
{
if (listfich[i].isFile())
SalidaTexto.println(listfich[i].getName());

SalidaTexto.flush();
}
SalidaTexto.println("#FIN#");
System.out.println("El DIR se ha realizado con exito. Por lo menos, del lado del servidor.");
} catch(Exception e) {
System.out.println("Error en el listado del sitio FTP: "+e);
}
} // ListarFTP

void EnviarFichero(String nombrefichero) {
File fich= new File(nombrefichero);
long tamano;
try {
if(fich.isFile()) {
SalidaTexto.println("OK"); // enviar contestacion
tamano = fich.length(); // obtener el tamaño del fichero
SalidaTexto.println(tamano); // enviar el tamaño
//SalidaTexto.flush(); creo que con el true al generar SalidaTexto se genera en modo autoflush. Si no, descomentar esta linea
String resp = EntradaTexto.readLine(); // lee READY
EnviarBytes (fich, tamano); // enviar el fichero
} else {
SalidaTexto.println("ERROR");
}
} catch(Exception e) {
System.out.println("Error en el envio del fichero: " + e);
}
} // EnviarFichero

void EnviarBytes(File fich, long size) {
try {
BufferedInputStream fichbis= new BufferedInputStream(
new FileInputStream(fich));
/*
// lee el fichero byte a byte
int dato;
System.out.println("leo el fichero byte a byte ");
for(long i= 0; i< size; i++) {
dato = fichbis.read();
SalidaBytes.write(dato);
}
*/
/*
// lee el fichero por bloques de bytes
int leidos= 0;
byte[] buffer= new byte [1024];
System.out.println("leo el fichero por bloques de bytes ");
while (leidos!=-1) {
leidos= fichbis.read (buffer);
if (leidos!=-1)
SalidaBytes.write (buffer, 0, leidos);
}
*/
// lee el fichero completo de golpe
DataInputStream fichdis= new DataInputStream (fichbis);
System.out.println("leo el fichero completo de golpe ");
byte[] buffer= new byte [(int) fich.length()];
fichdis.readFully (buffer);
SalidaBytes.write (buffer);
SalidaBytes.flush();
fichbis.close();
} catch(Exception e) {
System.out.println("Error en el envio del fichero binario: "+e);
}
} // EnviarBytes

void RecibirFichero(String nombrefichero) {

long tamano=0;
try {
// leer el tamaño del fichero

System.out.println("El cliente deberia decir algo distinto de null: ");//Debug
String mensajeDeCalculoDeTamanio=EntradaTexto.readLine();
System.out.println(mensajeDeCalculoDeTamanio);
System.out.println("Voy a leer el tamanio de lo que tengo que recibir....");
String tamanoLeidoEnString=EntradaTexto.readLine();
System.out.println(tamanoLeidoEnString);
System.out.println("-----");
tamano=Long.valueOf(tamanoLeidoEnString);
System.out.println(tamano);

/*
String tamanoEnString=EntradaTexto.readLine();
System.out.println("Leido");//Debug
System.out.println(tamanoEnString);//Debug
long tamano=Long.valueOf(tamanoEnString);
*/

/*
String tamanoEnString=EntradaTexto.readLine();
System.out.println(tamanoEnString);
//long tamano=Long.valueOf((EntradaTexto.readLine()));
System.out.println("Leido");//Debug
System.out.println(tamanoEnString);//Debug
long tamano=Long.valueOf(tamanoEnString);
*/



SalidaTexto.println("READY"); // enviar comando READY al cliente
//SalidaTexto.flush();
RecibirBytes(nombrefichero, tamano);
} catch(Exception e) {
System.out.println("Error en la recepción del fichero: " + e);
}
} // RecibirFichero
void RecibirBytes(String nomfich, long size){
int dato;
nomfich="copiaenservidor_"+nomfich;
System.out.println("cambio nombre fichero a: "+nomfich);
try {
BufferedOutputStream fichbos= new BufferedOutputStream(
new FileOutputStream(nomfich));
for(long i= 0; i<size; i++) {
dato = EntradaBytes.readByte();
fichbos.write(dato);
}
fichbos.close();
} catch(Exception e) {
System.out.println("Error en la recepcion del fichero binario: " + e);
}
} // RecibirBytes
	
	
	
	
	
	}