package pl;

import bll.*;
import dal.Kit;
import exceptions.UserNotFoundException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    private static int currentState;
    private static String firstLine;
    private static String secondLine;
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
        //testar se o MIS esta ocupado, se estiver nao fazemos nada
        if(!LCD.isBusy()){
            switch (currentState){
                case INIT:
                    input = KBD.getKey();
                    firstLine = getCurrentDate();
                    secondLine = "Utilizador:";
                    String questionMarks = "??";

                    if( input != KBD.NONE){
                        lastMillis = System.currentTimeMillis();
                        userID += input;
                    }

                    secondLine += userID + questionMarks.substring(userID.length());

                    sendToLCD();
                    if(userID.length() == USER_ID_LENGTH) {
                        lastMillis = 0;
                        currentState = READING_USER;
                    }

                    break;
                case READING_USER:
                    try {
                        currentUser = um.findUser(userID);

                        firstLine = getCurrentDate();
                        secondLine = "Password:????";
                        sendToLCD();

                        currentState = READING_PASSWORD;
                    } catch (UserNotFoundException e) {
                        firstLine = getCurrentDate();
                        secondLine = "Util. invalido";
                        sendToLCD();
                        lastMillis = System.currentTimeMillis();
                        currentState = WAIT_TIMEOUT;
                    }
                    break;
                case WAIT_TIMEOUT:
                    checkTimeout();
                    break;
                case READING_PASSWORD:
                    firstLine = getCurrentDate();
                    secondLine = "Password:";
                    String fourQuestionMarks = "????";
                    String asterisks = "****";

                    input = KBD.getKey();
                    if( input != KBD.NONE){
                        lastMillis = System.currentTimeMillis();
                        password += input;
                    }
                    secondLine += asterisks.substring(asterisks.length() - password.length()) + fourQuestionMarks.substring(password.length());

                    sendToLCD();

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
                        firstLine = getCurrentDate();
                        secondLine = "Password errada";
                        sendToLCD();
                        lastMillis = System.currentTimeMillis();
                        currentState = WAIT_TIMEOUT;
                    }
                    break;
                case USER_LOGIN:
                    currentUser.setMinutes(System.currentTimeMillis());
                    firstLine = getCurrentDay() + getHours(currentUser.getMinutes()) + "-??:??";
                    secondLine = "Semanal=" + formatSumHours(currentUser.getSum());
                    sendToLCD();
                    um.saveUser(currentUser);
                    currentState = OPEN_DOOR;
                    break;
                case USER_LOUGOUT:
                    //calcular o tempo que trabalhou
                    currentUser.incrementSum(System.currentTimeMillis() - currentUser.getMinutes());
                    firstLine = getCurrentDay() + getHours(currentUser.getMinutes()) + "-" + getHours(System.currentTimeMillis());
                    secondLine = "Semanal=" + formatSumHours(currentUser.getSum());
                    sendToLCD();
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
     * Obtem a data actual formatada
     * @return devolve uma string com o seguinte formato dd/MM/yyyy HH:mm
     */
    public static String getCurrentDate(){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
    }

    /**
     * Obter o dia atual abreviado em portugues, por ex: "Seg"
     * @return
     */
    public static String getCurrentDay(){
        return new SimpleDateFormat("EEE ",  new Locale("pt", "PT")).format(Calendar.getInstance().getTime());
    }

    /**
     * Obtem a hora actual
     * @return
     */
    public static String getHours(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return new SimpleDateFormat("HH:mm",  new Locale("pt", "PT")).format(calendar.getTime());
    }

    /**
     * Passando os milisegundos em formato unix epoch, converte para s horas e minutos correspondentes
     * @param millis
     * @return horas e minutos no formato HH:mm
     */
    public static String formatSumHours(long millis){
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1));
    }

    /**
     * Metodo que envia a informacao actual para o LCD
     */
    public static void sendToLCD(){
        LCD.clearDisplay();
        LCD.cursor(0, 0);
        LCD.write(firstLine);
        LCD.cursor(1, 0);
        LCD.write(secondLine);
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
        currentState = INIT;
        firstLine = getCurrentDate();
        secondLine= "Utilizador:??";
        currentUser = null;
        lastMillis = 0;

        sendToLCD();
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
