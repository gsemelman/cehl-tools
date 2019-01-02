package org.cehl.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.DrsTools;
import org.cehl.raw.decode.RosterTools;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

public class DrsPlayerDelete {
	public static void main(String[] args) throws Exception {
		DrsPlayerDelete deleter = new DrsPlayerDelete();
		
		deleter.run();
	}
	
	void run() throws IOException{
		RosterTools rosterTools = new RosterTools();
		DrsTools drsTools = new DrsTools();
		List<DrsRaw> newDrsList = new ArrayList<DrsRaw>();
		
		List<RosterRaw> rosterList = rosterTools.loadRoster(new File("cehl.ros"), false);
		List<DrsRaw> drsList = drsTools.loadDrs(new File("cehl.drs"));
		
		List<String> deleteList = new ArrayList<String>();
		
        ICsvListReader listReader = null;
        try {
                listReader = new CsvListReader(new FileReader("drsDelete.csv"), CsvPreference.STANDARD_PREFERENCE);
                
                listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
                final CellProcessor[] processors = getProcessors();
                
                List<Object> player;
                while( (player = listReader.read(processors)) != null ) {
                        System.out.println(String.format("lineNo=%s, rowNo=%s, playerList=%s", listReader.getLineNumber(),
                                listReader.getRowNumber(), player));
                        
                        deleteList.add((String) player.get(0));
                }
                
        }
        finally {
                if( listReader != null ) {
                        listReader.close();
                }
        }
        
        for(DrsRaw drsRaw : drsList){
        	
        	
        	if(deleteList.contains(drsRaw.getName()) && !rosterListContains(rosterList, drsRaw.getName())){
        		continue;
        	}
        	
        	newDrsList.add(drsRaw);
        }
        
        drsTools.writeDrs(newDrsList);
        
	}

	boolean rosterListContains(List<RosterRaw> rosterList, String name){
		for(RosterRaw raw : rosterList){
			if(raw.getName().equals(name)){
				return true;
			}
		}
		
		return false;
	}
	
	public CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(new StrMinMax(0, 22, new Trim())) // Name
		};

		return processors;
	}
	
}
