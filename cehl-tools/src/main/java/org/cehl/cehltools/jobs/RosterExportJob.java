package org.cehl.cehltools.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cehl.cehltools.JobType;
import org.cehl.cehltools.rerate.RerateUtils;
import org.cehl.commons.SimFileType;
import org.cehl.model.cehl.player.PlayerPositionType;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.CehlTeam;
import org.cehl.raw.decode.RosterTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RosterExportJob extends AbstractJob{
	
	private static final Logger logger = LoggerFactory.getLogger(RosterExportJob.class);
	
	public RosterExportJob(){
		super(JobType.EXPORT_ROSTER);
	}
	
	@Override
	public String getJobInfo() {
		return ""
				+ "Export roster for rerates";
	}
	
	@Override
	public List<String> additionalPreValidation(){
		List<String> validationMessages = new ArrayList<String>();

		return validationMessages;
	}

	@Override
	public void _run() {	
		
		List<RosterRaw> rosterList = RosterTools.loadRoster(super.getLeagueFileByType(SimFileType.ROSTER_FILE), false);

		List<String[]> rows = new ArrayList<>();
		rows.add(new String[] {"Team","Name","Number","Nat","Position","IT","SP","ST","EN","DU","DI","SK","PA","PC","DF","SC","EX","LD","OV"});
		
		
		for(RosterRaw rosterRaw : rosterList){
			
			PlayerPositionType position = PlayerPositionType.PositionByRawValue(rosterRaw.getPosition());
			CehlTeam team = CehlTeam.fromId(rosterRaw.getTeamId());
			
			if(StringUtils.isBlank(rosterRaw.getName())) continue;
			
			double ov = RerateUtils.calculateOv(position.stringValue(), 
					rosterRaw.getIt(), rosterRaw.getSp(), rosterRaw.getSt(),
					rosterRaw.getEn(), rosterRaw.getDu(), rosterRaw.getDi(),
					rosterRaw.getSk(), rosterRaw.getPa(), rosterRaw.getPc(), 
					rosterRaw.getDf(), rosterRaw.getSc(), rosterRaw.getEx(), rosterRaw.getLd());
			
			for(int x=1; x <=2 ;x++) {
				String[] values = new String[] {
						team.getName(),
						rosterRaw.getName(), 
						String.valueOf(rosterRaw.getJersey()), 
						rosterRaw.getBirthPlace(),
						position.stringValue(),
						String.valueOf(rosterRaw.getIt()), 
						String.valueOf(rosterRaw.getSp()), 
						String.valueOf(rosterRaw.getSt()), 
						String.valueOf(rosterRaw.getEn()), 
						String.valueOf(rosterRaw.getDu()), 
						String.valueOf(rosterRaw.getDi()), 
						String.valueOf(rosterRaw.getSk()), 
						String.valueOf(rosterRaw.getPa()), 
						String.valueOf(rosterRaw.getPc()), 
						String.valueOf(rosterRaw.getDf()), 
						String.valueOf(rosterRaw.getSc()), 
						String.valueOf(rosterRaw.getEx()), 
						String.valueOf(rosterRaw.getLd()), 
						String.valueOf(ov),
						String.valueOf(x)
						};
				
				
				rows.add(values);
			}
			

			rows.add(new String[] {});
			
		}
		
		File csvOutputFile = new File("c:/test/roster.csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			rows.stream()
	          .map(s-> RerateUtils.convertToCSV(s))
	          .forEach(pw::println);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}



}
