package eu.fp7.scase.restreviews.utilities.authorization.core;

import eu.fp7.scase.restreviews.utilities.authorization.finders.AttributeFinder;
import eu.fp7.scase.restreviews.utilities.authorization.finders.PolicyFinder;

public class AzEvaluationContext extends AbstractEvaluationContext{

	private AzRequest<?> request;
	private static final AttributeFinder attributeFinder;
	private static final PolicyFinder policyFinder;

	static{
		policyFinder= new PolicyFinder();
		attributeFinder= new AttributeFinder();
	}
	
	public AzEvaluationContext(AzRequest<?> request){
		this.request = request;
	}

	/**
	 * @return the request
	 */
	@Override
	public AzRequest<?> getRequest() {
		return request;
	}

	/**
	 * @return the attributeFinder
	 */
	@Override
	public AttributeFinder getAttributeFinder() {
		return attributeFinder;
	}
	
	/**
	 * @return the policyFinder
	 */
	@Override
	public PolicyFinder getPolicyFinder() {
		return policyFinder;
	}

}

