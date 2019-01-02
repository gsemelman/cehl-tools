package org.cehl.raw.transformer;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class TransformUtils {
	
	public static int getPenaltyMinutes(int pimStat1, int pimStat2){
		return (pimStat2 * 256) + pimStat1;
	}
	
	public static int getHits(int hitStat1, int hitStat2){
		return (hitStat2 * 256) + hitStat1;
	}
	
	public static int getShots(int shotsStat1, int shotsStat2){
		return (shotsStat2 * 256) + shotsStat1;
	}
	
	public static int getPlusMinus(int plusMinus1, int plusMinus2){
		if(plusMinus2 == 0){
			return plusMinus1;
		}else{
			return (256-plusMinus1) * -1;
		}
		
	}
	
	public static BigDecimal getShootingPct(int goals, int shotsAttempted){
		
		if(shotsAttempted == 0 || shotsAttempted == 0 ){
			return new BigDecimal(0).setScale(1);
		}
		
		BigDecimal decGoals = new BigDecimal((double)goals);
		BigDecimal decShotsAttempted = new BigDecimal((double)shotsAttempted);
		BigDecimal multiplyer = new BigDecimal((double)100);

		BigDecimal savePct = decGoals.divide(decShotsAttempted, new MathContext(100))
				.multiply(multiplyer).setScale(1,RoundingMode.HALF_UP);
		
		return savePct;
	}
	
	public static int getGoalieShotsAgainst(int shotsStat1, int shotsStat2){
		return (shotsStat2 * 256) + shotsStat1;
	}
	
	public static int getGoalieMinutes(int minutesStat1, int minutesStat2){
		return (minutesStat2 * 256) + minutesStat1;
	}
	
	public static BigDecimal getGoalieGaa(int goalsAllowed, int minutesPlayed){
		
		if(goalsAllowed == 0 || minutesPlayed == 0 ){
			return new BigDecimal(0).setScale(2);
		}
		
		BigDecimal decGoalsAllowed = new BigDecimal((double)goalsAllowed);
		BigDecimal decMinutesPlayed = new BigDecimal((double)minutesPlayed);
		BigDecimal multiplyer = new BigDecimal((double)60);
		
		BigDecimal gaa = decGoalsAllowed.multiply(multiplyer)
				.divide(decMinutesPlayed, new MathContext(100))
				.setScale(2,RoundingMode.HALF_UP);

		return gaa;
	}
	
	public static BigDecimal getGoalieSavePct(int goalsAllowed, int shotsAttempted){
		
		if(goalsAllowed == 0 || shotsAttempted == 0 ){
			return new BigDecimal(1).setScale(3);
		}
		
		BigDecimal decGoalsAllowed = new BigDecimal((double)goalsAllowed);
		BigDecimal decShotsAttempted = new BigDecimal((double)shotsAttempted);
	
		BigDecimal savePct = decShotsAttempted.subtract(decGoalsAllowed).divide(
				decShotsAttempted, new MathContext(100)).setScale(3, RoundingMode.HALF_UP);
		
		return savePct;
	}
	
}
