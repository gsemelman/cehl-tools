package org.cehl.cehltools.jobs;

import java.io.File;
import java.util.List;

import org.cehl.cehltools.JobType;
import org.cehl.commons.SimFileType;
import org.cehl.raw.RosterRaw;
import org.cehl.raw.decode.RosterTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HoldoutJob extends AbstractJob{
	
	private static final Logger logger = LoggerFactory.getLogger(HoldoutJob.class);
	
	private File inputFile;

	public HoldoutJob() {
		super(JobType.HOLDOUT);
	
	}
	
	@Override
	public String getJobInfo() {
		return "Set holdout status";
	}

	@Override
	public void _run() {

		List<RosterRaw> rosterList = RosterTools.loadRoster(super.getLeagueFileByType(SimFileType.ROSTER_FILE), false);

		for(RosterRaw rosterRaw : rosterList){
			
			if(rosterRaw.getContractLength() == 0) {
				rosterRaw.setSuspStatus(99);
			}
			
		}

		//write files		
		File rosterOutputFile = new File(this.getBaseOutputLocation(),super.getFileNameStringByType(SimFileType.ROSTER_FILE));
		logger.info("Writing roster output file: " + rosterOutputFile.getAbsolutePath());
		RosterTools.writeRoster(rosterList,rosterOutputFile);


	}
	


}
