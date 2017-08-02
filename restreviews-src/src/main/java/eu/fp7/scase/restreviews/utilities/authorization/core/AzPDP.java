package eu.fp7.scase.restreviews.utilities.authorization.core;

import java.util.List;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.utilities.authorization.enums.Action;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;

public class AzPDP {

	public AzPDP(){
		
	}
	
	public AuthorizationResult evaluate(AzRequest<?> azRequest){
		AbstractEvaluationContext evaluationContext = new AzEvaluationContext(azRequest);
		return this.evaluate(evaluationContext);
	}

	public AuthorizationResult evaluate(AbstractEvaluationContext evaluationContext){
		List<ResourceAccessPolicySet> oResourceAccessPolicySet = evaluationContext.getPolicyFinder().findPolicy(evaluationContext.getRequest().getAccessedResourceType());
		if (oResourceAccessPolicySet.isEmpty()){
			return new AuthorizationResult(AuthorizationResultCode.NOT_APPLICABLE, "No applicable ResourceAccessPolicySet was found for resource : " + evaluationContext.getRequest().getAccessedResourceType());
		}
		if (oResourceAccessPolicySet.size() > 1){
			return new AuthorizationResult(AuthorizationResultCode.INDETERMINATE, "Multiple root ResourceAccessPolicySets were found for resource : " + evaluationContext.getRequest().getAccessedResourceType());
		}
		return oResourceAccessPolicySet.get(0).evaluate(evaluationContext);
	}

	public AuthorizationResultCode getPermission(JavaaccountModel oAuthenticationAccount, String resourceType, String resourceId, Object includedResource, Action action)
	{
		AzRequest<JavaaccountModel> request = new AzRequest<JavaaccountModel>(oAuthenticationAccount,resourceType, resourceId, includedResource, action);    	
   		return filterResult(request);   	
	}

	public AuthorizationResultCode getPermission(String resourceType, String resourceId, Object includedResource, Action action)
	{
		AzRequest<JavaaccountModel> request = new AzRequest<JavaaccountModel>(resourceType, resourceId, includedResource, action);    	
    	return filterResult(request);   	
	}

	public AuthorizationResultCode getPermission(JavaaccountModel oAuthenticationAccount, String resourceType, String postedResourceParentType, String resourceId, Object includedResource, Action action)
	{
		AzRequest<JavaaccountModel> request = new AzRequest<JavaaccountModel>(oAuthenticationAccount,resourceType, postedResourceParentType, resourceId, includedResource, action);    	
   		return filterResult(request);   	
	}

	public AuthorizationResultCode getPermission(String resourceType, String postedResourceParentType, String resourceId, Object includedResource, Action action)
	{
		AzRequest<JavaaccountModel> request = new AzRequest<JavaaccountModel>(resourceType, postedResourceParentType, resourceId, includedResource, action);    	
    	return filterResult(request);   	
	}

	private AuthorizationResultCode filterResult(AzRequest request){
		if (request == null){
			return null;
		}
		AuthorizationResultCode oAuthorizationResultCode = this.evaluate(request).getResultCode();
		if (oAuthorizationResultCode.equals(AuthorizationResultCode.NOT_APPLICABLE)){
			return AuthorizationResultCode.PERMIT;
		}
		return oAuthorizationResultCode;
	}

}
