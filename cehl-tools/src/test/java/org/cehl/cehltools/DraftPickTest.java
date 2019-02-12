package org.cehl.cehltools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cehl.cehltools.dto.DraftPickDto;
import org.cehl.cehltools.dto.ProspectDto;
import org.cehl.raw.Teams;
import org.cehl.raw.decode.DecodeTools;

public class DraftPickTest {
	
	
	//.pct
	//Each prospect name = group of 16 bytes
	//Each team = 2700 bytes
	private static int RECORD_LENGTH = 6;
	private static int TEAM_LENGTH = 1536; //record length is 2700 but team can only fit 100 prospects
	
	public static void main(String[] args) throws Exception {
		DraftPickTest test = new DraftPickTest();
		
		test.readTest();
	}
	
	void readTest() {
		List<byte[]> byteList = DecodeTools.decodeFile(new File("C:/Temp/cehl.dpk"),RECORD_LENGTH);
		
		List<DraftPickDto> pickList = new ArrayList<>();
		
	   for (byte[] bytes : byteList) {
		   DraftPickDto dto = new DraftPickDto();
		   dto.setYear(DecodeTools.readByte(bytes[0]));
		   dto.setSelection(DecodeTools.readByte(bytes[1]));
		   dto.setTeamId(DecodeTools.readByte(bytes[2]));
		   dto.setNewTeamId(DecodeTools.readShort(Arrays.copyOfRange(bytes,3, 6)));
		   
		   pickList.add(dto);
		   
		   //DecodeTools.readByte(bytes[2]);
		  // DecodeTools.readByte(bytes[3]);
		   //DecodeTools.readByte(bytes[4]);
		   //DecodeTools.readByte(bytes[5]);
		   
	   }
		
		System.out.println("end of test");
	}
	

}
