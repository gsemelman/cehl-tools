package org.cehl.cehltools;

import java.io.File;

import org.cehl.cehltools.jobs.CashUpdaterJob;
import org.cehl.cehltools.jobs.CoachUpdaterJob;
import org.cehl.cehltools.jobs.ContractImportJob;
import org.cehl.cehltools.jobs.ProspecImportJob;
import org.cehl.cehltools.jobs.ProspectFileUpdaterJob;
import org.cehl.cehltools.jobs.RerateImportJob;
import org.cehl.cehltools.jobs.RookieFixJob;
import org.cehl.cehltools.jobs.UnassignedCleanupJob;
import org.cehl.commons.spring.StaticContextAccessor;

public class JobRunner {

    public static void coachImport(File inputFile){
    	CoachUpdaterJob job = StaticContextAccessor.getBean(CoachUpdaterJob.class);
    	job.setCoachInputFile(inputFile);
    	job.runJob();
    }
    
    public static void unassignedCleanup(File inputFile){
    	UnassignedCleanupJob job = StaticContextAccessor.getBean(UnassignedCleanupJob.class);
    	job.setInputFile(inputFile);
    	job.runJob();
    }
    
    public static void prospectImport(File inputFile){
    	ProspecImportJob job = StaticContextAccessor.getBean(ProspecImportJob.class);
    	job.setInputFile(inputFile);
    	job.runJob();
    }
    
    public static void rerateImport(File inputFile){
    	RerateImportJob job = StaticContextAccessor.getBean(RerateImportJob.class);
    	job.setInputFile(inputFile);
    	job.runJob();
    }
    
    public static void contractImport(File inputFile){
    	ContractImportJob job = StaticContextAccessor.getBean(ContractImportJob.class);
    	job.setInputFile(inputFile);
    	job.runJob();
    }
    
    public static void cashImport(File inputFile){
    	CashUpdaterJob job = StaticContextAccessor.getBean(CashUpdaterJob.class);
    	job.setInputFile(inputFile);
    	job.runJob();
    }
    
    public static void prospectFileImport(File inputFile){
    	ProspectFileUpdaterJob job = StaticContextAccessor.getBean(ProspectFileUpdaterJob.class);
    	job.setInputFile(inputFile);
    	job.runJob();
    }
    
    public static void rookieFix(File inputFile){
    	RookieFixJob job = StaticContextAccessor.getBean(RookieFixJob.class);
    	job.setInputFile(inputFile);
    	job.runJob();
    }
}
