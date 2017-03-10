//Sergio Alvare Pelaez 2012

import java.io.*;
import java.nio.*;  
import java.nio.channels.*;
import java.net.*;
import java.util.*; 
 
 
public class servidor6 
{   
  public static int puertoPorDefecto = 7;
  
  private Byte numeroDeConexiones;
  
  public Byte getNumeroDeConexiones() {
	return numeroDeConexiones;
}
   
  public static void main(String[] args) 
  {
	    int puerto; 
	    try { 
	      puerto = Integer.parseInt(args[0]); 
	    } catch (Exception ex) { puerto = puertoPorDefecto; } 
	  new servidor6(puerto);
	  
  }
  
  public servidor6(int puerto) 
  {
	System.out.println("Servidor corriendo...");
    numeroDeConexiones = new Byte((byte)0); 
    ServerSocketChannel serverChannel; 
    Selector selector; 
    try { 
      serverChannel = ServerSocketChannel.open(); 
      ServerSocket ss = serverChannel.socket(); 
      InetSocketAddress address = new InetSocketAddress(puerto); 
      ss.bind(address); 
      serverChannel.configureBlocking(false); 
      selector = Selector.open(); 
      serverChannel.register(selector, SelectionKey.OP_ACCEPT); 
    } catch (IOException ex) { 
         System.out.println(""); 
         return;    
      } 
     
    while (true) {  
      try { 
        selector.select(); 
      } catch (IOException ex) { 
           ex.printStackTrace(); 
           break; 
        } 
      Set readyKeys = selector.selectedKeys(); 
      Iterator iterator = readyKeys.iterator(); 
      while (iterator.hasNext()) { 
        SelectionKey key = (SelectionKey) iterator.next(); 
        iterator.remove(); 
        try { 
          if (key.isAcceptable()) { 
            ServerSocketChannel servidor= (ServerSocketChannel) key.channel(); 
            SocketChannel client = servidor.accept(); 
            numeroDeConexiones++; 
            System.out.println("Numero de conexiones: "+numeroDeConexiones); 
            client.configureBlocking(false); 
            SelectionKey clientKey = client.register( 
              selector, SelectionKey.OP_WRITE);
            Timer t1 = new Timer();
            t1.schedule(new esclavoConexiones(clientKey, this), 5000, 5000);
          }
         
        } 
        catch (IOException ex) { 
          key.cancel(); 
          System.out.println("Numero de conexiones: "+--numeroDeConexiones); 
          try { 
            key.channel().close();  
          }  catch (IOException e) { System.out.println("");} 
        }  
      }
    }
  } 
}