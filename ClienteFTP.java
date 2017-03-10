//Sergio Alvare Pelaez 2012
//Programa creado para la asignatura de servicios de comunicaciones basicos

//Diseniado para ser utilizado con el servidor ftp multihilo que se encuentra en un directorio adjunto al que contiene a este codigo
//Ejemplo de como se usa este programa: desde la linea de comandos escribir: Java Cliente "localhost"


import java.net.*;
import java.io.*;

import java.lang.*;

class ClienteFTP{

	private BufferedReader EntradaTexto;
	private PrintWriter SalidaTexto;
	
	private DataInputStream EntradaBytes;
	private BufferedOutputStream SalidaBytes;
	
	private BufferedReader teclado;

	public static void main(String []args){
	clienteFTP cliente=new clienteFTP();
	cliente.run(args);
	}
	
	
	
	void run(String []args){
		InetAddress direcc=null;
		try{
			direcc=InetAddress.getByName(args[0]); //hay que pasarle, 
			//por linea de comandos, el nombre de la maquina servidor con la que me quiero conectar
			//Ese nombre estara en la primera posicion del array de String llamado args
			}catch(UnknownHostException e){
			System.err.println("Host no encontrado: "+e);
			System.exit(-1);
			}
			
		int puerto=7;
		
			Socket sckt=null;
			
			

		
			try{
				sckt=new Socket(direcc,puerto);//recibe dos parametros, uno es la direccion de la maquina servidor a la que me
				//quiero conectar, y otro el puerto donde escucha esa maquina servidor a la que me quiero conectar
				
				//Abro canales de lectura y escritura por parte del cliente
				//canal de lectura de cadenas de caracteres desde el cliente:
				//BufferedReader EntradaTexto=new BufferedReader(new InputStreamReader(sckt.getInputStream()));
				EntradaTexto=new BufferedReader(new InputStreamReader(sckt.getInputStream()));	
				//canal de escritura de cadenas de caracteres desde el cliente:
				//PrintWriter SalidaTexto=new PrintWriter(new BufferedWriter(new OutputStreamWriter(sckt.getOutputStream())),true);
				SalidaTexto=new PrintWriter(new BufferedWriter(new OutputStreamWriter(sckt.getOutputStream())),true);
				//canal de lectura en modo binario desde el cliente:
				//DataInputStream EntradaBytes=new DataInputStream(sckt.getInputStream());
				EntradaBytes=new DataInputStream(sckt.getInputStream());
				//canal de escritura en modo binario desde el cliente:
				//DataOutputStream SalidaBytes=new DataOutputStream(sckt.getOutputStream());
				SalidaBytes=new BufferedOutputStream(sckt.getOutputStream());	
				//Abro un canal de lectura de teclado para poder mandarle ordenes al servidor desde el cliente
				//BufferedReader teclado= new BufferedReader(new InputStreamReader(System.in));
				teclado= new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Canales de comunicacion abiertos");
				
				//Ahora voy a enviarle la cadena			
				
				//TENEMOS QUE ENVIARLE EL args PERO RECORTADO SIN EL PRIMER ELEMENTO,
				//QUE ES EL LOCALHOST Y SOLO SIRVIO PARA ESTABLECER LA CONEXION CON EL 
				//SERVIDOR AL QUE LE ENVIO LA SIGUIENTE CADENA:
				

				String flag = EntradaTexto.readLine();
				if(flag.equalsIgnoreCase("hebraEnEsperaDeSolicitud"))
				{
					System.out.println("El servidor esta disponible");//debug
				
				
				String solicitud="";
				String orden="";
				String nombreFichero="";
					if(args.length>2)
					{
						solicitud=args[1]+" "+args[2];//"orden fichero"
						orden=args[1];
						nombreFichero=args[2];
						System.out.println("Hay tres argumentos");//debug
										//Envio la solicitud al servidor, que ya estara esperando por ella
				SalidaTexto.println(solicitud);
				System.out.println("Se ha enviado:");
				System.out.println(solicitud);
					}	
					
					else
					{
						solicitud=args[1];//"DIR"
						orden=args[1];
						System.out.println("Hay dos argumentos");//debug
										//Envio la solicitud al servidor, que ya estara esperando por ella
				SalidaTexto.println(solicitud);
				System.out.println("Se ha enviado:");
				System.out.println(solicitud);
					}
				
				

				
				if(orden.equalsIgnoreCase("DIR"))
				{
					
					String mensaje="";
					boolean salir=false;
					do
					{
						mensaje=EntradaTexto.readLine();
						System.out.println(mensaje);
						if(mensaje.equals("#FIN#")) {salir=true;}
					}while(salir==false);
				}
				
				//Basado en las mismas funciones del servidor; pero ante un GET, o ante un PUT, cuando uno envia,
				//el otro recibe.
				if (orden.equalsIgnoreCase("GET")) {RecibirFichero(nombreFichero);}
				if (orden.equalsIgnoreCase("PUT")) {
				System.out.println("La orden es un PUT.... ");
				
				EnviarFichero(nombreFichero);}
				
				
				
				
			}
				
			}catch(Exception e){
				System.err.println("Se ha producido la excepcion: "+e);
			}
			
			try{
				sckt.close();
			}catch(IOException e){
				System.err.println("Error al cerrar el socket: "+e);
			}
		
	}
	
	
	
void EnviarFichero(String nombrefichero) {
System.out.println("Funcion EnviarFichero en marcha.... ");
File fich= new File(nombrefichero);
long tamano;
System.out.println("Hemos llegado hasta antes del try.... ");//Debug
try {
if(fich.isFile()) {
System.out.println("superado el isfile");//Debug
SalidaTexto.println("El cliente esta calculando el tamanio del fichero que va a enviar");//Debug
tamano = fich.length(); // obtener el tamaño del fichero
System.out.println(Long.toString(tamano));
SalidaTexto.println(Long.toString(tamano));
//SalidaTexto.flush();
System.out.println("Enviado el tamanio.... ");
System.out.println(Long.toString(tamano));

String resp = EntradaTexto.readLine(); // lee READY
System.out.println("El servidor dice: "+resp);
EnviarBytes (fich, tamano); // enviar el fichero
} else {
SalidaTexto.println("ERROR");
}
} catch(Exception e) {
System.out.println("Error en el envio del fichero: " + e);
}
} // EnviarFichero

void EnviarBytes(File fich, long size) {

System.out.println("Funcion EnviarBytes en marcha.... ");
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
try {
String resp = EntradaTexto.readLine(); // lee OK
System.out.println("El servidor deberia contestar con un OK");
System.out.println("El servidor dice..."+resp);


String tamanoLeidoEnString=EntradaTexto.readLine();
System.out.println(tamanoLeidoEnString);
System.out.println("-----");
long tamano=Long.valueOf(tamanoLeidoEnString);
System.out.println(tamano);

SalidaTexto.println("READY"); // enviar comando READY al servidor

//long tamano = Integer.parseInt(EntradaTexto.readLine());//leo el tamanio: vale el parseint o el valueof como hice en otro sitio en el servidor
RecibirBytes(nombrefichero, tamano);
} catch(Exception e) {
System.out.println("Error en la recepción del fichero: " + e);
}
} // RecibirFichero
void RecibirBytes(String nomfich, long size){
int dato;
//nomfich="copiaenservidor_"+nomfich;
//System.out.println("cambio nombre fichero a: "+nomfich);
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