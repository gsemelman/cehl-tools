package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class StRatingProcessor extends AbstractRatingProcessor{

	static RangeTable rangeTable = initRange();

	static RangeTable initRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0, 60);
		rangeTable.insertValue(28, 70);
		rangeTable.insertValue(31, 90); 
		
		return rangeTable;

	}
	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
		double bmi = ((player.getWeight() * 703) / (player.getHeight() * player.getHeight()));
		
		double pim = statHolder.getPim();
		double height = player.getHeight();
		
		double pimBonus = 0;		
		if(pim >= 100) {
			pimBonus = 5;
		}else if(pim >= 80) {
			pimBonus = 4;
		}else if(pim >= 60) {
			pimBonus = 3;
		}else if(pim >= 40) {
			pimBonus = 2;
		}else if(pim >= 20) {
			pimBonus = 1;
		}
		
		double heightBonus = 0;
		
		if(height < 70) {
			heightBonus = -5;
		}else if(height < 72) {
			heightBonus = -3;
		}else if(height <= 73) {
			heightBonus = 2;
		}else if(height <= 75) {
			heightBonus = 3.5;
		}else if(height <= 78) {
			heightBonus = 4.5;
		}else if(height <= 79) {
			heightBonus = 6;
		}else if(height >= 80) {
			heightBonus = 10;
		}
		

		//double st = Double.valueOf(rangeTable.findInterpolatedValue(bmi + (player.getWeight() / 10)));
		double st = Double.valueOf(rangeTable.findInterpolatedValue(bmi));
		
		if(player.getPosition().contains("D")) {
			st++;
		}
		
		st = st + pimBonus;
		st = st + heightBonus;
		
		st = Math.min(st, 95);

		return RerateUtils.normalizeRating(st);
	}

}
