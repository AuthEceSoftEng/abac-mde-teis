package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessRule;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public class DenyOverridesRuleCombAlgorithm extends RuleCombiningAlgorithm {

	public DenyOverridesRuleCombAlgorithm() {
		super(CombiningAlgorithmEnum.DENY_OVERRIDES);
	}
	
	@Override
	public AuthorizationResult combine(AbstractEvaluationContext evaluationContext, List<?> rules) {
		List<String> errors = new ArrayList<String>();
		boolean atLeastOnePermit = false;
		boolean atLeastOneError = false;
		Iterator<?> it = rules.iterator();
		
		 while (it.hasNext()) {
			 ResourceAccessRule rule = (ResourceAccessRule)it.next();
			 AuthorizationResult result = rule.evaluate(evaluationContext);
			 
			 if (result.getResultCode() == AuthorizationResultCode.DENY){
				 return result;
			 }		 
			 if (!atLeastOnePermit && result.getResultCode() == AuthorizationResultCode.PERMIT){
				 atLeastOnePermit = true;
			 }			 
			 if(!atLeastOneError && result.getResultCode() == AuthorizationResultCode.INDETERMINATE){
				 atLeastOneError = true;
				 errors.addAll(result.getErrorMessages());
			 }
		 }
		 
		 if (atLeastOneError){
			 return new AuthorizationResult(AuthorizationResultCode.INDETERMINATE,errors);
		 }
		 if (atLeastOnePermit){
			 return new AuthorizationResult(AuthorizationResultCode.PERMIT);
		 }
		 
		 return new AuthorizationResult(AuthorizationResultCode.NOT_APPLICABLE);
	}

}


