
package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.Iterator;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessRule;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public class PermitUnlessDenyRuleCombAlgorithm extends RuleCombiningAlgorithm{

	protected PermitUnlessDenyRuleCombAlgorithm() {
		super(CombiningAlgorithmEnum.PERMIT_UNLESS_DENY);
	}

	@Override
	public AuthorizationResult combine(AbstractEvaluationContext evaluationContext, List<?> rules) {
		Iterator<?> it = rules.iterator();
		
		 while (it.hasNext()) {
			 ResourceAccessRule rule = (ResourceAccessRule)it.next();
			 AuthorizationResult result = rule.evaluate(evaluationContext);
			 
			 if (result.getResultCode() == AuthorizationResultCode.DENY){
				 return result;
			 }		 
		 }
		 return new AuthorizationResult(AuthorizationResultCode.PERMIT);
	}

}