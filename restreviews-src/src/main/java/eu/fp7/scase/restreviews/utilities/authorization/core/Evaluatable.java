package eu.fp7.scase.restreviews.utilities.authorization.core;

public interface Evaluatable {

	public AuthorizationResult evaluate(AbstractEvaluationContext evaluationContext);
}

