import java.net.*; import java.io.*; import java.util.*;
public class requesthttp2 implements Runnable {

private static LinkedList<Socket> pool = new LinkedList<Socket>();

private File baseDirectory;
private String indexFileName= "index.html";
private Socket connection;
private int identificadorHebra;

public requesthttp2(File bDir, String indexFich,int i) {
try {
baseDirectory = bDir.getCanonicalFile();
identificadorHebra=i;
} catch (IOException ex) {
System.out.println ("requesthttp2 excepcion: "+ ex);
}
if (indexFich != null) indexFileName= indexFich;
//connection= request;
}


public void run() {

String root = baseDirectory.getPath(); // para chequeo de seguridad
while (true) {

synchronized (pool) {
while (pool.isEmpty()) {
try {
pool.wait();
} catch (InterruptedException ex) {}
}
connection = (Socket) pool.remove(0);

System.out.println("Conexion atendida por la hebra: ");
System.out.println(identificadorHebra);

}

try {
String filename;
String contentType= "text/plain";
int tamano=0;
BufferedOutputStream raw= new
BufferedOutputStream(connection.getOutputStream());
PrintWriter out= new PrintWriter(raw);
InputStreamReader in= new InputStreamReader(
new BufferedInputStream(connection.getInputStream()),"ASCII");
StringBuffer requestLine= new StringBuffer();
int c;
while (true) {
c = in.read();
if (c == '\r' || c == '\n') break;
requestLine.append((char) c);
}
String get = requestLine.toString();
System.out.println("recibido: "+ get); // logea la peticion
String [] peticion = get.split(" ");
String method = peticion[0];
String version = "";
if (method.equals("GET")) {
filename = peticion[1];
if (filename.endsWith("/")) filename+= indexFileName;
contentType = guessContentTypeFromName(filename);
File theFile = new File(baseDirectory,
filename.substring(1,filename.length()));
if (theFile.canRead() // No permite acceder fuera del directorio base
&& theFile.getCanonicalPath().startsWith(root)) {
DataInputStream fis = new DataInputStream(
new BufferedInputStream(
new FileInputStream(theFile)));
byte[] theData = new byte[(int) theFile.length()];
tamano= theData.length;
fis.readFully(theData);
fis.close();
cabeceraMIME (out, "200 OK", tamano, contentType);
// envia una cabecera MIME
raw.write(theData); // env�a el fichero en modo binario
raw.flush();
} else { // no encuentra el fichero
// cabeceraMIME (out, "404 File Not Found", tamano, contentType);
/*
tamano= 119;
contentType= "text/html";
cabeceraMIME (out, "200 OK", tamano, contentType);
out.write("<HTML>\r\n");
out.write("<HEAD><TITLE>File Not Found</TITLE>\r\n");
out.write("</HEAD>\r\n");
out.write("<BODY>");
out.write("<H1>HTTP Error 404: File Not Found</H1>\r\n");
out.write("</BODY></HTML>\r\n ");
out.flush();
*/
}
}
else { // m�todo no "GET"
cabeceraMIME (out, "501 Not Implemented", tamano, contentType);
}
} catch (IOException ex) {}
finally {
try {
System.out.println("cierro la conexion");
connection.close(); break; // para salir del while
} catch (IOException ex) {}
} } }

String guessContentTypeFromName(String name) {
if (name.endsWith(".html") || name.endsWith(".htm")) {
return "text/html";
} else if (name.endsWith(".txt") || name.endsWith(".java")) {
return "text/plain";
} else if (name.endsWith(".gif")) {
return "image/gif";
} else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
return "image/jpeg";
} else if (name.endsWith(".class")) {
return "application/octet-stream";
} else return "text/plain";
} // guessContentTypeFromName
void cabeceraMIME (PrintWriter wout, String resp, int tamano, String ctype){
wout.println("HTTP/1.0 " + resp);
Date now = new Date();
wout.println("Date: " + now);
wout.println ("Server: jhttp1/1.0");
if (resp.indexOf("200 OK")>=0)
wout.println("Content-length: "+ tamano);
wout.println("Content-type: "+ ctype);
wout.println(); // envia dos saltos de linea seguidos
wout.flush();
}


public static void processRequest(Socket request) {
synchronized (pool) {
pool.add(pool.size(), request);
pool.notifyAll();
}
}

} // requesthttp2
