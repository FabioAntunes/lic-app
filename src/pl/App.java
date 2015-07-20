package pl;

import bll.*;
import dal.Kit;
import exceptions.UserNotFoundException;

public class App {
    //constantes com os estados da nossa "maquina de estados"
	private static final int INIT = 0;
	private static final int READING_USER = 1;
	private static final int WAIT_TIMEOUT = 2;
	private static final int READING_PASSWORD = 3;
	private static final int CHECK_PASSWORD= 4;
	private static final int USER_LOGIN = 5;
	private static final int USER_LOUGOUT = 6;
	private static final int OPEN_DOOR = 7;
	private static final int CLOSE_DOOR = 8;
    //fim das constantes relativas aos estados
	private static final int USER_ID_LENGTH = 2; //constante que define o tamanho do userID
	private static final int PASSWORD_LENGTH = 4; //constante que define o tamanho da password
	private static final long TIMEOUT_MILLIS = 5000; //constante que define o timeout em milissegundo
    public static final int EXIT_MASK = 0x40; // Mascara para obter o 7 bit do Kit, responsavel por desligar a aplicacao
    private static int currentState = -1;
    private static String userID;
    private static String password;
	private static UserManager um;
    private static User currentUser;
    private static long lastMillis;


    /**
     * Metodo responsavel pela inicializacao da classe App
     */
	public static void init() {
		um = new UserManager();
		um.loadUsers();

        resetData();
	}

    /**
     * Metodo responsavel por ler o input do teclado e controlar as varias fases
     */
    public static void readInput(){
        char input;

        checkTimeout();
        if(currentState<USER_LOGIN){
            LCDManager.refreshClock();
        }
        //testar se o MIS esta ocupado, se estiver nao fazemos nada
        if(!LCD.isBusy()){
            switch (currentState){
                case INIT:
                    input = KBD.getKey();

                    if( input != KBD.NONE){

                        if(input == 'C'){
                            lastMillis = 0;
                            LCDManager.resetID();
                            userID = "";
                        }else if(input == 'F'){
                            currentState = -1;
                            resetData();
                        }else{
                            lastMillis = System.currentTimeMillis();
                            userID += input;
                            LCDManager.writeChar(input);
                        }

                    }

                    if(userID.length() == USER_ID_LENGTH) {
                        currentState = READING_USER;
                    }

                    break;
                case READING_USER:
                    try {
                        currentUser = um.findUser(userID);
                        LCDManager.writeOnSecondLine("Password:????");
                        LCDManager.cursor(1, 9);
                        currentState = READING_PASSWORD;
                        lastMillis = System.currentTimeMillis();
                    } catch (UserNotFoundException e) {
                        LCDManager.writeOnSecondLine("Util. invalido");
                        lastMillis = System.currentTimeMillis();
                        currentState = WAIT_TIMEOUT;
                    }
                    break;
                case WAIT_TIMEOUT:
                    checkTimeout();
                    break;
                case READING_PASSWORD:

                    input = KBD.getKey();
                    if( input != KBD.NONE){
                        lastMillis = System.currentTimeMillis();

                        if(input == 'C'){
                            password = "";
                            LCDManager.resetPassword();
                        }else{
                            password += input;
                            LCDManager.writeChar('*');
                        }
                    }

                    if(password.length() == PASSWORD_LENGTH) {
                        lastMillis = 0;
                        currentState = CHECK_PASSWORD;
                    }
                    break;
                case CHECK_PASSWORD:
                    if (currentUser.getPassword().equals(password)) {
                        if (currentUser.getMinutes() > 0) {
                            currentState = USER_LOUGOUT;
                        } else {
                            currentState = USER_LOGIN;
                        }
                    } else {
                        LCDManager.writeOnSecondLine("Password errada");
                        lastMillis = System.currentTimeMillis();
                        currentState = WAIT_TIMEOUT;
                    }
                    break;
                case USER_LOGIN:
                    currentUser.setMinutes(System.currentTimeMillis());
                    LCDManager.userLogin(currentUser.getMinutes(), currentUser.getSum());
                    um.saveUser(currentUser);
                    currentState = OPEN_DOOR;
                    break;
                case USER_LOUGOUT:
                    //calcular o tempo que trabalhou
                    currentUser.incrementSum(System.currentTimeMillis() - currentUser.getMinutes());
                    LCDManager.userLogout(currentUser.getMinutes(), currentUser.getSum());
                    currentUser.setMinutes(0);
                    um.saveUser(currentUser);
                    currentState = OPEN_DOOR;
                    break;
                case OPEN_DOOR:
                    Door.abrirPorta(Door.VELOCITY.medium.getValue());
                    currentState = CLOSE_DOOR;
                    break;
                case CLOSE_DOOR:
                    Door.fecharPorta(Door.VELOCITY.medium.getValue());
                    lastMillis = System.currentTimeMillis();
                    currentState = WAIT_TIMEOUT;
                    break;
            }
        }
    }





    /**
     * Verifica se foi accionado o mecanismo para desligar a aplicacao
     * @return um valor booleano verdadeiro no caso do mecanismo ter sido accionado
     */
    public static boolean getTurnOff() {
        int in_val = Kit.in();

        if((in_val&EXIT_MASK) == EXIT_MASK){

            um.saveUsers();
            return true;
        }

        return false;
    }

    /**
     * Faz reset aos dados responsaveis que representam os varios estados e volta ao estado inicial
     */
    private static void resetData(){
        userID = "";
        password = "";
        currentUser = null;
        lastMillis = 0;
        if(currentState == INIT) {
            LCDManager.resetID();
        }else{
            LCDManager.resetLCD();
        }
        currentState = INIT;
    }

    /**
     * Funcao que verifica se passou o tempo suficiente
     */
    private static void checkTimeout(){
        //verifica se lastMillis e maior que zero e se for verifica se ja passou o timeout, tambem verifica o MyBUSY
        if(lastMillis > 0 && System.currentTimeMillis() - lastMillis > TIMEOUT_MILLIS && !LCD.isBusy()){
            resetData();
        }
    }
}
