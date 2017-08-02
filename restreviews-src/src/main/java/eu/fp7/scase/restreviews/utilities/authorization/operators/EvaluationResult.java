package eu.fp7.scase.restreviews.utilities.authorization.operators;

import eu.fp7.scase.restreviews.utilities.authorization.attr.AttributeValue;
import eu.fp7.scase.restreviews.utilities.authorization.attr.BooleanAttribute;

public class EvaluationResult {

	private AttributeValue attributeValue;
	private String errorStatus;
	
    private static EvaluationResult falseBooleanResult;
    private static EvaluationResult trueBooleanResult;
	
	/**
	 * @return the attributeValue
	 */
	public AttributeValue getAttributeValue() {
		return attributeValue;
	}

	/**
	 * @return the errorStatus
	 */
	public String getErrorStatus() {
		return errorStatus;
	}

	public EvaluationResult(String errorStatus){
		this(null,errorStatus);
		System.out.println(errorStatus);
	}
	
	public EvaluationResult(AttributeValue attributeValue){
		this(attributeValue,"");
	}
	
	public EvaluationResult(AttributeValue attributeValue, String errorStatus){
		this.attributeValue = attributeValue;
		this.errorStatus = errorStatus;
	}
	
    public static EvaluationResult getInstance(boolean value) {
        if (value)
            return getTrueInstance();
        else
            return getFalseInstance();
    }

    public static EvaluationResult getFalseInstance() {
        if (falseBooleanResult == null) {
            falseBooleanResult = new EvaluationResult(BooleanAttribute.getFalseInstance());
        }
        return falseBooleanResult;
    }

    public static EvaluationResult getTrueInstance() {
        if (trueBooleanResult == null) {
            trueBooleanResult = new EvaluationResult(BooleanAttribute.getTrueInstance());
        }
        return trueBooleanResult;
    }
	
}


