//Ejemplo de uso: "java jhttp2 ." El punto es sinonimo del directorio donde esta el .class

import java.net.*; import java.io.*;
public class jhttp2 {
private File baseDirectory;
private String indexFileName= "index.html";
private ServerSocket server;



public jhttp2(File basedir, int port, String indexfich) {
try {
baseDirectory= basedir;
if (!baseDirectory.isDirectory()) {
throw new IOException(baseDirectory + " no es un directorio");
}
indexFileName = indexfich;
server = new ServerSocket(port);


System.out.println(" jhttp2 server acepta conexiones en puerto " +
server.getLocalPort());



for (int i = 0; i < 10; i++) {
Thread t = new Thread(
new requesthttp2 (baseDirectory, indexFileName, i));
// el constructor no necesita el Socket como tercer parámetro
t.start();
}


while (true) {
try {
Socket request = server.accept();
requesthttp2.processRequest(request);
} catch (IOException ex) {}
}




} catch (IOException ex) {
System.out.println("jhttp2 Excepcion: " + ex);
} }



public static void main(String[] args) {
File baseDir;
String nomfich= "index.html";
int port;
try {
baseDir = new File(args[0]);
} catch (Exception ex) {
System.out.println("Uso: java jhttp2 directorio_base [port indexfile]");
return;
}
try {
port = Integer.parseInt(args[1]);
if (port < 0 || port > 65535) port = 80;
} catch (Exception ex) { port = 80;}
if (args.length==3) nomfich=args[2];
try {
jhttp2 webserver = new jhttp2(baseDir,port,nomfich);
} catch (Exception ex) {
System.out.println("jhttp2 server no arranca por: ");
System.out.println(ex);
} } }