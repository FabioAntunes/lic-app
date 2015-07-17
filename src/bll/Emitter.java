package bll;
import dal.Kit;


public class Emitter {
	public static final int MIBy_MASK = 0x10; // Máscara para obter o 5 bit, do Kit
	public static final int MIxD_MASK = 0x02; // Máscara para emitir o 2 bit, do Kit
	public static final int MIck_MASK = 0x04; // Máscara para emitir o 3 bit, do Kit
	private static int in_val;
	
	
	/**
	 * Envia tramas para o módulo Serial Receiver.
	 * Envia uma trama com o bit ‘lnd’ e os bits de ‘data’ (do menor peso ao maior peso)
	 * O parâmetro ‘size’ indica o número de bits de informação (3 ou 9).
	 * @param lnp
	 * @param data
	 * @param size
	 */
	public static void send(boolean lnp, int data, int size){
		sendGeneric(data<<1 |(lnp ? 1 : 0), size+1);
	}
	
	/**
	 * Indica se o MIS está ocupado a processar uma trama.
	 * @return verdadeiro quando ocupado
	 */
	public static boolean busy(){
		in_val = Kit.in();

		if((in_val&MIBy_MASK) == MIBy_MASK){
			return true;
		}

		return false;
	}
	
	/**
	 * Estabelece os valores iniciais no porto de saída.
	 */
	public static void init(){
		Kit.setBit(MIxD_MASK);
		Kit.clrBit(MIck_MASK);
	}
	
	
	private static void sendBit(boolean bit){
		if(bit){
			Kit.setBit(MIxD_MASK);
		}else{
			Kit.clrBit(MIxD_MASK);
		}

		Kit.clrBit(MIck_MASK);
		Kit.sleep(2);
		Kit.setBit(MIck_MASK);
	}
	
	private static void sendGeneric(int data, int size){
		initTransmission();
		
		int mask =  0x01;
		
		for (int i = 0; i < size; i++) {
			sendBit((mask&data) != 0);
			mask<<=1;
		}
		
		Kit.setBit(MIxD_MASK);
	}
	
	private static void initTransmission(){
		Kit.clrBit(MIck_MASK);
		Kit.setBit(MIxD_MASK);
		Kit.sleep(5);
		Kit.clrBit(MIxD_MASK);
		Kit.sleep(5);
	}
	
}
