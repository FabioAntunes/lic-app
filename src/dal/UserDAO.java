package dal;

import java.io.IOException;
import java.util.LinkedList;

import bll.User;

public class UserDAO {
	private FileUtils fileReader = new FileUtils();
	private static final String FILE =  "src/users.txt";
	private static final String SEPARATOR =  ";";
	
	public LinkedList<User> getUsers() {
		LinkedList<User> users = new LinkedList<User>();
		try {
			LinkedList<String> usersFile = fileReader.readFile(FILE);
			
			for (String userLine : usersFile){
				//obter as várias colunas
	        	String[] user = userLine.split(SEPARATOR);
				users.add(new User(user[0], user[1], user[2], Long.parseLong(user[3]), Long.parseLong(user[4])));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return users;
	}
	
	public void saveUsers(LinkedList<User> users) {
		String[] serializedUsers = new String[users.size()];
		
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			//cria uma string com os dados do utilizador
			serializedUsers[i] = user.getId() + SEPARATOR + 
					user.getName() + SEPARATOR + user.getPassword() + SEPARATOR +
					user.getSum() + SEPARATOR + user.getMinutes();
		}
		
		try {
			fileReader.writeToFile(FILE, serializedUsers);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
