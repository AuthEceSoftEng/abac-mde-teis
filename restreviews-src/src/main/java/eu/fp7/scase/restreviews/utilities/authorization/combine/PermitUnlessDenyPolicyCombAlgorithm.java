
package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.Iterator;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractPolicy;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public class PermitUnlessDenyPolicyCombAlgorithm extends PolicyCombiningAlgorithm{

	protected PermitUnlessDenyPolicyCombAlgorithm() {
		super(CombiningAlgorithmEnum.PERMIT_UNLESS_DENY);
	}

	@Override
	public AuthorizationResult combine(AbstractEvaluationContext evaluationContext, List<?> policies) {
		Iterator<?> it = policies.iterator();
		
		 while (it.hasNext()) {
			 AbstractPolicy policy = (AbstractPolicy)it.next();
			 AuthorizationResult result = policy.evaluate(evaluationContext);
			 
			 if (result.getResultCode() == AuthorizationResultCode.DENY){
				 return result;
			 }		 
		 }
		 return new AuthorizationResult(AuthorizationResultCode.PERMIT);
	}

}
