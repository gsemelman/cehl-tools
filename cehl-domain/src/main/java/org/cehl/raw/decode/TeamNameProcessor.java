package org.cehl.raw.decode;

import org.cehl.raw.CehlTeam;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class TeamNameProcessor extends CellProcessorAdaptor{

	boolean returnString = false;
	
    public TeamNameProcessor() {
    	super();
    }
    
    public TeamNameProcessor(boolean returnString) {
    	super();
    	this.returnString = returnString;
    }
    
    public TeamNameProcessor(CellProcessor next) {
        // this constructor allows other processors to be chained
    	super(next);
    }
    
    public Object execute(Object value, CsvContext context) {
            
        validateInputNotNull(value, context);  // throws an Exception if the input is null

        //support both tteam name and abbreviation
    	CehlTeam team = CehlTeam.fromName(value.toString());
    	
    	if(team == null) {
    		team = CehlTeam.fromAbbr(value.toString());
    	}
    	
    	if(team == null) {
    		throw new SuperCsvCellProcessorException(
                    String.format("Could not parse rating '%s' as it is not a valid team", value), context, this);
    	}
    	
    	if(returnString)  return next.execute(team.getName(), context);

        return next.execute(team, context);
           
    }
}