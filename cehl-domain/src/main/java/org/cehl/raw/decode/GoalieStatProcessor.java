package org.cehl.raw.decode;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import com.google.common.collect.Lists;

//public class GoalieStatProcessor extends CellProcessorAdaptor{
public class GoalieStatProcessor extends CellProcessorAdaptor{
	static final ArrayList<String> NA_STRINGS = Lists.newArrayList("NA","N/A","#N/A");
	
    public GoalieStatProcessor() {
            super();
    }
    
    public GoalieStatProcessor(CellProcessor next) {
            // this constructor allows other processors to be chained after ParseDay
            super(next);
    }
    
    public Object execute(Object value, CsvContext context) {
            
            //validateInputNotNull(value, context);  // throws an Exception if the input is null
            
            if(!NumberUtils.isCreatable(value.toString())) {
                if(NA_STRINGS.contains(value.toString())){
                	return next.execute(0, context);
                }
            }
            
//            if(NA_STRINGS.contains(value.toString())){
//            	return next.execute(0, context);
//            }
            
//                try{
//                	Integer.parseInt(value.toString());
//                }catch(NumberFormatException e){
//                	 throw new SuperCsvCellProcessorException(
//                             String.format("Could not parse value '%s' as it is not an integer value", value), context, this);
//                }
            
            return next.execute(value, context);
           
    }
}
