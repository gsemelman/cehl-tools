package org.cehl.raw.decode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.cehl.model.cehl.player.PlayerHandType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.DrsRaw;
import org.cehl.raw.ProspectRaw;
import org.cehl.raw.Teams;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class ProspectDecodeTools {
    
    public static final int FILE_LENGTH = 2700;
    public static int RECORD_LENGTH = 22;
    public static int TEAM_LENGTH= 122;
    public static int FILLER = FILE_LENGTH - (TEAM_LENGTH * RECORD_LENGTH);
    
    public static List<ProspectRaw> readWithCsvBeanReader(String fileName)  {
		
		List<ProspectRaw> prospectList = new ArrayList<ProspectRaw>();

		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(fileName),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			
			ProspectRaw prospect;
			while ((prospect = beanReader.read(ProspectRaw.class, header,
					processors)) != null) {
				//drs.setPlayerId(playerId);
				prospectList.add(prospect);
				//System.out.println(prospect.toString());
			}

		}catch (Exception e){
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
		
		//Collections.sort(drsList);
		return prospectList;
	}

	public static List<String> validateProspectRecords(List<ProspectRaw> imports){
		List<String> messages = new ArrayList<String>();
		String baseMessage = "Record for name [%s] is invalid. ";
		
		for(ProspectRaw prospect : imports){
			
			
			if(StringUtils.isEmpty(prospect.getName())){
				messages.add(String.format(baseMessage, prospect.getName()) + "Prospect name cannot be empty");
			}
			PlayerHandType handType = PlayerHandType.HandTypeByFlagValue(prospect.getHand());
			if(handType == null){
				messages.add(String.format(baseMessage, prospect.getName()) + "Hand type: " + prospect.getHand() + ". Must be L OR R");
			}
			PlayerPositionType positionType = PlayerPositionType.PositionByFlagValue(prospect.getPosition());
			if(positionType == null){
				messages.add(String.format(baseMessage, prospect.getName()) + "Position type: " + prospect.getPosition() + ". Must be LW/C/RW/D/G");
				continue;
			}
			
			
		}
		
		
		return messages;
		
	}

	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(new StrMinMax(0, 22, new Trim())), // Name
				new NotNull(new StrMinMax(0, 2, new Trim())), // position
				new NotNull(new StrMinMax(0, 1, new Trim())), // hand
				new NotNull(new ParseInt()), //height
				new NotNull(new ParseInt()), //weight
				new NotNull(new ParseInt()),//age
				new NotNull(new ParseInt()), //intensity
				new NotNull(new ParseInt()), //speed
				new NotNull(new ParseInt()), //strength
				new NotNull(new ParseInt()), //endurence
				new NotNull(new ParseInt()), //duribility
				new NotNull(new ParseInt()), //disciplie
				new NotNull(new ParseInt()), //skaing
				new NotNull(new ParseInt()), //pass accuracy
				new NotNull(new ParseInt()), //puck control
				new NotNull(new GoalieStatProcessor(new ParseInt())), //defense
				new NotNull(new GoalieStatProcessor(new ParseInt())), //scoring
				new NotNull(new ParseInt()), //experience
				new NotNull(new ParseInt()), //leadership
				new NotNull(new ParseInt()), //salary
				new NotNull(new ParseInt()), //contract
				new NotNull(new StrMinMax(0, 3, new Trim())), //birthplace
				new StrMinMax(0, 22, new Trim()) // teamName

				
		};

		return processors;
	}
	
	public static void main(String[] args) {
	    //ProspectDecodeTools.loadProspects(new File("d:/temp/cehl.pct"));
	    
	    List<ProspectImport> prospects =  ProspectDecodeTools.readWithCsvBeanReader2("d:/temp/prospect.csv");
	    ProspectDecodeTools.writeProspects(prospects, new File("d:/temp/cehl.pct"));
	    
	    ProspectDecodeTools.loadProspects(new File("d:/temp/cehl.pct"));
    }
	
	  public static List<ProspectImport> readWithCsvBeanReader2(String fileName)  {
	        
	        List<ProspectImport> prospectList = new ArrayList<ProspectImport>();

	        ICsvBeanReader beanReader = null;
	        try {
	            beanReader = new CsvBeanReader(new FileReader(fileName),
	                    CsvPreference.STANDARD_PREFERENCE);

	            // the header elements are used to map the values to the bean (names
	            // must match)
	            final String[] header = beanReader.getHeader(true);
	            final CellProcessor[] processors = getProcessors2();
	            
	            ProspectImport prospect;
	            while ((prospect = beanReader.read(ProspectImport.class, header,
	                    processors)) != null) {
	                //drs.setPlayerId(playerId);
	                prospectList.add(prospect);
	                //System.out.println(prospect.toString());
	            }

	        }catch (Exception e){
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
	        
	        //Collections.sort(drsList);
	        return prospectList;
	    }
	  
	  private static CellProcessor[] getProcessors2() {

	        final CellProcessor[] processors = new CellProcessor[] {
	                new NotNull(new StrMinMax(0, 50, new Trim())), // Team
	                new NotNull(new StrMinMax(0, 50, new Trim()))
     
	        };

	        return processors;
	    }
	
	  
   public static List<DrsRaw> loadProspects(File file) {

        List<byte[]> byteList = DecodeTools.decodeFile(file,RECORD_LENGTH);
        
        List< List<byte[]>> partitioned = DecodeTools.partition(byteList, TEAM_LENGTH);
        
        for(int x=0;x<= partitioned.size() -1;x++) {
          
            List<byte[]> prospectArray = partitioned.get(x);
            
            for (byte[] bytes : prospectArray) {
                String prospect = DecodeTools.readString2(bytes,0, 22);
                if(StringUtils.isNoneEmpty(prospect)) {
                    System.out.println("TeamId " + x + " " + DecodeTools.readString2(bytes,0, 22)); 
                }
      
            }
        }
        

       
        return null;
   }
	   
   public static class ProspectImport{
       String team;
       String name;
       
    public String getTeam() {
        return team;
    }
    public void setTeam(String team) {
        this.team = team;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
       
       
   }
	
   public static void writeProspects(List<ProspectImport> prospectList, File ouputFile){

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ouputFile.getAbsolutePath()))){

            TreeMap<Integer,ArrayList<String>> treeMap = new TreeMap<>();
            
            for(ProspectImport p : prospectList) {
                Teams team = Teams.fromName(p.getTeam());
                
                if(team == null) {
                    throw new RuntimeException("invalid team name" + p.getTeam());
                }
                
                if(treeMap.containsKey(team.getTeamId())) {
                    List<String> list = treeMap.get(team.getTeamId());
                    list.add(p.getName());
                }else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(p.getName());
                    treeMap.put(team.getTeamId(), list);
                }
            }
            
            
            byte[] blankData = new byte[22];
            for(int i = 0 ; i < blankData.length ; i++) {
                blankData[i] = 0;
            }
            
            //assumes teams are ordered correctly (by id)
            for(Teams team : Teams.values()) {
                ArrayList<String> prospects = treeMap.get(team.getTeamId());
                for(int x = 0; x < TEAM_LENGTH ; x++ ) {
                    byte[] bytes = new byte[RECORD_LENGTH];
                    
                    if(x >= prospects.size()) {
                        //DecodeTools.writeString(bytes, "test", 0, RECORD_LENGTH);
                        bos.write(bytes);
                    }else {
                        String prospect = prospects.get(x);
                        
                        String name = prospect.length() >= RECORD_LENGTH ? prospect.substring(0,RECORD_LENGTH) : prospect;
                        DecodeTools.writeString(bytes, name, 0, RECORD_LENGTH);
                        bos.write(bytes);
                    }
    
                    
                   
                }
                //each team is a block of 2700 bytes, only 122 22 byte blocks can be allocated
                //to that total, leaving 16 to complete the allocated bytes per team.
                blankData = new byte[FILLER];
                bos.write(blankData);
            }

//            for (ProspectImport prospect : prospectList) {
//                byte[] bytes = new byte[RECORD_LENGTH];
//                
//                String name = prospect.getName().length() > RECORD_LENGTH ? prospect.getName().substring(0,RECORD_LENGTH) : prospect.getName();
//                DecodeTools.writeString(bytes, name, 0, 22);
//              
//
//                bos.write(bytes);
//            }
            
            
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            System.out.println("Error Reading The File.");
            throw new RuntimeException(e);
        }
        catch (Exception e){
            System.out.println("Unknown file processing error for file : ");
            throw new RuntimeException(e);
        }

    }

}
