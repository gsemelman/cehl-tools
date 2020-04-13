package org.cehl.raw.decode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.cehl.raw.DrsRaw;
import org.cehl.raw.RosterRaw;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

public class DrsTools {
	
	private static int RECORD_LENGTH = 88;
	
	//private List<DrsRaw> drsList;
	//private int fhlId = 0;
	
	public static List<DrsRaw> loadDrs(File file) {

		List<byte[]> byteList = DecodeTools.decodeFile(file,RECORD_LENGTH);
		List<DrsRaw> drsList = new ArrayList<DrsRaw>();
        
        for (byte[] bytes : byteList) {
        	
        	DrsRaw drs = new DrsRaw();

        	drs.setProFarmStatus1(DecodeTools.readByte(bytes[0]));
        	drs.setProFarmStatus2(DecodeTools.readByte(bytes[1]));
        	drs.setName(DecodeTools.readString(Arrays.copyOfRange(bytes,2, 24)).trim());
        	drs.setPosition(DecodeTools.readByte(bytes[24]));
        	drs.setJersey(DecodeTools.readByte(bytes[25]));
        	drs.setVetRookieStatus1(DecodeTools.readByte(bytes[26]));
        	drs.setVetRookieStatus2(DecodeTools.readByte(bytes[27]));
        	drs.setHand(DecodeTools.readByte(bytes[28]));
        	drs.setHeight((DecodeTools.readByte(bytes[29])));
        	drs.setWeight((DecodeTools.readByte(bytes[30])));
        	drs.setAge(DecodeTools.readByte(bytes[31]));
        	drs.setInjStatus(DecodeTools.readByte(bytes[32]));
        	drs.setCondition(DecodeTools.readByte(bytes[33]));
        	drs.setIt(DecodeTools.readByte(bytes[34]));
        	drs.setSp(DecodeTools.readByte(bytes[35]));
        	drs.setSt(DecodeTools.readByte(bytes[36]));
        	drs.setEn(DecodeTools.readByte(bytes[37]));
        	drs.setDu(DecodeTools.readByte(bytes[38]));
        	drs.setDi(DecodeTools.readByte(bytes[39]));
        	drs.setSk(DecodeTools.readByte(bytes[40]));
        	drs.setPa(DecodeTools.readByte(bytes[41]));
        	drs.setPc(DecodeTools.readByte(bytes[42]));
        	drs.setDf(DecodeTools.readByte(bytes[43]));
        	drs.setSc(DecodeTools.readByte(bytes[44]));
        	drs.setEx(DecodeTools.readByte(bytes[45]));
        	drs.setLd(DecodeTools.readByte(bytes[46]));
        	drs.setFiller1(bytes[47]);
        	drs.setSalary(DecodeTools.readInt(Arrays.copyOfRange(bytes,48, 52)));
        	drs.setContractLength(DecodeTools.readByte(bytes[52]));
        	drs.setSuspStatus(DecodeTools.readByte(bytes[53]));
        	drs.setGamesPlayed(DecodeTools.readByte(bytes[54]));
        	drs.setGoals(DecodeTools.readByte(bytes[55]));
        	drs.setAssists(DecodeTools.readByte(bytes[56]));
        	drs.setPlusMinus(DecodeTools.readByte(bytes[57]));
        	drs.setPlusMinus2(DecodeTools.readByte(bytes[58]));
        	drs.setPims(DecodeTools.readByte(bytes[59]));
        	drs.setPims2(DecodeTools.readByte(bytes[60]));
        	drs.setShots(DecodeTools.readByte(bytes[61]));
        	drs.setShots2(DecodeTools.readByte(bytes[62]));
        	drs.setPpGoals(DecodeTools.readByte(bytes[63]));
        	drs.setShGoals(DecodeTools.readByte(bytes[64]));
        	drs.setGwGoals(DecodeTools.readByte(bytes[65]));
        	drs.setGtGoals(DecodeTools.readByte(bytes[66]));
        	drs.setGoalStreak(DecodeTools.readByte(bytes[67]));
        	drs.setPointStreak(DecodeTools.readByte(bytes[68]));
        	drs.setFiller3(DecodeTools.readByte(bytes[69]));
        	drs.setFiller4(DecodeTools.readByte(bytes[70]));
        	drs.setFiller5(DecodeTools.readByte(bytes[71]));
        	drs.setFiller6(DecodeTools.readByte(bytes[72]));
        	drs.setFarmGoals(DecodeTools.readByte(bytes[73]));
        	drs.setFarmAssists(DecodeTools.readByte(bytes[74]));
        	drs.setFiller7(DecodeTools.readByte(bytes[75]));
        	drs.setFiller8(DecodeTools.readByte(bytes[76]));
        	drs.setFiller9(DecodeTools.readByte(bytes[77]));
        	drs.setBirthPlace(DecodeTools.readString(Arrays.copyOfRange(bytes,78, 81)));
        	drs.setFiller10(bytes[81]);
        	drs.setFiller11(bytes[82]);
        	drs.setFarmGamesPlayed(DecodeTools.readByte(bytes[83]));
        	drs.setFarmPim(DecodeTools.readByte(bytes[84]));
        	drs.setFarmPim2(DecodeTools.readByte(bytes[85]));
        	drs.setHits(DecodeTools.readByte(bytes[86]));
        	drs.setHits2(DecodeTools.readByte(bytes[87]));
        	
        	if(drs.getName().length() > 0){
        		drsList.add(drs);
        	}else{
        		//System.out.println("Skipping blank player");
        	}

        }
        
        return drsList;
	}
	
	public static void writeDrs(List<DrsRaw> drsList){
		writeDrs(drsList, new File("output\\cehl.drs"));
	}
	
	public static void writeDrs(List<DrsRaw> drsList, File outputFile){

		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
			
			//List<byte[]> byteList = new ArrayList<byte[]>();
			for (DrsRaw drs : drsList) {
				byte[] bytes = new byte[RECORD_LENGTH];
				bytes[0] = (byte) drs.getProFarmStatus1();
				bytes[1] = (byte) drs.getProFarmStatus2();
				DecodeTools.writeString(bytes, drs.getName(), 2, 22);
				bytes[24] = (byte) drs.getPosition();
				bytes[25] = (byte) drs.getJersey();
				bytes[26] = (byte) drs.getVetRookieStatus1();
				bytes[27] = (byte) drs.getVetRookieStatus2();
				bytes[28] = (byte) drs.getHand();
				bytes[29] = (byte) drs.getHeight();
				bytes[30] = (byte) drs.getWeight();
				bytes[31] = (byte) drs.getAge();
				bytes[32] = (byte) drs.getInjStatus();
				bytes[33] = (byte) drs.getCondition();
				bytes[34] = (byte) drs.getIt();
				bytes[35] = (byte) drs.getSp();
				bytes[36] = (byte) drs.getSt();
				bytes[37] = (byte) drs.getEn();
				bytes[38] = (byte) drs.getDu();
				bytes[39] = (byte) drs.getDi();
				bytes[40] = (byte) drs.getSk();
				bytes[41] = (byte) drs.getPa();
				bytes[42] = (byte) drs.getPc();
				bytes[43] = (byte) drs.getDf();
				bytes[44] = (byte) drs.getSc();
				bytes[45] = (byte) drs.getEx();
				bytes[46] = (byte) drs.getLd();
				bytes[47] = (byte) drs.getFiller1();
				DecodeTools.writeInt(bytes,drs.getSalary(), 48, 52);
				bytes[52] = (byte) drs.getContractLength();
				bytes[53] = (byte) drs.getSuspStatus();
				bytes[54] = (byte) drs.getGamesPlayed();
				bytes[55] = (byte) drs.getGoals();
				bytes[56] = (byte) drs.getAssists();
				bytes[57] = (byte) drs.getPlusMinus();
				bytes[58] = (byte) drs.getPlusMinus2();
				bytes[59] = (byte) drs.getPims();
				bytes[60] = (byte) drs.getPims2();
				bytes[61] = (byte) drs.getShots();
				bytes[62] = (byte) drs.getShots2();
				bytes[63] = (byte) drs.getPpGoals();
				bytes[64] = (byte) drs.getShGoals();
				bytes[65] = (byte) drs.getGwGoals();
				bytes[66] = (byte) drs.getGtGoals();
				bytes[67] = (byte) drs.getGoalStreak();
				bytes[68] = (byte) drs.getPointStreak();
				bytes[69] = (byte) drs.getFiller3();
				bytes[70] = (byte) drs.getFiller4();
				bytes[71] = (byte) drs.getFiller5();
				bytes[72] = (byte) drs.getFiller6();
				bytes[73] = (byte) drs.getFarmGoals();
				bytes[74] = (byte) drs.getFarmAssists();
				bytes[75] = (byte) drs.getFiller7();
				bytes[76] = (byte) drs.getFiller8();
				bytes[77] = (byte) drs.getFiller9();
				DecodeTools.writeString(bytes, drs.getBirthPlace(), 78, 3);
				bytes[81] = (byte) drs.getFiller10();
				bytes[82] = (byte) drs.getFiller11();
				bytes[83] = (byte) drs.getFarmGamesPlayed();
				bytes[84] = (byte) drs.getFarmPim();
				bytes[85] = (byte) drs.getFarmPim2();
				bytes[86] = (byte) drs.getHits();
				bytes[87] = (byte) drs.getHits2();
				
				bos.write(bytes);
			}
			
			bos.close();
			
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
	
	public static void sortDrsListByName(List<DrsRaw> drsList){
		Collections.sort(drsList,
                new Comparator<DrsRaw>()
                {
                    public int compare(DrsRaw d1, DrsRaw d2)
                    {
                        return d1.getName().compareTo(d2.getName());
                    }        
                });
	}
	
	public static DrsRaw createInstance(){
		DrsRaw drs = new DrsRaw();
		
		drs.setVetRookieStatus1(255);
		drs.setVetRookieStatus2(255);
		
		return drs;
	}
	
	public static List<DrsRaw> readWithCsvBeanReader(String fileName)  {
		
		List<DrsRaw> drsList = new ArrayList<DrsRaw>();

		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader(fileName),
					CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			
			DrsRaw drs;
			while ((drs = beanReader.read(DrsRaw.class, header,
					processors)) != null) {
				//drs.setPlayerId(playerId);
				drsList.add(drs);
				//System.out.println(drs.toString());
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
		return drsList;
	}

	private static CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] {
				new ConvertNullTo(new Integer(0),new ParseInt()), // pro farm status 1
				new ConvertNullTo(new Integer(0),new ParseInt()), // pro farm status 2
				new NotNull(new StrMinMax(0, 22, new Trim())), // Name
				new NotNull(new ParseInt()), // position
				new NotNull(new ParseInt()), // jersey
				new ConvertNullTo(new Integer(0),new ParseInt()), // vet rookie status 1
				new ConvertNullTo(new Integer(0),new ParseInt()), // vet rookie status 2
				new NotNull(new ParseInt()), //hand
				new NotNull(new ParseInt()), //height
				new NotNull(new ParseInt()), //weight
				new NotNull(new ParseInt()),//age
				new NotNull(new ParseInt()), //in status
				new NotNull(new ParseInt()), //condition
				new NotNull(new ParseInt()), //intensity
				new NotNull(new ParseInt()), //speed
				new NotNull(new ParseInt()), //strength
				new NotNull(new ParseInt()), //endurence
				new NotNull(new ParseInt()), //duribility
				new NotNull(new ParseInt()), //disciplie
				new NotNull(new ParseInt()), //skaing
				new NotNull(new ParseInt()), //pass accuracy
				new NotNull(new ParseInt()), //puck control
				new NotNull(new ParseInt()), //defense
				new NotNull(new ParseInt()), //scoring
				new NotNull(new ParseInt()), //experience
				new NotNull(new ParseInt()), //leadership
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler1
				new NotNull(new ParseInt()), //salary
				new ConvertNullTo(new Integer(0),new ParseInt()), //contract
				new ConvertNullTo(new Integer(0),new ParseInt()), //suspension status
				new ConvertNullTo(new Integer(0),new ParseInt()), //gp
				new ConvertNullTo(new Integer(0),new ParseInt()), //goals
				new ConvertNullTo(new Integer(0),new ParseInt()), //assists
				new ConvertNullTo(new Integer(0),new ParseInt()), //+/-
				new ConvertNullTo(new Integer(0),new ParseInt()), //+/-2
				new ConvertNullTo(new Integer(0),new ParseInt()), //pims
				new ConvertNullTo(new Integer(0),new ParseInt()), //pims2
				new ConvertNullTo(new Integer(0),new ParseInt()), //shots
				new ConvertNullTo(new Integer(0),new ParseInt()), //shots2
				new ConvertNullTo(new Integer(0),new ParseInt()), //pp goals
				new ConvertNullTo(new Integer(0),new ParseInt()), //sh goals
				new ConvertNullTo(new Integer(0),new ParseInt()), //gwg
				new ConvertNullTo(new Integer(0),new ParseInt()), //gtg
				new ConvertNullTo(new Integer(0),new ParseInt()), //goalstreak
				new ConvertNullTo(new Integer(0),new ParseInt()), //pointstreak
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler3
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler4
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler5
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler6
				new ConvertNullTo(new Integer(0),new ParseInt()), //farm goals
				new ConvertNullTo(new Integer(0),new ParseInt()), //farm assists
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler7
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler8
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler9
				new NotNull(new StrMinMax(0, 3, new Trim())), //birthplace
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler10
				new ConvertNullTo(new Integer(0),new ParseInt()), //filler11
				new ConvertNullTo(new Integer(0),new ParseInt()), //farm gp
				new ConvertNullTo(new Integer(0),new ParseInt()), //farm pim
				new ConvertNullTo(new Integer(0),new ParseInt()), //farm pim2
				new ConvertNullTo(new Integer(0),new ParseInt()), //hits
				new ConvertNullTo(new Integer(0),new ParseInt()) //hits2
				
		};

		return processors;
	}

	
	public static List<DrsRaw> findPlayerByName(List<DrsRaw> drsList , String name){
		return drsList.stream()
				.filter(roster -> roster.getName().toUpperCase().equals(name.toUpperCase()))
				.collect(Collectors.toList());
		
	}
	
}
