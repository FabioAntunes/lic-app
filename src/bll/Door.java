package bll;

public class Door {
	private static final int BYTES_SIZE = 3;
	public enum VELOCITY {
		slow(1), medium(2), fast(3);
		private int value ;

		VELOCITY ( int value )
		{
			this.value = value ;
		}
		public int getValue(){
			return this.value;
		};
	};
	
	/**
	 *  Dá ordem de abertura da porta com respectiva velocidade.
	 * @param veloc
	 */
	public static void abrirPorta(int veloc){
		Emitter.send(false, (veloc<<1)|1, BYTES_SIZE);
	}
	
	/**
	 * Dá ordem de fecho com respectiva velocidade.
	 * @param veloc
	 */
	public static void fecharPorta(int veloc){
		Emitter.send(false, (veloc << 1), BYTES_SIZE);
	}
	
	/**
	 * Indica se foi concluída a operação de abertura ou fecho.
	 * @return
	 */
	public static boolean limite(){
		 return Emitter.busy();
	}

	/**
	 * Estabelece os valores iniciais. 
	 */
	public static void init(){
		return;
	}

}
