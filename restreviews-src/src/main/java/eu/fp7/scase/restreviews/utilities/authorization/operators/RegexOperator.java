
package eu.fp7.scase.restreviews.utilities.authorization.operators;

import java.util.Iterator;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.attr.AttributeValue;
import eu.fp7.scase.restreviews.utilities.authorization.attr.BooleanAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.attr.CollectionOfAttributes;
import eu.fp7.scase.restreviews.utilities.authorization.attr.StringAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.enums.OperatorEnum;

public class RegexOperator extends Operator {
	
	private final OperatorEnum operatorId;
	
	protected RegexOperator(OperatorEnum operatorId){
		this.operatorId = operatorId;
	}
	
	@Override
	public EvaluationResult evaluate(List<AttributeValue> inputs) {
		if (inputs.size() != 2){
			return new EvaluationResult("Wrong Number of inputs : " + inputs.size() + " instead of 2");
		}
		if (!inputs.get(1).getType().equalsIgnoreCase("String")){
			return new EvaluationResult("A regular expression must be a String.");
		}
		String regex = ((StringAttribute) inputs.get(0)).getValue();
		if (inputs.get(0).isCollection()){
			CollectionOfAttributes col = (CollectionOfAttributes) inputs.get(0);
			Iterator<AttributeValue> i = col.getValue().iterator();
			while (i.hasNext()){
				AttributeValue value = i.next();
				if (value.getType().equalsIgnoreCase("String")){
					StringAttribute oStringAttribute = (StringAttribute) value;
					if (!oStringAttribute.getValue().matches(regex)){
						return EvaluationResult.getInstance(false);
					}
				}else{
					return new EvaluationResult("Argument must be a String.");
				}
			}
			return EvaluationResult.getInstance(true);
		}else{
			if (inputs.get(0).getType().equalsIgnoreCase("String")){
				StringAttribute oStringAttribute = (StringAttribute) inputs.get(0);
				return EvaluationResult.getInstance(oStringAttribute.getValue().matches(regex));
			}else{
				return new EvaluationResult("Argument must be a String.");
			}
		}
	}

	@Override
	public OperatorEnum getOperatorId() {
		return operatorId;
	}

}

