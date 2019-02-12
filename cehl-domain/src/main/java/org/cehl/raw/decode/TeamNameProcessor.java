package org.cehl.raw.decode;

import org.cehl.raw.Teams;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class TeamNameProcessor extends CellProcessorAdaptor{

    public TeamNameProcessor() {
    	super();
    }
    
    public TeamNameProcessor(CellProcessor next) {
        // this constructor allows other processors to be chained
    	super(next);
    }
    
    public Object execute(Object value, CsvContext context) {
            
        validateInputNotNull(value, context);  // throws an Exception if the input is null

    	Teams team = Teams.fromName(value.toString());
    	
    	if(team == null) {
    		throw new SuperCsvCellProcessorException(
                    String.format("Could not parse rating '%s' as it is not a valid team", value), context, this);
    	}

        return next.execute(team, context);
           
    }
}