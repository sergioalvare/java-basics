import java.io.*; import java.net.*;
public class echoClient 
{
	public static void main (String args[]) 
	{
		String host = "localhost", theLine;
		if (args.length > 0) {host=args[0];}
	
	try 
	{
	Socket s = new Socket(host, 7);
	LineNumberReader netIn = new LineNumberReader(new InputStreamReader(s.getInputStream()));
	PrintWriter netOut = new PrintWriter(s.getOutputStream(), true);
	LineNumberReader sysIn = new LineNumberReader(new InputStreamReader(System.in));
	
	while (true) 
	{
		System.out.println("Escriba algo: ");
		theLine = sysIn.readLine();
		if (theLine.equals(".")) break;
		netOut.println(theLine);
		
		System.out.println("Eco recibido: ");
		System.out.println(netIn.readLine());
	}
	} catch (UnknownHostException e) { System.out.println(e);
	} catch (IOException e) {}
	}
}