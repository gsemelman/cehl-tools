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

//		forwardRangeTable.insertValue(0.00, 60);
//		forwardRangeTable.insertValue(0.1219, 70); //10
//		forwardRangeTable.insertValue(0.26, 74); //21.32
//		forwardRangeTable.insertValue(0.48, 81); //39.3
//		forwardRangeTable.insertValue(0.68, 85); //55.7
//		forwardRangeTable.insertValue(0.8760, 96); //71.8
		
		forwardRangeTable.insertValue(0.00, 60);
		forwardRangeTable.insertValue(0.1219, 69); //10
		forwardRangeTable.insertValue(0.26, 73); //21.32
		forwardRangeTable.insertValue(0.48, 80); //39.3
		//forwardRangeTable.insertValue(0.68, 84); //55.7
		forwardRangeTable.insertValue(0.68, 83); //55.7
		//forwardRangeTable.insertValue(0.8760, 96); //71.8
		forwardRangeTable.insertValue(0.8760, 91); //71.8
		
		return forwardRangeTable;

	}
	
	static RangeTable initDefenseRanges() {

		defenseRangeTable = new RangeTable();

		defenseRangeTable.insertValue(0.0001, 60);
		defenseRangeTable.insertValue(0.05, 68.5);
		//defenseRangeTable.insertValue(0.2, 74);
		defenseRangeTable.insertValue(0.2, 73);
		//defenseRangeTable.insertValue(0.42, 80);
		defenseRangeTable.insertValue(0.435, 80);
		//defenseRangeTable.insertValue(0.50, 84);
		defenseRangeTable.insertValue(0.50, 83);
		//defenseRangeTable.insertValue(0.8760, 94); 
		defenseRangeTable.insertValue(0.8760, 90);
		
		return defenseRangeTable;
	}
	

	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
	
		double gp = (double)statHolder.getGp();
		double apg = (double)statHolder.getAssists() / gp;

		double pa = 0;
		
		if(player.getPosition().contains("D")) {
			pa = Double.valueOf(defenseRangeTable.findInterpolatedValueSmooth(apg)); //* 1.045;
		}else {
			pa = Double.valueOf(forwardRangeTable.findInterpolatedValueSmooth(apg));
		}
		
		pa = Math.min(pa, 97);

		return RerateUtils.normalizeRating(pa);
	}

	public void postProcess(RatingResult ratingResult) {
		double pc = ratingResult.getPc();
		double pa = ratingResult.getPa();
		
//		if(pa <= 76) {
//			pc = Math.min(pc, pa + 2);
//		}else if(pa <= 77) {
//			pc = Math.min(pc, pa + 3);
//		}else if(pa < 79) {
//			pc = Math.min(pc, pa + 4);
//		}else if(pa < 85) {
//			pc = Math.min(pc, pa + 5);
//		}
		
		if(pa <= 74) {
			pc = Math.min(pc, pa + 2);
		}else if(pa <= 78) {
			pc = Math.min(pc, pa + 3);
		}else if(pa < 81) {
			pc = Math.min(pc, pa + 4);
		}else {
			pc = Math.min(pc, pa + 5);
		}

		ratingResult.setPc(pc);

	}
	
}
