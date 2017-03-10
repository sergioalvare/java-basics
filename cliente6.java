//Sergio Alvare Pelaez 2012

import java.io.*;
import java.nio.*;  
import java.nio.channels.*;
import java.net.*;
import java.util.*; 

 
public class cliente6 
{ 
  public static int puertoPorDefecto = 7; 
   
  public static void main(String[] args) 
  { 
	System.out.println("Cliente corriendo...");
    String host= "localhost"; 
    int port; 
    if (args.length>0)  host=args[0]; 
    try { 
    	
      SocketAddress direccion= new InetSocketAddress (host, puertoPorDefecto); 
      SocketChannel cliente= SocketChannel.open(direccion);  
      ByteBuffer arrayDeBytes = ByteBuffer.allocate(128); //para leer lo que envia el servidor 
      WritableByteChannel out= Channels.newChannel(System.out); 
      cliente.configureBlocking(false);
      while (true) {  
       
        int n = cliente.read(arrayDeBytes);
        if (n>0) {  
             
          arrayDeBytes.flip(); 
          out.write(arrayDeBytes); 
          arrayDeBytes.clear(); 
        } 
      }  
  } catch (IOException e){ System.out.println(""); } 
 }         
} 