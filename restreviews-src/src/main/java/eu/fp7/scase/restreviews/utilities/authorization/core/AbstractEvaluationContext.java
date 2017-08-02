package eu.fp7.scase.restreviews.utilities.authorization.core;

import eu.fp7.scase.restreviews.utilities.authorization.finders.AttributeFinder;
import eu.fp7.scase.restreviews.utilities.authorization.finders.PolicyFinder;

public abstract class AbstractEvaluationContext {
	/**
	 * @return the request
	 */
	public abstract AzRequest<?> getRequest();

	/**
	 * @return the attributeFinder
	 */
	public abstract AttributeFinder getAttributeFinder();
	
	/**
	 * @return the policyFinder
	 */
	public abstract PolicyFinder getPolicyFinder();
}

