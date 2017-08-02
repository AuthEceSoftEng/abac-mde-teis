package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public abstract class PolicyCombiningAlgorithm extends CombiningAlgorithm{

	protected PolicyCombiningAlgorithm(CombiningAlgorithmEnum id){
		super(id);
	}
	
	public abstract AuthorizationResult combine(AbstractEvaluationContext evaluationContext, List<?> policies);
	
	@Override
	public boolean isPolicyCombiningAlgorithm(){
		return true;
	}
	
	public static PolicyCombiningAlgorithm getInstance(CombiningAlgorithmEnum policyCombiningAlgorithmId) {
		switch (policyCombiningAlgorithmId){
		case DENY_OVERRIDES:
			return new DenyOverridesPolicyCombAlgorithm();
		case PERMIT_OVERRIDES:
			return new PermitOverridesPolicyCombAlgorithm();
		case DENY_UNLESS_PERMIT:
			return new DenyUnlessPermitPolicyCombAlgorithm();
		case PERMIT_UNLESS_DENY:	
			return new PermitUnlessDenyPolicyCombAlgorithm();
		default:
			return null;
		
		}
	}
}
