package org.cehl.cehltools.rerate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.util.Precision;
import org.cehl.cehltools.CehlUtils;
import org.cehl.cehltools.rerate.dto.PlayerRerateDto;
import org.cehl.cehltools.rerate.rating.RatingResult;
import org.cehl.commons.SimFileType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.decode.DrsTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class DrsRerateJob implements IRerateJob {
	
	private static final Logger logger = LoggerFactory.getLogger(DrsRerateJob.class);

	@Autowired
	RerateService rerateService;
	
	@Autowired
	@Qualifier("simLocation")
	private File simLocation;
	
	@Autowired
	@Qualifier("baseInputLocation")
	private File baseInputLocation;
	
	@Autowired
	@Qualifier("baseOutputLocation")
	private File baseOutputLocation;
	
	@Autowired
	@Qualifier("leaguePrefix")
	private String leaguePrefix;
	

	public void runJob(int endYear) {
	
		List<DrsRaw> drsList = DrsTools.loadDrs(
				CehlUtils.getLeagueFileByType(simLocation, leaguePrefix, SimFileType.DRS_FILE)
				);

		
		drsList.forEach(drs -> {
			PlayerRerateDto rerateDto = new PlayerRerateDto(drs.getName(),drs.getAge(),drs.getBirthPlace());
			PlayerPositionType position = PlayerPositionType.PositionByRawValue(drs.getPosition());
			rerateDto.setPos(position.stringValue().charAt(0));
			
			RatingResult result = rerateService.reratePlayer(rerateDto, endYear);
			
			if(result != null) {
				drs.setIt((int) result.getIt());
				drs.setEn((int) result.getEn());
				drs.setDu((int) result.getDu());
				drs.setDi((int) result.getDi());
				drs.setPa((int) result.getPa());
				drs.setPc((int) result.getPc());
				drs.setSc((int) result.getSc());
			}
		});

		
		output(drsList);
	}
	
	public void output(List<DrsRaw> drsList) {
		File drsOutputFile = new File(baseOutputLocation,CehlUtils.getFileNameStringByType(leaguePrefix,SimFileType.DRS_FILE));
		logger.info("Writing drs output file: " + drsOutputFile.getAbsolutePath());
		DrsTools.writeDrs(drsList,drsOutputFile);

	}
	
}
