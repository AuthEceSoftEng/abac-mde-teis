package eu.fp7.scase.restreviews.utilities.authorization.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import eu.fp7.scase.restreviews.utilities.authorization.attr.BooleanAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.combine.RuleCombiningAlgorithm;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;
import eu.fp7.scase.restreviews.utilities.authorization.operators.EvaluationResult;

@Entity
@Table(name="policy")
public class ResourceAccessPolicy extends AbstractPolicy{

	@Id
	@GeneratedValue	@Column(name = "policyId")
	private int policyId;
	
	@Enumerated(EnumType.STRING)
	private CombiningAlgorithmEnum ruleCombiningAlgorithmId;
	
	@Transient
	private RuleCombiningAlgorithm ruleCombiningAlgorithm;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="parentpolicySetId")
	@ForeignKey(name = "fk_policy_policySet")
	private ResourceAccessPolicySet resourceAccessPolicySet;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="resourceAccessPolicy",orphanRemoval=true)
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<ResourceAccessRule> setOfResourceAccessRule;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="resourceAccessPolicy",orphanRemoval=true)
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<ResourceAccessCondition> setOfApplyCondition;
	
	public ResourceAccessPolicy(){
		
	}
	
	public ResourceAccessPolicy(CombiningAlgorithmEnum ruleCombiningAlgorithmId,ResourceAccessPolicySet resourceAccessPolicySet){
		this.setRuleCombiningAlgorithmId(ruleCombiningAlgorithmId);
		this.setResourceAccessPolicySet(resourceAccessPolicySet);
	}
	
	public AuthorizationResult evaluate(AbstractEvaluationContext evaluationContext){
		//System.out.println("Evaluating Policy : " + this.getPolicyId());
		for (ResourceAccessCondition applyCondition : getSetOfApplyCondition()){
			EvaluationResult result = applyCondition.evaluate(evaluationContext);
			if (result.getAttributeValue() == null){
				if (result.getErrorStatus().equals("Access subject not included in request")){
					return new AuthorizationResult(AuthorizationResultCode.NOT_APPLICABLE,result.getErrorStatus());
				}
				return new AuthorizationResult(AuthorizationResultCode.INDETERMINATE,result.getErrorStatus());
			}
			if (result.equals(BooleanAttribute.getFalseInstance())){
				return new AuthorizationResult(AuthorizationResultCode.NOT_APPLICABLE);
			}
		}
		return this.getRuleCombiningAlgorithm().combine(evaluationContext, new ArrayList<ResourceAccessRule>(getSetOfResourceAccessRule()));
	}
	
	/**
	 * @return the policyCombiningAlgorithm
	 */
	public RuleCombiningAlgorithm getRuleCombiningAlgorithm() {
		if (ruleCombiningAlgorithm == null){
			return RuleCombiningAlgorithm.getInstance(getRuleCombiningAlgorithmId());
		}
		return ruleCombiningAlgorithm;
	}

	/**
	 * @return the policyId
	 */
	public int getPolicyId() {
		return policyId;
	}

	/**
	 * @param policyId the policyId to set
	 */
	public void setPolicyId(int policyId) {
		this.policyId = policyId;
	}

	/**
	 * @return the ruleCombiningAlgorithmId
	 */
	public CombiningAlgorithmEnum getRuleCombiningAlgorithmId() {
		return ruleCombiningAlgorithmId;
	}

	/**
	 * @param ruleCombiningAlgorithmId the ruleCombiningAlgorithmId to set
	 */
	public void setRuleCombiningAlgorithmId(CombiningAlgorithmEnum ruleCombiningAlgorithmId) {
		this.ruleCombiningAlgorithmId = ruleCombiningAlgorithmId;
	}

	/**
	 * @return the resourceAccessPolicySet
	 */
	public ResourceAccessPolicySet getResourceAccessPolicySet() {
		return resourceAccessPolicySet;
	}

	/**
	 * @param resourceAccessPolicySet the resourceAccessPolicySet to set
	 */
	public void setResourceAccessPolicySet(ResourceAccessPolicySet resourceAccessPolicySet) {
		this.resourceAccessPolicySet = resourceAccessPolicySet;
	}

	/**
	 * @return the setOfResourceAccessRule
	 */
	public Set<ResourceAccessRule> getSetOfResourceAccessRule() {
		if (setOfResourceAccessRule == null){
			this.setSetOfResourceAccessRule(new HashSet<ResourceAccessRule>());
		}
		return setOfResourceAccessRule;
	}

	/**
	 * @param setOfResourceAccessRule the setOfResourceAccessRule to set
	 */
	public void setSetOfResourceAccessRule(Set<ResourceAccessRule> setOfResourceAccessRule) {
		this.setOfResourceAccessRule = setOfResourceAccessRule;
	}

	/**
	 * @return the setOfApplyCondition
	 */
	public Set<ResourceAccessCondition> getSetOfApplyCondition() {
		if (setOfApplyCondition == null){
			this.setSetOfApplyCondition(new HashSet<ResourceAccessCondition>());
		}
		return setOfApplyCondition;
	}

	/**
	 * @param setOfApplyCondition the setOfApplyCondition to set
	 */
	public void setSetOfApplyCondition(Set<ResourceAccessCondition> setOfApplyCondition) {
		this.setOfApplyCondition = setOfApplyCondition;
	}
}

