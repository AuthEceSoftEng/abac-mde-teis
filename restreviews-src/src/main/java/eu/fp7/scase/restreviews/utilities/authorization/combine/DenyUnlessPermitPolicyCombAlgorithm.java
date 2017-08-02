
package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.Iterator;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractPolicy;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public class DenyUnlessPermitPolicyCombAlgorithm extends PolicyCombiningAlgorithm{

	protected DenyUnlessPermitPolicyCombAlgorithm() {
		super(CombiningAlgorithmEnum.DENY_UNLESS_PERMIT);
	}

	@Override
	public AuthorizationResult combine(AbstractEvaluationContext evaluationContext, List<?> policies) {
		Iterator<?> it = policies.iterator();
		
		 while (it.hasNext()) {
			 AbstractPolicy policy = (AbstractPolicy)it.next();
			 AuthorizationResult result = policy.evaluate(evaluationContext);
			 
			 if (result.getResultCode() == AuthorizationResultCode.PERMIT){
				 return result;
			 }		 
		 }
		 return new AuthorizationResult(AuthorizationResultCode.DENY);
	}

}
