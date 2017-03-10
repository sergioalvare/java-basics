
interface cons {
static final int tout=0, close=1, frame=2; // eventos
static final int espera=0, recibiendo=1, acabando=2; // estados
static final int RRQ=1, WRQ=2, DATA=3, ACK=4,ERROR=5;
// c�digos de trama
static final int ServerPort=1069;
} //cons