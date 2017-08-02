package eu.fp7.scase.restreviews.utilities.authorization.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.fp7.scase.restreviews.utilities.authorization.attr.AttributeValue;
import eu.fp7.scase.restreviews.utilities.authorization.enums.OperatorEnum;

public abstract class Operator {
	
	public abstract EvaluationResult evaluate(List<AttributeValue> inputs);
	public abstract OperatorEnum getOperatorId();
	
	private static Map<OperatorEnum , Operator> operatorMap;
	
	static{
		//add all the supported operators here
		operatorMap = new HashMap<OperatorEnum, Operator>();
		operatorMap.put(OperatorEnum.EQUAL, new EqualOperator(OperatorEnum.EQUAL));
		operatorMap.put(OperatorEnum.NOT_EQUAL, new EqualOperator(OperatorEnum.NOT_EQUAL));
		operatorMap.put(OperatorEnum.GREATER_THAN, new ComparisonOperator(OperatorEnum.GREATER_THAN));
		operatorMap.put(OperatorEnum.LESS_THAN, new ComparisonOperator(OperatorEnum.LESS_THAN));
		operatorMap.put(OperatorEnum.GREATER_THAN_OR_EQUAL, new ComparisonOperator(OperatorEnum.GREATER_THAN_OR_EQUAL));
		operatorMap.put(OperatorEnum.LESS_THAN_OR_EQUAL, new ComparisonOperator(OperatorEnum.LESS_THAN_OR_EQUAL));
		operatorMap.put(OperatorEnum.SUBSET, new CollectionContainsOperator(OperatorEnum.SUBSET));
		operatorMap.put(OperatorEnum.NOT_SUBSET, new CollectionContainsOperator(OperatorEnum.NOT_SUBSET));
		operatorMap.put(OperatorEnum.SET_CONTAINS, new CollectionContainsOperator(OperatorEnum.SET_CONTAINS));
		operatorMap.put(OperatorEnum.SET_NOT_CONTAINS, new CollectionContainsOperator(OperatorEnum.SET_NOT_CONTAINS));
		operatorMap.put(OperatorEnum.REGEX, new RegexOperator(OperatorEnum.REGEX));
		
	}
	
	public static Operator getInstance(OperatorEnum operatorId){
		return operatorMap.get(operatorId);
	}
	
	public Set<String> getSupportedTypes(){
		return null;
	}
}

