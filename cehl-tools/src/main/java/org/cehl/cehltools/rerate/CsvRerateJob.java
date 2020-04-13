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
import org.cehl.cehltools.dto.RerateDto;
import org.cehl.cehltools.rerate.dto.PlayerRerateDto;
import org.cehl.cehltools.rerate.rating.RatingResult;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.decode.GoalieStatProcessor;
import org.cehl.raw.decode.RatingProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class CsvRerateJob implements IRerateJob {
	
	private String fileLocation;
	
	@Autowired
	RerateService rerateService;
	
	public CsvRerateJob(String fileLocation) {
		super();
		this.fileLocation = fileLocation;
	}

	public void runJob(int endYear) {
		List<PlayerRerateDto> players = importReratesFromCsv(fileLocation);
		
		Map<PlayerRerateDto, RatingResult> results = new LinkedHashMap<>();
		
		for(PlayerRerateDto player : players){
			RatingResult result = rerateService.reratePlayer(player, endYear);
			
			results.put(player,result);
		}
		
		output(results);
	}
	
	public void output(Map<PlayerRerateDto, RatingResult> results) {
		
		List<String[]> rows = new ArrayList<>();
		rows.add(new String[] {"Name","Age","IT","SP","ST","EN","DU","DI","SK","PA","PC","DF","SC","EX","LD","OV"});

		
		for(Entry<PlayerRerateDto, RatingResult> entry: results.entrySet()) {
			PlayerRerateDto player = entry.getKey();
			RatingResult rerateResult = entry.getValue();
			
			if(rerateResult == null) {
				String[] values = new String[] {
						player.getName()
				};
				
				rows.add(values);
				continue;
			}

			
			String[] values = new String[] {
					player.getName(), 
					String.valueOf(player.getAge()),
					String.valueOf(Precision.round(rerateResult.getIt(),0) ), 
					String.valueOf(Precision.round(rerateResult.getSp(),0) ), 
					String.valueOf(Precision.round(rerateResult.getSt(),0) ), 
					String.valueOf(Precision.round(rerateResult.getEn(),0) ), 
					String.valueOf(Precision.round(rerateResult.getDu(),0) ), 
					String.valueOf(Precision.round(rerateResult.getDi(),0) ), 
					String.valueOf(Precision.round(rerateResult.getSk(),0) ), 
					String.valueOf(Precision.round(rerateResult.getPa(),0) ), 
					String.valueOf(Precision.round(rerateResult.getPc(),0) ), 
					String.valueOf(Precision.round(rerateResult.getDf(),0) ),
					String.valueOf(Precision.round(rerateResult.getSc(),0) ),
					String.valueOf(Precision.round(rerateResult.getEx(),0) ),
					String.valueOf(Precision.round(rerateResult.getLd(),0) ),
					String.valueOf(Precision.round(rerateResult.getOv(),0) ),
					};
			
			rows.add(values);
		}
		

		
		File csvOutputFile = new File("output/csv_ratings.csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			rows.stream()
	          .map(s-> RerateUtils.convertToCSV(s))
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public static List<PlayerRerateDto> importReratesFromCsv(String fileName)  {
		
		List<PlayerRerateDto> rosterList = new ArrayList<PlayerRerateDto>();

		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(fileName),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			
			PlayerRerateDto rerate;
			while ((rerate = beanReader.read(PlayerRerateDto.class, header,
					processors)) != null) {
				rosterList.add(rerate);
				System.out.println(rerate.toString());
			}

		}catch(SuperCsvConstraintViolationException e){
			throw e;
		}
		catch (Exception e){
			throw new RuntimeException(e);
		}
		finally {
			if (beanReader != null) {
				try {
					beanReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return rosterList;
	}


	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new StrNotNullOrEmpty(), // Name
				new Optional(new ParseInt()), // age
				new Optional() // nat
				
				

				
		};

		return processors;
	}
}
