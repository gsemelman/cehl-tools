package org.cehl.cehltools.jobs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.cehl.cehltools.JobType;
import org.cehl.commons.SimFileType;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.DpkRaw;
import org.cehl.raw.decode.DecodeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DraftPickUpdaterJob extends AbstractJob {
	private static final Logger logger = LoggerFactory.getLogger(DraftPickUpdaterJob.class);
	
	private static int TEAM_LENGTH = 1536; 
	private static int REC_PER_TEAM = 256;
	private static int RECORD_LENGTH = 6;
	
	private boolean cleanDraftYear = true;
	
	private boolean addNewYears = true;
	private int maxYears = 6;
	
	private File inputFile;
	
	public DraftPickUpdaterJob() {
		super(JobType.DRAFT_PICK_FIX);
		super.setBackupLeagueFiles(false);
	}

	
	@Override
	public void _run() {
		List<DpkRaw> dpkList = processDraft(false);
		output(dpkList);
	}
		

	@Override
	public String getJobInfo() {
		return "Rollover to new draft year";
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}
		
	public List<DpkRaw> processDraft(boolean skipBlank) {
		
		File file = getLeagueFileByType(SimFileType.DRAFT_PICK_FILE);

		List<byte[]> byteList = DecodeTools.decodeFile(file,TEAM_LENGTH);
		List<DpkRaw> dpkList = new ArrayList<>();

		int teamId = 0;

        for (byte[] bytes : byteList) {
        	
        	List<byte[]> split = splitArray(bytes, RECORD_LENGTH);
        	
        	CehlTeam team = CehlTeam.fromId(teamId);
        	if(team != null) {
            	System.out.println("PROCESSING TEAM " + team.getName());
        	}else {
            	System.out.println("PROCESSING TEAM UNKNOWN");
        	}

        	
        	for(byte[] record : split) {

        		if(DecodeTools.readByte(record[5]) == 255 || !skipBlank) {

            		System.out.println("-----------------------------------------");
            		System.out.println("year " + DecodeTools.readByte(record[0]));
            		
            		int selection = DecodeTools.readByte(record[1]);
            		selection++;
            		
            		System.out.println("Selection " + selection);
            		System.out.println("team " + DecodeTools.readByte(record[2]));
            		System.out.println("dunno " + DecodeTools.readByte(record[3]));
            		System.out.println("group digit " + DecodeTools.readByte(record[4]));
            		System.out.println("group digit " + DecodeTools.readByte(record[5]));

            		System.out.println("-----------------------------------------");
            		
            		DpkRaw dpk = new DpkRaw();
            		dpk.setTeamId(teamId);
            		
            		dpk.setYearIndex(DecodeTools.readByte(record[0]));
            		dpk.setSelection(DecodeTools.readByte(record[1]));
            		dpk.setPickTeamId(DecodeTools.readByte(record[2]));
            		dpk.setFiller(DecodeTools.readByte(record[3]));
            		dpk.setGroup1(DecodeTools.readByte(record[4]));
            		dpk.setGroup2(DecodeTools.readByte(record[5]));
            		
            		dpkList.add(dpk);
     
        		}

        	}

        teamId++;
        	
        }

        return dpkList;
	}
	
	void output(List<DpkRaw> dpkList) {
		//need to retain order while grouping (using linkedhashmap)
        Map<Integer, List<DpkRaw>> teamMap = dpkList.stream()
        		.collect(Collectors.groupingBy(DpkRaw::getTeamId, LinkedHashMap::new, Collectors.toList()));
       // .collect(Collectors.toMap(DpkRaw::getTeamId, Arrays::asList));
        
        List<DpkRaw> newList = new ArrayList<>();
        
        int maxYear = dpkList.stream().mapToInt(d->d.getYearIndex()).max().orElse(0) + 1;
        
        for(Entry<Integer, List<DpkRaw>> entry : teamMap.entrySet()) {
        	if(cleanDraftYear) {
        		List<DpkRaw> picks = entry.getValue();
        		List<DpkRaw> newPicks = new ArrayList<>();
        		
        		
        		picks.forEach(dpk -> {
        			if(dpk.getYearIndex() != 0 && dpk.getGroup1() == 255) {
        				dpk.setYearIndex(dpk.getYearIndex() - 1);
        				newPicks.add(dpk);
        			}
        		});
        		
  
        		//check first empty grouping exists. if not add.
        		if(newPicks.size() == 0) {
        			//blank team. just skip.
        			//we can add back later if a new team is added.
        			logger.debug("error");
        			continue;
        		}else if(newPicks.get(0).getGroup1() != 0) {
        			newPicks.add(0,new DpkRaw());
        		}
        		
        		//add missing years
          		if(addNewYears) {

            		if(maxYear < maxYears) {

            			for(int x = maxYear ; x <= maxYears; x++) {
            				
            				for(int p = 0 ; p<9 ; p++) {
            					DpkRaw dpk = new DpkRaw();
                				dpk.setYearIndex(x-1);
                				dpk.setSelection(p);
                				dpk.setPickTeamId(entry.getKey());
                				dpk.setFiller(entry.getKey());
                				dpk.setGroup1(255);
                				dpk.setGroup2(255);
                				
                				newPicks.add(dpk);
            				}
            			
            			}
            		}
        		}
        		
        		//fill in blank entries
        		for(int x = newPicks.size(); x < REC_PER_TEAM ; x++) {
        			newPicks.add(new DpkRaw());
        		}
        		
        		newList.addAll(newPicks);
        	}
        }
        
        newList.toString();
        
        List<byte[]> newByteList = new ArrayList<>();
        
        for(DpkRaw dpk : newList) {
    		byte[] record2 = new byte[6];
    		record2[0] = (byte) dpk.getYearIndex();
    		record2[1] = (byte) dpk.getSelection();
    		record2[2] = (byte) dpk.getPickTeamId();
    		record2[3] = (byte) dpk.getFiller();
    		record2[4] = (byte) dpk.getGroup1();
    		record2[5] = (byte) dpk.getGroup2();
    		
    		newByteList.add(record2);
        }
        
        byte[] array = newByteList.stream()
        	    .collect(
        	        () -> new ByteArrayOutputStream(),
        	        (b, e) -> {
        	            try {
        	                b.write(e);
        	            } catch (IOException e1) {
        	                throw new RuntimeException(e1);
        	            }
        	        },
        	        (a, b) -> {}).toByteArray();

		File outputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.DRAFT_PICK_FILE));
		
		try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))){
			bos.write(array);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	private static List<byte[]> splitArray(byte[] originalArray, int chunkSize) {
		List<byte[]> listOfArrays = new ArrayList<>();
		int totalSize = originalArray.length;
		if(totalSize < chunkSize ){
		   chunkSize = totalSize;
		}
		int from = 0;
		int to = chunkSize;

		while(from < totalSize){
		    byte[] partArray = Arrays.copyOfRange(originalArray, from, to);
		    listOfArrays.add(partArray);

		    from+= chunkSize;
		    to = from + chunkSize;
		    if(to>totalSize){
		        to = totalSize;
		    }
		}
		return listOfArrays;
	}
	

}
