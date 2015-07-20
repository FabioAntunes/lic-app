package pl;


import bll.LCD;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LCDManager {
    private static int lin = 0;
    private static int col = 0;
    private static String clock;

    /**
     * Escreve um caracter na posicao corrente.
     * @param c caracter que vai ser escrito
     */
    public static void writeChar(char c){
        LCD.write(c);
        col++;
    }

    /**
     * Envia comando para posicionar cursor
     * @param lin
     * @param col
     */
    public static void cursor(int lin, int col){
        LCDManager.lin = lin;
        LCDManager.col = col;
        LCD.cursor(lin, col);
    }

    /**
     * Metodo que limpa o ecra envia as duas linhas para o LCD
     */
    public static void clearAndSendToLCD(String firstLine, String secondLine){
        LCD.clearDisplay();
        LCD.cursor(0, 0);
        LCD.write(firstLine);
        LCD.cursor(1, 0);
        LCD.write(secondLine);
        LCDManager.lin = 1;
        LCDManager.col = secondLine.length();
    }

    /**
     * Metodo que escreve na segunda linha do LCD
     * @param secondLine
     */
    public static void writeOnSecondLine(String secondLine){
        LCD.cursor(1, 0);
        LCD.write(secondLine);
        LCDManager.lin = 1;
        LCDManager.col = secondLine.length();
    }

    /**
     * Faz reset ao ID do utilizador
     */
    public static void resetID(){
        lin = 1;
        col = 11;
        LCDManager.cursor(lin, col);
        LCD.write("??");
        LCDManager.cursor(lin, col);
    }

    /**
     * Faz reset a password
     */
    public static void resetPassword(){
        lin = 1;
        col = 9;
        LCDManager.cursor(lin, col);
        LCD.write("????");
        LCDManager.cursor(lin, col);
    }

    /**
     * Faz reset ao LCD e volta ao estado inicial
     */
    public static void resetLCD(){
        clearAndSendToLCD(getCurrentDate(), "Utilizador:??");
        lin = 1;
        col = 11;
        LCDManager.cursor(lin, col);

    }

    /**
     * Escreve a mensagem de entrada de um utilizador
     * @param currentUserMinutes
     * @param currentUserSum
     */
    public static void userLogin(long currentUserMinutes, long currentUserSum) {
        clearAndSendToLCD(getCurrentDay() + getHoursFromMillis(currentUserMinutes) + "-??:??",
                "Semanal=" + formatSumHours(currentUserSum));

    }

    /**
     * Escreve a mensagem de saida de um utilizador
     * @param currentUserMinutes
     * @param currentUserSum
     */
    public static void userLogout(long currentUserMinutes, long currentUserSum) {
        clearAndSendToLCD(getCurrentDay() + getHoursFromMillis(currentUserMinutes) + "-" + getHoursFromMillis(System.currentTimeMillis()),
                "Semanal=" + formatSumHours(currentUserSum));
    }

    /**
     * Funcao que verifica se ja passou um minuto e actualiza o relogio.
     * No fim volta a colocar o cursor onde estava anteriormente
     */
    public static void refreshClock() {
        String oldCLock = clock;

        if(!oldCLock.equals(getCurrentHours())){
            LCD.cursor(0, 11);
            LCD.write(clock);
            LCD.cursor(lin, col);
        }
    }

    /**
     * Obtem a data actual formatada
     * @return devolve uma string com o seguinte formato dd/MM/yyyy HH:mm
     */
    private static String getCurrentDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()) + " " + getCurrentHours();
    }

    /**
     * Obtem a hora actual formatada
     * @return devolve uma string com o seguinte formato HH:mm
     */
    private static String getCurrentHours(){
        clock = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        return  clock;
    }

    /**
     * Obter o dia atual abreviado em portugues, por ex: "Seg"
     * @return
     */
    private static String getCurrentDay(){
        return new SimpleDateFormat("EEE ",  new Locale("pt", "PT")).format(Calendar.getInstance().getTime());
    }

    /**
     * Obtem a hora actual
     * @return
     */
    private static String getHoursFromMillis(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return new SimpleDateFormat("HH:mm",  new Locale("pt", "PT")).format(calendar.getTime());
    }

    /**
     * Passando os milisegundos, converte para as horas e minutos correspondentes
     * @param millis
     * @return horas e minutos no formato HH:mm
     */
    private static String formatSumHours(long millis){
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1));
    }
}
