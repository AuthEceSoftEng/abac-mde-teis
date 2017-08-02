package eu.fp7.scase.restreviews.utilities.authorization.core;

public abstract class AbstractPolicy implements Evaluatable {

	public abstract AuthorizationResult evaluate(AbstractEvaluationContext evaluationContext);

}
