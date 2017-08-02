package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractPolicy;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public class DenyOverridesPolicyCombAlgorithm extends PolicyCombiningAlgorithm {

	public DenyOverridesPolicyCombAlgorithm() {
		super(CombiningAlgorithmEnum.DENY_OVERRIDES);
	}
	
	@Override
	public AuthorizationResult combine(AbstractEvaluationContext evaluationContext,List<?> policies) {
		List<String> errors = new ArrayList<String>();
		boolean atLeastOnePermit = false;
		boolean atLeastOneError = false;
		Iterator<?> it = policies.iterator();
		
		 while (it.hasNext()) {
			 AbstractPolicy policy = (AbstractPolicy)it.next();
			 AuthorizationResult result = policy.evaluate(evaluationContext);
			 
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

