package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessRule;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public class PermitOverridesRuleCombAlgorithm extends RuleCombiningAlgorithm {

	public PermitOverridesRuleCombAlgorithm() {
		super(CombiningAlgorithmEnum.PERMIT_OVERRIDES);
	}
	
	@Override
	public AuthorizationResult combine(AbstractEvaluationContext evaluationContext,List<?> rules) {
		List<String> errors = new ArrayList<String>();
		boolean atLeastOneDeny = false;
		boolean atLeastOneError = false;
		Iterator<?> it = rules.iterator();
		
		 while (it.hasNext()) {
			 ResourceAccessRule rule = (ResourceAccessRule)it.next();
			 AuthorizationResult result = rule.evaluate(evaluationContext);
			 
			 if (result.getResultCode() == AuthorizationResultCode.PERMIT){
				 return result;
			 }		 
			 if (!atLeastOneDeny && result.getResultCode() == AuthorizationResultCode.DENY){
				 atLeastOneDeny = true;
			 }			 
			 if(!atLeastOneError && result.getResultCode() == AuthorizationResultCode.INDETERMINATE){
				 atLeastOneError = true;
				 errors.addAll(result.getErrorMessages());
			 }
		 }
		 
		 if (atLeastOneError){
			 return new AuthorizationResult(AuthorizationResultCode.INDETERMINATE,errors);
		 }
		 if (atLeastOneDeny){
			 return new AuthorizationResult(AuthorizationResultCode.DENY);
		 }
		 
		 return new AuthorizationResult(AuthorizationResultCode.NOT_APPLICABLE);
	}

}


