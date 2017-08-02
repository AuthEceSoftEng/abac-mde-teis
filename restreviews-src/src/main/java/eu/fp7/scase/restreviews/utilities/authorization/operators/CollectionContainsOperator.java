package eu.fp7.scase.restreviews.utilities.authorization.operators;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;

import eu.fp7.scase.restreviews.utilities.authorization.attr.AttributeValue;
import eu.fp7.scase.restreviews.utilities.authorization.attr.CollectionOfAttributes;
import eu.fp7.scase.restreviews.utilities.authorization.enums.OperatorEnum;

public class CollectionContainsOperator extends Operator {

	private final OperatorEnum operatorId;
	
	protected CollectionContainsOperator(OperatorEnum operatorId){
		this.operatorId = operatorId;
	}
	
	@Override
	public EvaluationResult evaluate(List<AttributeValue> inputs) {
		if (inputs.size() != 2){
			return new EvaluationResult("Wrong Number of inputs : " + inputs.size() + " instead of 2");
		}
		Collection<String> col1 = new ArrayList<String>();
		Collection<String> col2 = new ArrayList<String>();

		if (inputs.get(0).isCollection()){
			Iterator<AttributeValue> leftOperandIterator = ((CollectionOfAttributes)inputs.get(0)).getValue().iterator();
			while(leftOperandIterator.hasNext()){
				col1.add(leftOperandIterator.next().getValue().toString());
			}
		}else{
			col1.add(inputs.get(0).getValue().toString());			
		}

		if (inputs.get(1).isCollection()){
			Iterator<AttributeValue> rightOperandIterator = ((CollectionOfAttributes)inputs.get(1)).getValue().iterator();
			while(rightOperandIterator.hasNext()){
				col2.add(rightOperandIterator.next().getValue().toString());
			}
		}else{
			col2.add(inputs.get(1).getValue().toString());				
		}
		
		switch (operatorId){
		case SUBSET:
			return  EvaluationResult.getInstance(col2.containsAll(col1));
		case NOT_SUBSET:
			return EvaluationResult.getInstance(!col2.containsAll(col1));
		case SET_CONTAINS:
			return EvaluationResult.getInstance(col1.containsAll(col2));
		case SET_NOT_CONTAINS:
			return EvaluationResult.getInstance(!col1.containsAll(col2));
		default:
			return new EvaluationResult("Unsupported comparison operator");			
		}
	}

	@Override
	public OperatorEnum getOperatorId() {
		return operatorId;
	}

}

