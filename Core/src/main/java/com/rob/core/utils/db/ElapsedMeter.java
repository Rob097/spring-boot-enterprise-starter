package com.rob.core.utils.db;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;


/**
 * @author Roberto97
 * Class used to trace how long a query takes to be executed
 */
public class ElapsedMeter {
	long startMills;
	long partialMills;
	
	public ElapsedMeter()
	{
		this.restart();
	}
	
	/** Restart milliseconds count
	 * 
	 */
	public void restart()
	{
		startMills = System.currentTimeMillis();
		partialMills = System.currentTimeMillis();
	}
	
	/** Calculate milliseconds from last restart
	 * @param withRestart 
	 * 
	 * @withRestart if true --> restarted
	 * @return duration in milliseconds from last restart
	 */
	public long partialDuration(boolean withRestart)
	{
		long mills = System.currentTimeMillis()-partialMills;
		if (withRestart == true) this.restart();
		partialMills = System.currentTimeMillis();
		return mills;
	}
	
	/** Calculate total milliseconds
	 * 
	 * @return duration in milliseconds from last restart
	 */
	public long totalDuration()
	{
		return System.currentTimeMillis()-startMills;
	}
	
	public String getFormattedTotalDuration()
	{
		return StringUtils.leftPad(String.valueOf(this.totalDuration()), 10, ' ');
	}
	public String getFormattedPartialDuration()
	{
		return StringUtils.leftPad(String.valueOf(this.partialDuration(false)), 10, ' ');
	}
	public String getFormattedPartialDurationAndRestart()
	{
		return StringUtils.leftPad(String.valueOf(this.partialDuration(true)), 10, ' ');
	}
	/**
	 * si fa passare l'handler e stampa nel logger i dati del parziale <br>
	 * secondo la codifica <br>
	 * 
	 * %n%30s%n %10d ms per un totale di %2d min %2d sec%n"
	 * 
	 * @param message
	 * @param restartChrono
	 * @param logger
	 */
	public void logInformation(String message, boolean restartChrono, Logger logger){
		long p1 = this.partialDuration(restartChrono);
		logger.debug(String.format("%n%30s%n %10d ms per un totale di %2d min %2d sec%n", 
				message ,p1, TimeUnit.MILLISECONDS.toMinutes(p1),TimeUnit.MILLISECONDS.toSeconds(p1) -  TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(p1))));
	}
}
