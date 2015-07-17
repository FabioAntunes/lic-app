package bll;
import dal.Kit;


public class KBD {
	public static final int KEY_MASK = 0x0F; // Máscara para obter os 4 primeiros bits, do Kit
	public static final int OBF_MASK = 0x80;  // Máscara para obter o 8 bit, do Kit
	public static final int K_ACK_MASK = 0x01; // Máscara para emitir o 1 bit, do Kit
	public static final char NONE = 0; // Valor retornado quando não há tecla.
	private static char[] keys;
	
	
	
	/**
	 * Retorna de imediato a tecla premida ou NONE se não há tecla premida.
	 * @return Tecla lida de forma nao bloqueante
	 */
	public static char getKey(){
		int in_val = 0;
		in_val = Kit.in();
		if ((in_val& OBF_MASK) == OBF_MASK){
			
			//aplicar mascara e obter a tecla
			System.out.println("Valor da tecla " + (in_val&KEY_MASK));
			
			//Emite K_ACK
			Kit.out(K_ACK_MASK);
			
			while(true){
				if(( Kit.in()& OBF_MASK) != OBF_MASK){
					Kit.clear();
					break;
				}
				Kit.sleep(100);
			}
			System.out.println("Caracter pressionado: " + KBD.keys[in_val&KEY_MASK]);
			
			return KBD.keys[in_val&KEY_MASK];
		}
		
		return NONE;
		
	}
	
	/**
	 * Retorna quando a tecla for premida ou NONE após decorrido ‘timeout’ milisegundos.
	 * @param timeout Em milisegundos
	 * @return Devolve o caracter pressionado
	 */
	public static char waitKey(long timeout){
		long startTime = Kit.getTimeInMillis();
		long currentTime = Kit.getTimeInMillis();
		char returnedChar;
		while (currentTime - startTime <= timeout) {
			returnedChar = getKey();
			if(returnedChar != NONE){
				
				return returnedChar;
				
			}
			//esperar um decimo do timeout
			Kit.sleep(timeout/10);
			currentTime = Kit.getTimeInMillis();
		}
		
		return NONE;
		
	} 
	
	/**
	 * Estabelece os valores iniciais no porto de saída. Chamado no início do programa.
	 */
	public static void init(){
		Kit.clear();
		initChars();
	}
	
	/**
	 * Método privado que inicializa o mapeamento dos caracteres com os valores inteiros recebidos pelo Kit
	 */
	private static void initChars(){
//		KBD.keys = new char[]{'0', '4', '8', 'C', '1', '5', '9', 'D','2', '6', 'A', 'E', '3', '7', 'B', 'F'};
		KBD.keys = new char[]{'0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	}

}