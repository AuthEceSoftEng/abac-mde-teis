package eu.fp7.scase.restreviews.utilities.authorization.operators;

import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.attr.AttributeValue;
import eu.fp7.scase.restreviews.utilities.authorization.attr.BooleanAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.enums.OperatorEnum;

public class EqualOperator extends Operator{

	private final OperatorEnum operatorId;
	
	protected EqualOperator(OperatorEnum operatorId){
		this.operatorId = operatorId;
	}
	
	@Override
	public EvaluationResult evaluate(List<AttributeValue> inputs) {
		if (inputs.size() != 2){
			return new EvaluationResult("Wrong Number of inputs : " + inputs.size() + " instead of 2");
		}
		if(inputs.get(0).isCollection() ^ inputs.get(1).isCollection()){
			return new EvaluationResult("Trying to equalize a collection with an instance");
		}
		if (!inputs.get(0).getType().equals(inputs.get(1).getType())){
			return new EvaluationResult("Different types between operands");
		}
		
		switch (operatorId){
		case EQUAL:
			return EvaluationResult.getInstance(inputs.get(0).equals(inputs.get(1)));	
		case NOT_EQUAL:
			return EvaluationResult.getInstance(!inputs.get(0).equals(inputs.get(1)));			
		default:
			return new EvaluationResult("Unsupported operator");
		
		}		
	}

	/**
	 * @return the operatorId
	 */
	@Override
	public OperatorEnum getOperatorId() {
		return operatorId;
	}

}

