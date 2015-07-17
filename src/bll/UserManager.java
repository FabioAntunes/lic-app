package bll;

import java.util.LinkedList;

import dal.UserDAO;
import exceptions.FullListException;
import exceptions.UserNotFoundException;

public class UserManager {
	private static final int NUM_USERS = 100;
	private LinkedList<User> users = new LinkedList<User>();
	private UserDAO userDAO = new UserDAO();
	
	/**
	 * Devolve um novo objecto User e adiciona o mesmo a lista de Users
	 *  
	 * @param id
	 * @param nome
	 * @param password
	 * @return
	 * @throws FullListException 
	 */
	public User createUser(String id, String nome, String password, int acumulado, int minutes) throws FullListException{
		if(users.size() == NUM_USERS){
			throw new FullListException("Users list is full");
		}else{
			
			User user = new User(id, nome, password, acumulado, minutes);
			users.add(user);
			return user;
		}
	}
	
	/**
	 * Procura um utilizador pelo id
	 * 
	 * @param id of the User
	 * @return User 
	 * @throws UserNotFoundException 
	 */
	public User findUser(String id) throws UserNotFoundException{

		for (User user : users) {
			if (user.getId().equals(id)){
				return user;
			}
		}
		throw new UserNotFoundException("User with id: "+ id +" not found");

	}
	
	/**
	 * Carrega os utilzadores existentes na nossa Data Access Layer
	 */
	public void loadUsers(){
		users = userDAO.getUsers();
	}
	
	/**
	 * Guarda os utilzadores existentes na nossa Data Access Layer
	 */
	public void saveUsers(){
		userDAO.saveUsers(users);
	}

	/**
	 * Guarda utilizador na lista
	 * @param currentUser
	 */
	public void saveUser(User currentUser) {
		for (User user : users) {
			if (user.getId().equals(currentUser.getId())){
				user.setSum(currentUser.getSum());
				user.setMinutes(currentUser.getMinutes());
				break;
			}
		}
	}
}
