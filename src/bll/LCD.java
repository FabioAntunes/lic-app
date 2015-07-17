package bll;
import dal.Kit;


public class LCD {
	public static final int LINES = 2;
	public static final int COLS = 16;
	private static final boolean COMMAND = false;
	private static final boolean WRITE = true;
	private static final int DISPLAYCLEAR = 0X01;;

	/**
	 * Envia o bit �rs� e os 8 bits de �data�.
	 * O bit �rs� indica se � comando ou escrita.
	 * @param rs
	 * @param data
	 */
	 private static void sendByte(boolean rs, int data){
		 Emitter.send(true, data<<1 | (rs ? 1 : 0), 9);
	 }
	 
	 /**
	  * Envia a sequ�ncia de inicia��o do LCD.
	  */
	 public static void init(){
		 int initCommand = 0X30;
		 int fontLineCommand = 0X38;
		 int displayOff = 0X08;
		 int entryModeSet = 0X06;
		 int displayOn = 0X0F;
		 sendByte(COMMAND, initCommand);
		 
		 //wait more than 4.1ms
		 Kit.sleep(5);
		 sendByte(COMMAND, initCommand);
		 Kit.sleep(1);
		 sendByte(COMMAND, initCommand);
		 
		 sendByte(COMMAND, fontLineCommand);
		 
		 sendByte(COMMAND, displayOff);
		 sendByte(COMMAND, DISPLAYCLEAR);
		 sendByte(COMMAND, entryModeSet);
		 sendByte(COMMAND, displayOn);
	 }
	 
	 /**
	  * Escreve um car�cter na posi��o corrente.
	  * @param c caracter que vai ser escrito
	  */
	 public static void write(char c){
		 sendByte(WRITE, (int)c);
	 }
	 
	 /**
	  * Escreve uma string na posi��o corrente.
	  * @param txt texto que vai ser escrito
	  */
	 public static void write(String txt){
		 System.out.println(txt);
		 for (int i = 0; i < txt.length(); i++){
		    write(txt.charAt(i));
		}
	 }
	 
	 /**
	  * Envia comando para posicionar cursor (�lin�:0..LINES-1 , �col�:0..COLS-1)
	  * @param lin
	  * @param col
	  */
	 public static void cursor(int lin, int col){
		 int DB7 = 0X80;
		 int LINHA = 0X40;
		 
		 sendByte(COMMAND, DB7 | (lin * LINHA + col));
	 }

	/**
	 * Este método permite limpar o LCD
	 */
	public static void clearDisplay(){
		sendByte(COMMAND, DISPLAYCLEAR);
	}

	/**
	 * Este metodo permite verificar se o MIS esta ocupado
	 * @return
	 */
	public static boolean isBusy(){
		return Emitter.busy();
	}
}
