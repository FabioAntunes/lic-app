package pl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bll.Door;
import bll.Emitter;
import bll.KBD;
import bll.LCD;
import bll.UserManager;
import dal.Kit;


public class Main {

	public static void main(String[] args) {
		boolean turnOff = false;
        init();

		
		do{
			App.readInput();
            turnOff = App.getTurnOff();
		}while(!turnOff);
	}
	
	private static void init(){
		Emitter.init();
		KBD.init();
		Door.init();
		LCD.init();
        App.init();
	}

}
