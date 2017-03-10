import java.io.*;
import java.util.*;
import java.lang.*;



public class DirSearchVersion4 {

	static int numeroDeHebrasLanzadas=0;
	static int numeroDeHebrasFinalizadas=0;

	//constructor:
	public DirSearchVersion4(){}

	public static void main (String [] args) throws Exception
	{
	DirSearchVersion4 indexador=new DirSearchVersion4();
	
	if(args[0]!=null)
	{
		if(args[1]!=null)
		{
		indexador.indexa(args);
		}
	}
	
	}
	
	void indexa(String [] args) throws IOException
	{
	

	File directorioAExplorar=new File(args[0]);
	String palabraABuscar=args[1];//Hay que pasarla entre comillas: ejemplo: "palabra"
		
	//1. CREACION DEL FICHERO VACIO PARA RECOGER
	//LOS RESULTADOS DE LA BUSQUEDA
	String nombreFicheroResultados="resultados.txt";
	
	//BufferedWriter bw=new BufferedWriter(new FileWriter(nombreFicheroResultados));
	
	File ficheroResultados=new File(nombreFicheroResultados);
	ficheroResultados.createNewFile();
		
	//Lista donde se van a guardar los datos que generen las hebras:
	LinkedList<String> listaGlobal=new LinkedList<String>();
		
	//2. ARRANQUE DE UNA HEBRA POR CADA FICHERO DE TEXTO
	File [] listado=directorioAExplorar.listFiles();
	
	
	//Ahora voy a crear y arrancar cada una de las hebras
	for(int i=0;i<listado.length;i++)
		{
		
			 //HAY QUE COMPROBAR QUE EL FICHERO SEA txt ANTES DE LANZAR UNA HEBRA PARA ESE FICHERO
			 
			if(estxt(listado[i].getName())==true)
			{
			System.out.println("Se lanzo una hebra");
			HebraBuscadoraPalabras Hebra=new HebraBuscadoraPalabras(palabraABuscar,listado[i],listaGlobal,this,ficheroResultados);
			Hebra.start(); 
			numeroDeHebrasLanzadas++;
			}
		}
	
	String nuevalinea = System.getProperty("line.separator"); 
	boolean finDelBucle=false;
	while(finDelBucle==false)
	{
	
	if(numeroDeHebrasFinalizadas==numeroDeHebrasLanzadas)
	{
	//System.out.println(numeroDeHebrasFinalizadas);
	//System.out.println(numeroDeHebrasLanzadas);
	System.out.println("Volcando... ");

	/*
	while(!listaGlobal.isEmpty())
	{
		String cadenaAEscribir=listaGlobal.remove(0);
		bw.write(cadenaAEscribir+nuevalinea);
		
	}
	bw.close();*/
	System.out.println("               ...finalizado");
	imprimirFicherotxtPorPantalla(nombreFicheroResultados);
	finDelBucle=true;
	}

	}
	
	
	
	}
	
		
	

	public static int contarCuantosFicherostxt(File directorioAExplorar)
	{
		int contador=0;
		File [] listado=directorioAExplorar.listFiles();
		for(int i=0;i<listado.length;i++)
		{
			String nombreFichero=listado[i].getName();
			//ahora obtenemos la extension mirando si acaba en .txt:
			
			if(estxt(nombreFichero)==true) {contador++;}
		}
		return contador;
	}
	
	//para mirar si un fichero es de extension .txt:
	public static boolean estxt(String nombreFichero)
	{
	boolean elFicheroTieneExtensiontxt=false;
	String laExtension=nombreFichero.substring(nombreFichero.lastIndexOf('.'));
	//System.out.println(laExtension);
	if(laExtension.equals(".txt"))
		{
			elFicheroTieneExtensiontxt=true;
		}
		
		//System.out.println("elFicheroTieneExtensiontxt = "+elFicheroTieneExtensiontxt);
	return elFicheroTieneExtensiontxt;

	}	
	
	
	public static void imprimirFicherotxtPorPantalla(String nomFich) throws IOException
	{
		//System.out.println("IMPORTANTE: CONTENIDO DEL FICHERO DE RESULTADOS:");
		
		
		System.out.println("");
		System.out.println("");
		System.out.println("Contenido del fichero de resultados: ");
		System.out.println("");
		System.out.println("");
		
		FileReader fich=new FileReader(nomFich);//con esto abrimos el fichero
		
		//ahora vamos a leerlo linea a linea:
		BufferedReader fichBR=new BufferedReader(fich);
		
		//voy leyendo y volcando linea a linea:
		String linea=fichBR.readLine();
		while(linea!=null)
		{
			System.out.println(linea);
			linea=fichBR.readLine();
		}
		
		fichBR.close();
		}
	
/*
	public static void volcarDeListaAFichero(LinkedList<String> lista, String nombreFicheroResultados) throws IOException
{
	
	BufferedWriter bw=new BufferedWriter(new FileWriter(nombreFicheroResultados));
	
	
	//Vease pag 45 del pdf de la practica 1 para la siguiente instruccion:
	//FileOutputStream ficheroResultados=new FileOutputStream(nombreFicheroResultados);
	//Para lo siguiente, vease la pagina 12 de scbprac02.pdf
	while(!lista.isEmpty())
	{
		String cadenaAEscribir=lista.remove(0);
		bw.write(cadenaAEscribir);
		
	}
	bw.close();
	
	System.out.println("IMPORTANTE: VOLCADO DE LISTA A FICHERO: FINALIZADO");
} */

void finalizoUnaHebra()
{//La finalidad de este metodo es incrementar en 1 un contador del objeto
//DirSearch instanciado; dicho contador sirve para saber cuando han terminado
//todas las hebras lanzadas
numeroDeHebrasFinalizadas++;
}



	
}

