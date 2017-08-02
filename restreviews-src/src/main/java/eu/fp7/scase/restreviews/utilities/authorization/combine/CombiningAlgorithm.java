package eu.fp7.scase.restreviews.utilities.authorization.combine;

import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AuthorizationResult;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;

public abstract class CombiningAlgorithm {

	protected CombiningAlgorithm(CombiningAlgorithmEnum id){
		this.combiningAlgorithmId = id;
	}
	
	private CombiningAlgorithmEnum combiningAlgorithmId;
	
	public abstract AuthorizationResult combine(AbstractEvaluationContext evaluationContext, List<?> elements);
	
	public CombiningAlgorithmEnum getCombiningAlgorithmId(){
		return this.combiningAlgorithmId;
	}
	
	public boolean isPolicyCombiningAlgorithm(){
		return false;
	}
	public boolean isRuleCombiningAlgorithm(){
		return false;
	}
	
}
