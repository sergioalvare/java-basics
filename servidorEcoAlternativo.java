import java.net.*; import java.util.Date;
public class servidorEcoAlternativo 
{
	public static void main (String args[]) throws Exception 
	{
		byte d[] = new byte[1024];
		DatagramPacket dp = new DatagramPacket(d, d.length);
		DatagramSocket ds = new DatagramSocket(4444);
		
		while (true) 
		{
			try {
			ds.receive(dp);
			d = dp.getData();
			//d = ((new Date()).toString()).getBytes();
			ds.send(new DatagramPacket(d, d.length,
			dp.getAddress(), dp.getPort()));
			} catch (Exception e) {}
		}
	}
}