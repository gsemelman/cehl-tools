package org.cehl.cehltools.rerate.processor;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.model.PlayerSeason;
import org.cehl.cehltools.rerate.rating.RatingResult;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class PaRatingProcessor extends AbstractRatingProcessor{

	static RangeTable forwardRangeTable = initForwardRanges();
	static RangeTable defenseRangeTable = initDefenseRanges();
	

	static RangeTable initForwardRanges() {
		RangeTable forwardRangeTable = new RangeTable();
		forwardRangeTable.insertValue(0.00, 60);
		forwardRangeTable.insertValue(0.1219, 72);
		//forwardRangeTable.insertValue(0.2857, 75);
		forwardRangeTable.insertValue(0.5, 81);
		//forwardRangeTable.insertValue(0.8760, 97); 
		forwardRangeTable.insertValue(0.8760, 96);
		
		return forwardRangeTable;

	}
	
	static RangeTable initDefenseRanges() {

		defenseRangeTable = new RangeTable();
//		defenseRangeTable.insertValue(0.05, 60);
//		defenseRangeTable.insertValue(0.1, 68.5);
//		defenseRangeTable.insertValue(0.2, 75);
//		defenseRangeTable.insertValue(0.4, 79);
//		defenseRangeTable.insertValue(0.45, 80);
//		defenseRangeTable.insertValue(0.8760, 97); //played all games
		
		defenseRangeTable.insertValue(0.05, 60);
		defenseRangeTable.insertValue(0.1, 68.5);
		defenseRangeTable.insertValue(0.2, 75);
		defenseRangeTable.insertValue(0.42, 80);
		//defenseRangeTable.insertValue(0.45, 81);
		defenseRangeTable.insertValue(0.8760, 97); //played all games
		
		return defenseRangeTable;
	}
	

	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = (double)statHolder.getGp();
		double apg = (double)statHolder.getAssists() / gp;

		double pa = 0;
		
		if(player.getPosition().contains("D")) {
			pa = Double.valueOf(defenseRangeTable.findInterpolatedValue(apg)); //* 1.045;
		}else {
			pa = Double.valueOf(forwardRangeTable.findInterpolatedValue(apg));
		}
		
		pa = Math.min(pa, 97);

		return RerateUtils.normalizeRating(pa);
	}

	public void postProcess(RatingResult ratingResult) {
		double pc = ratingResult.getPc();
		double pa = ratingResult.getPa();
		
		if(pa <= 76) {
			pc = Math.min(pc, pa + 2);
		}else if(pa <= 77) {
			pc = Math.min(pc, pa + 3);
		}else if(pa < 79) {
			pc = Math.min(pc, pa + 4);
		}else if(pa < 85) {
			pc = Math.min(pc, pa + 5);
		}
		
		
		ratingResult.setPc(pc);

	}
	
}
