package bll;

public class User {
	private String id;
	private String name;
	private String password;
	private long sum;
	private long minutes;


	public User(String id, String name, String password, long sum, long minutes){
		this.id = id;
		this.name = name;
		this.password= password;
		this.sum = sum;
		this.minutes = minutes;
	}
	
	/**
	 * Obter id do utilizador
	 * @return Id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Define o id do utilizador
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return Obter o nome do utilizador
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param Define o nome do utilizador
	 */
	public void setName(String nome) {
		this.name = nome;
	}
	
	/**
	 * Obter password do utilizador
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Define a password do utilizador
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Obter horas acumuladas
	 */
	public long getSum() {
		return sum;
	}

	/**
	 * @param Define as horas acumuladas pelo utilizador
	 */
	public void setSum(long sum) {
		this.sum = sum;
	}

	/**
	 * Incrementa o somatorio
	 * @param sum valor a incrementar ao total
	 */
	public void incrementSum(long sum) {
		this.sum += sum;
	}

	/**
	 * @return Obter os minutos do utilizador
	 */
	public long getMinutes() {
		return minutes;
	}

	/**
	 * @param Define os minutos do utilizador
	 */
	public void setMinutes(long minutes) {
		this.minutes = minutes;
	}
	
}
