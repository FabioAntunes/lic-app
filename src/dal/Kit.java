package dal;
import isel.leic.usbio.UsbPort;
import isel.leic.utils.Time;

public class Kit {
	
	// guarda a imagem do valor que está no porto de saída do USBPort
	private static int outImage = 0;
	
	/**
	 * Obtém valores de entrada do USBPort
	 * @return Valor inteiro, negado, correspondente aos 8 bits de entrada
	 */
	public static int in() {
		return ~UsbPort.in();
	}
	
	/**
	 * Devolve o valor inteiro, negado, para o USBPort
	 * @param outValue Valor decimal correspondente aos 8 bits que se deseja emitir
	 */
	public static void out(int outValue) {
		UsbPort.out(~outValue);
	}
	
	/**
	 * Limpar a saída do USBPort
	 */
	public static void clear() {
		UsbPort.out(0xFF);
	}
	
	/**
	 * Colocar a execução do programa em standby durante o tempo indicado em milisegundos
	 * @param milis Tempo em milisegundos
	 */
	public static void sleep(long milis) {
		Time.sleep(milis);
	}
	
	/**
	 * Obter o tempo atual em milisegundos
	 * @return Tempo em milisegundos
	 */
	public static long getTimeInMillis() {
		return Time.getTimeInMillis();
	}
	
	/**
	 * Fazer set de apenas um bit mantendo o resto
	 * @param mask
	 */
	public static void setBit(int mask) {
		outImage = outImage|mask;
		out(outImage);
	}
	
	/**
	 * Colocar os bits a zero correspondentes a mascara
	 * @param mask
	 */
	public static void clrBit(int mask) {
		outImage = outImage & ~mask;
		out(outImage);
	}
	
	/**
	 * Para obter o valor da imagem guardada do porto de saída
	 * @return o valor guardado anteriormente no porto de saída
	 */
	public static int getOutImage() {
		return 0;
	}
	
	/**
	 * Definir o valor do porto de saída para guardar a imagem
	 * @param outImage valor a guardar
	 */
	public void setOutImage(int outImage) {
		Kit.outImage = outImage;
	}
	
}
