package org.cehl.cehltools.rerate.processor;

import java.util.HashMap;
import java.util.Map;

import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.cehltools.rerate.dto.PlayerStatHolder;
import org.cehl.cehltools.rerate.model.Player;
import org.cehl.cehltools.rerate.rating.interp.RangeTable;

public class StRatingProcessor extends AbstractRatingProcessor{

	static RangeTable bmiRangeTable = initBmiRange();
	static RangeTable heightRangeTable = initHeightRange();
	static RangeTable pimRangeTable = pimRange();
	static RangeTable pimFighterRange = pimRange();

	static RangeTable initBmiRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(21, 60);
		rangeTable.insertValue(25, 71);
		rangeTable.insertValue(26, 72);
		rangeTable.insertValue(27, 75);
		rangeTable.insertValue(28, 80);
		rangeTable.insertValue(29.5, 85);
		rangeTable.insertValue(30, 99);
		//rangeTable.insertValue(30, 73);
		//rangeTable.insertValue(32, 80); 

		
		return rangeTable;

	}
	
	static RangeTable initHeightRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(64, 60);
		rangeTable.insertValue(66, 66);
		rangeTable.insertValue(71, 70);
		rangeTable.insertValue(76, 78);
		rangeTable.insertValue(77, 79);
		rangeTable.insertValue(78, 79);
		rangeTable.insertValue(79, 80);
		rangeTable.insertValue(80, 85);
		rangeTable.insertValue(81, 99);

		
		return rangeTable;

	}
	
	static RangeTable pimRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0.1, 60);
		rangeTable.insertValue(4, 99);
	
		return rangeTable;

	}
	
	static RangeTable pimFighterRange() {
		RangeTable rangeTable = new RangeTable();
		rangeTable.insertValue(0.1, 60);
		rangeTable.insertValue(4, 99);
	
		return rangeTable;

	}
	
	public StRatingProcessor() {
		super();
	}
	
	@Override
	public int getSeasonRating(Player player, PlayerStatHolder statHolder) {
		double bmi = ((player.getWeight() * 703) / (player.getHeight() * player.getHeight()));
		
		double pim = statHolder.getPim();
		double pimPerGame = pim / ((double)statHolder.getGp());
		double height = player.getHeight();
		double heightByWeight = Math.round(((double)player.getHeight()/(double)player.getWeight()) * 100);
		
		double heightValue = Double.valueOf(heightRangeTable.findInterpolatedValue(height));
		double bmiValue = Double.valueOf(bmiRangeTable.findInterpolatedValue(bmi));
		double pimValue = Double.valueOf(pimRangeTable.findInterpolatedValue(pimPerGame));
		
		Map<Double, Integer> map = new HashMap<>();
		map.put(heightValue, 100);
		map.put(bmiValue, 100);
		//map.put(pimValue, 60);
		
		if(player.getPosition().contains("D")) {
			//map.put(heightValue + 10, 50);
		}
		
		Double st = RerateUtils.calculateWeightedAverage(map);

		//double st = Double.valueOf(rangeTable.findInterpolatedValue(bmi + (player.getWeight() / 10)));
		
		
//		if(player.getPosition().contains("D")) {
//			st= st+6;
//		}else {
//			st = st+ 4;
//		}
		
		//st = st + pimBonus;
		//st = st + heightBonus;
		
		//st = Math.min(st, 95);

		return RerateUtils.normalizeRating(st);
	}

}
