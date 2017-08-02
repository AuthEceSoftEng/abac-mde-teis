package eu.fp7.scase.restreviews.utilities.authorization.finders;

import java.util.Set;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AttributeCategory;
import eu.fp7.scase.restreviews.utilities.authorization.operators.EvaluationResult;

public abstract class AttributeFinderModule {

	public Set<String> getSupportedType() {
		return null;
	}

	public Set<AttributeCategory> getSupportedCategory() {
		return null;
	}

	public EvaluationResult findAttribute(ResourceAccessAttribute attribute, AbstractEvaluationContext evaluationContext) {
		return new EvaluationResult("Not Implemented");
	}

}
