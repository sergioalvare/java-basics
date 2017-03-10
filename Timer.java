import java.net.*;
import java.io.*;
import java.util.*;

class Timer implements Runnable, cons {  //permite programar temporizaciones,  generara evento en el pipe, duran 2 seg(reenvio) y 4 el de finalizacion
	static final int plazo = 2, fin = 4; //genera 2 evento que entran en el pipe (tout o close)
	int[] timer = { 0, 0 }; // array con contadores de los dos timers
	PipedOutputStream o; // stream para envío de timeouts

	Timer(PipedOutputStream ot) {
		o = ot;
		Thread t = new Thread(this);
		t.start();
	} // Timer constructor

	synchronized void startTout() {
		timer[tout] = plazo;
	}

	synchronized void stopTout() {
		timer[tout] = 0;
	}

	synchronized void startTimers() { // arranca los dos timers
		timer[tout] = plazo;
		timer[close] = plazo * fin;
	}

	synchronized void stopTimers() { // para los dos timers
		timer[tout] = 0;
		timer[close] = 0;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(1000); // espera 1 segundo
				synchronized (this) {
					for (int i = 0; i < timer.length; i++) { // todos los timers
						if (timer[i] > 0)
							if (--timer[i] == 0) { // decrementa timer[i] y
													// compara
								o.write((byte) i);
								o.flush(); // envía timeout
							}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Timer: " + e);
		} catch (InterruptedException e) {
			System.out.println("Timer" + e);
		}
	}
}
