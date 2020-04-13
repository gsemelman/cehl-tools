package org.cehl.cehltools;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.cehl.cehltools.config.CehlToolsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

public class App{
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
	static final int EXIT_CODE_SUCCESS = 0;
	static final int EXIT_CODE_FAIL = 1;
	
	static final String CMD_LINE_OPT_HELP = "help";
	static final String CMD_LINE_UPDATE_COACHES = "coachUpdate";
	static final String CMD_LINE_PROSPECT_IMPORT = "prospectImport";
	static final String CMD_LINE_UNASSIGNED_CLEANUP = "unassignedCleanup";
	static final String CMD_LINE_RERATE_IMPORT = "rerateImport";
	static final String CMD_LINE_CONTRACT_IMPORT = "contractImport";
	static final String CMD_LINE_TEAM_CASH_IMPORT = "financesImport";
	static final String CMD_LINE_PROSPECT_FILE_IMPORT = "prospectFileImport";
	static final String CMD_LINE_ROOKIE_FIX = "rookieFix";
	static final String CMD_LINE_HOLDOUT = "holdoutUpdate";
	static final String CMD_LINE_UPLOAD = "upload";
	static final String CMD_LINE_ROSTER_EXPORT = "rosterExport";
	static final String CMD_LINE_UNASSIGNED_RERATE = "unassignedRerate";
	
	private Options commandLineOptions;
	private ApplicationContext ctx;
	
    public static void main( String[] args )
    {
    	App app = new App();
    	logger.debug("Arguments:");
    	for(String arg : args){
    		logger.debug("argument:" + arg);
    	}
    	
    	CommandLine commandLine = app.parseCommandLineOptions(args);
    	
    	app.executeConfigurationCommandLineOptions(commandLine);
    	
    	app.initAppContext(args);
    	
    	app.init();
    	
    	app.executeLaunchCommandLineOptions(commandLine);

    }
    
    CommandLine parseCommandLineOptions(String[] args) {

		commandLineOptions = new Options();
		
		boolean hasArg = true;
		
		//these commands are mutually exclusive so use an OptionGroup
		OptionGroup optionGroup = new OptionGroup();
		Option option;

		//mutually exclusive group (add one offs here)
		option = new Option(CMD_LINE_OPT_HELP, "Display command line options");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_UPDATE_COACHES, hasArg,"Replace and update team coaches from csv file");
		option.setArgName("import csv location");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_PROSPECT_IMPORT, hasArg,"Import prospects and add to teams from csv");
		option.setArgName("import csv location");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_UNASSIGNED_CLEANUP, hasArg,"Cleanup unassigned list from list of names in csv file");
		option.setArgName("Cleanup unassigned list");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_RERATE_IMPORT, hasArg,"Imports Rerates from list of rerates in csv file");
		option.setArgName("import csv location");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_CONTRACT_IMPORT, hasArg,"Imports Contracts from list of rerates in csv file");
		option.setArgName("import csv location");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);

		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_TEAM_CASH_IMPORT, hasArg,"Imports Team Finances from list of rerates in csv file");
		option.setArgName("import csv location");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_PROSPECT_FILE_IMPORT, hasArg,"Import prospects and add to teams from csv to pct file");
		option.setArgName("import csv location");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_ROOKIE_FIX, hasArg,"Remove rookie status for roster players over threshold");
		option.setArgName("import location (ros/csv)");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_HOLDOUT, false,"Update holdout status");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_UPLOAD, false,"Upload");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_ROSTER_EXPORT, false,"Roster Export");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		optionGroup = new OptionGroup();
		option = new Option(CMD_LINE_UNASSIGNED_RERATE, false,"Unassigned rerate");
		optionGroup.addOption(option);
		commandLineOptions.addOptionGroup(optionGroup);
		
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {

			cmd = parser.parse(commandLineOptions, args, true);

		} catch (MissingOptionException | MissingArgumentException e) {
			logger.error("Incorrect arguments passed in. Displaying command line options");
			usage();
			//throw new IllegalArgumentException();
			System.exit(-1); 
		}catch(Exception e) {
			logger.error("***ERROR: " 
					+ e.getClass() + ": " 
					+ e.getMessage());
			System.exit(-1); 
		}
		
		return cmd;
		
	}
    
	void executeConfigurationCommandLineOptions(CommandLine cmdLine) {
		
		if(cmdLine.hasOption(CMD_LINE_OPT_HELP)) {
			usage();
			System.exit(EXIT_CODE_SUCCESS);
		}
		
//		if(cmdLine.hasOption(CMD_LINE_OPT_PROPERTIES_FILE_LOCATION)) {
//			
//			String optionArg = cmdLine.getOptionValue(CMD_LINE_OPT_PROPERTIES_FILE_LOCATION);
//
//			//push the specified properties file location on system properties
//			//to initialize property placeholder configurer
//			System.getProperties().put(
//				PROPERTIES_FILE_LOCATION_SYSTEM_PROPERTY_NAME,
//				optionArg);
//			
//		}
		
	}
    
    void executeLaunchCommandLineOptions(CommandLine cmdLine) {
    	
		if(cmdLine.hasOption(CMD_LINE_UPDATE_COACHES)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_UPDATE_COACHES);
			if(optionArg == null){
				logger.error("Location of coach update csv must be passed in as parameter (with -" + CMD_LINE_UPDATE_COACHES + ")");
				System.exit(1);
			}
			JobRunner.coachImport(new File(optionArg));
			return;
		}else if(cmdLine.hasOption(CMD_LINE_PROSPECT_IMPORT)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_PROSPECT_IMPORT);
			if(optionArg == null){
				logger.error("Location of prospect import cleanup csv must be passed in as parameter (with -" + CMD_LINE_PROSPECT_IMPORT + ")");
				System.exit(1);
			}
			JobRunner.prospectImport(new File(optionArg));
			return;
		}else if(cmdLine.hasOption(CMD_LINE_UNASSIGNED_CLEANUP)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_UNASSIGNED_CLEANUP);
			if(optionArg == null){
				logger.error("Location of unassigned list cleanup csv must be passed in as parameter (with -" + CMD_LINE_UNASSIGNED_CLEANUP + ")");
				System.exit(1);
			}
			JobRunner.unassignedCleanup(new File(optionArg));
			return;
		}else if(cmdLine.hasOption(CMD_LINE_RERATE_IMPORT)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_RERATE_IMPORT);
			if(optionArg == null){
				logger.error("Location of re-rate csv must be passed in as parameter (with -" + CMD_LINE_RERATE_IMPORT + ")");
				System.exit(1);
			}
			JobRunner.rerateImport(new File(optionArg));
			return;
		}else if(cmdLine.hasOption(CMD_LINE_CONTRACT_IMPORT)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_CONTRACT_IMPORT);
			if(optionArg == null){
				logger.error("Location of contract csv must be passed in as parameter (with -" + CMD_LINE_CONTRACT_IMPORT + ")");
				System.exit(1);
			}
			JobRunner.contractImport(new File(optionArg));
			return;
		}else if(cmdLine.hasOption(CMD_LINE_TEAM_CASH_IMPORT)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_TEAM_CASH_IMPORT);
			if(optionArg == null){
				logger.error("Location of finances csv must be passed in as parameter (with -" + CMD_LINE_TEAM_CASH_IMPORT + ")");
				System.exit(1);
			}
			JobRunner.cashImport(new File(optionArg));
			return;
		}else if(cmdLine.hasOption(CMD_LINE_PROSPECT_FILE_IMPORT)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_PROSPECT_FILE_IMPORT);
			if(optionArg == null){
				logger.error("Location of provspect csv must be passed in as parameter (with -" + CMD_LINE_PROSPECT_FILE_IMPORT + ")");
				System.exit(1);
			}
			JobRunner.prospectFileImport(new File(optionArg));
			return;
		}else if(cmdLine.hasOption(CMD_LINE_ROOKIE_FIX)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_ROOKIE_FIX);
			if(optionArg == null){
				logger.error("Previous season ROS file or CSV must be passed in as parameter (with -" + CMD_LINE_ROOKIE_FIX + ")");
				System.exit(1);
			}
			JobRunner.rookieFix(new File(optionArg));
			return;
		}else if(cmdLine.hasOption(CMD_LINE_HOLDOUT)) {
			JobRunner.updateHoldouts();
			return;
		}else if(cmdLine.hasOption(CMD_LINE_UPLOAD)) {
			JobRunner.uploadfiles();
			return;
		}else if(cmdLine.hasOption(CMD_LINE_ROSTER_EXPORT)) {
			JobRunner.rosterExport();
			return;
		}else if(cmdLine.hasOption(CMD_LINE_UNASSIGNED_RERATE)) {
			String optionArg = cmdLine.getOptionValue(CMD_LINE_UNASSIGNED_RERATE);
			if(optionArg == null){
				logger.error(" CSV must be passed in as parameter (with -" + CMD_LINE_UNASSIGNED_RERATE + ")");
				System.exit(1);
			}
			JobRunner.unassignedRerate(new File(optionArg));
			return;
		}else{
			usage();
		}
		
		
    }
    
    void initAppContext( String[] args ){
    	ctx = new SpringApplicationBuilder(CehlToolsConfig.class)
    			.bannerMode(Banner.Mode.OFF) // do not print spring boot banner
    			//.
    			//.web(false)
    			.run(args);
    	

    }
    
    void init(){
    	File backupLocation = ctx.getBean("backupLocation", File.class);
    	if(!backupLocation.exists()){
    		backupLocation.mkdirs();
    	}
    	
    	File outputLocation = ctx.getBean("baseOutputLocation", File.class);
    	if(!outputLocation.exists()){
    		outputLocation.mkdirs();
    	}
    	
    }
    
    void initContextForJobType(JobType jobType, String[] args){
    	switch(jobType){
		case COACH_UPDATE:
			break;
		default:
			break;
    	
    	}
    }
    
    JobRunner getJobRunner(JobType jobType){
    	JobRunner jobRunner = new JobRunner();
    	
		return jobRunner;
    }
    
	void usage() {
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "CEHL Tools Command line options",commandLineOptions);
		
	}
    
}
