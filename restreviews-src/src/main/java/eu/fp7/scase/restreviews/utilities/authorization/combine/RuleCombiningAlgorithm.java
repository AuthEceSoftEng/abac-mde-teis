package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public abstract class RuleCombiningAlgorithm extends CombiningAlgorithm {

	protected RuleCombiningAlgorithm(CombiningAlgorithmEnum id){
		super(id);
	}
	
	public abstract AuthorizationResult combine(AbstractEvaluationContext evaluationContext,List<?> elements);

	@Override
	public boolean isRuleCombiningAlgorithm(){
		return true;
	}
	
	public static RuleCombiningAlgorithm getInstance(CombiningAlgorithmEnum ruleCombiningAlgorithmId) {
		switch (ruleCombiningAlgorithmId){
		case DENY_OVERRIDES:
			return new DenyOverridesRuleCombAlgorithm();
		case PERMIT_OVERRIDES:
			return new PermitOverridesRuleCombAlgorithm();
		case DENY_UNLESS_PERMIT:
			return new DenyUnlessPermitRuleCombAlgorithm();
		case PERMIT_UNLESS_DENY:	
			return new PermitUnlessDenyRuleCombAlgorithm();
		default:
			return null;
		
		}
	}

}
