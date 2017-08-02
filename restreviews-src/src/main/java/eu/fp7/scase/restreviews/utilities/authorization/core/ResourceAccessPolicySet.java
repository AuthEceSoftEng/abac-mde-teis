package eu.fp7.scase.restreviews.utilities.authorization.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import eu.fp7.scase.restreviews.utilities.authorization.combine.PolicyCombiningAlgorithm;
import eu.fp7.scase.restreviews.utilities.authorization.enums.CombiningAlgorithmEnum;


@Entity
@Table(name="policySet")
public class ResourceAccessPolicySet extends AbstractPolicy{

	@Id
	@GeneratedValue	@Column(name = "policySetId")
	private int policySetId;
	
	@Column(name = "auhtorizableResourceName", unique=true, nullable=true)
	private String auhtorizableResourceName;
	
	@Enumerated(EnumType.STRING)
	private CombiningAlgorithmEnum policyCombiningAlgorithmId;
	
	@Transient
	private PolicyCombiningAlgorithm policyCombiningAlgorithm;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="parentpolicySetId")
	@ForeignKey(name = "fk_policySet")
	private ResourceAccessPolicySet resourceAccessPolicySet;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="resourceAccessPolicySet",orphanRemoval=false)
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<ResourceAccessPolicySet> setOfResourceAccessPolicySet;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="resourceAccessPolicySet",orphanRemoval=true)
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<ResourceAccessPolicy> setOfResourceAccessPolicy;
	
	public ResourceAccessPolicySet(){
		
	}
	
	public ResourceAccessPolicySet (String auhtorizableResourceName,CombiningAlgorithmEnum policyCombiningAlgorithmId){
		this(auhtorizableResourceName,policyCombiningAlgorithmId,null);
	}
	
	public ResourceAccessPolicySet (CombiningAlgorithmEnum policyCombiningAlgorithmId,ResourceAccessPolicySet resourceAccessPolicySet){
		this(null,policyCombiningAlgorithmId,resourceAccessPolicySet);
	}
	
	private ResourceAccessPolicySet(String auhtorizableResourceName,
						CombiningAlgorithmEnum policyCombiningAlgorithmId,ResourceAccessPolicySet resourceAccessPolicySet)
	{
		this.setAuhtorizableResourceName(auhtorizableResourceName);
		this.setPolicyCombiningAlgorithmId(policyCombiningAlgorithmId);
		this.setResourceAccessPolicySet(resourceAccessPolicySet);
	}
	
	public AuthorizationResult evaluate(AbstractEvaluationContext evaluationContext){
		//System.out.println("Evaluating Policy Set: " + this.policySetId);
		List<AbstractPolicy> policies = new ArrayList<AbstractPolicy>();
		for (ResourceAccessPolicySet policySet : getSetOfResourceAccessPolicySet()){
			policies.add(policySet);
		}
		for (ResourceAccessPolicy policy : getSetOfResourceAccessPolicy()){
			policies.add(policy);
		}
		return this.getPolicyCombiningAlgorithm().combine(evaluationContext, policies);
	}


	/**
	 * @return the policySetId
	 */
	public int getPolicySetId() {
		return policySetId;
	}

	/**
	 * @param policySetId the policySetId to set
	 */
	public void setPolicySetId(int policySetId) {
		this.policySetId = policySetId;
	}

	/**
	 * @return the auhtorizableResourceName
	 */
	public String getAuhtorizableResourceName() {
		return auhtorizableResourceName;
	}

	/**
	 * @param auhtorizableResourceName the auhtorizableResourceName to set
	 */
	public void setAuhtorizableResourceName(String auhtorizableResourceName) {
		this.auhtorizableResourceName = auhtorizableResourceName;
	}

	/**
	 * @return the policyCombiningAlgorithmId
	 */
	public CombiningAlgorithmEnum getPolicyCombiningAlgorithmId() {
		return policyCombiningAlgorithmId;
	}

	/**
	 * @param policyCombiningAlgorithmId the policyCombiningAlgorithmId to set
	 */
	public void setPolicyCombiningAlgorithmId(CombiningAlgorithmEnum policyCombiningAlgorithmId) {
		this.policyCombiningAlgorithmId = policyCombiningAlgorithmId;
	}

	/**
	 * @return the policyCombiningAlgorithm
	 */
	public PolicyCombiningAlgorithm getPolicyCombiningAlgorithm() {
		if (policyCombiningAlgorithm == null){
			return PolicyCombiningAlgorithm.getInstance(getPolicyCombiningAlgorithmId());
		}
		return policyCombiningAlgorithm;
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
	 * @return the setOfResourceAccessPolicySet
	 */
	public Set<ResourceAccessPolicySet> getSetOfResourceAccessPolicySet() {
		if (setOfResourceAccessPolicySet == null){
			setOfResourceAccessPolicySet = new HashSet<ResourceAccessPolicySet>();
		}
		return setOfResourceAccessPolicySet;
	}

	/**
	 * @param setOfResourceAccessPolicySet the setOfResourceAccessPolicySet to set
	 */
	public void setSetOfResourceAccessPolicySet(Set<ResourceAccessPolicySet> setOfResourceAccessPolicySet) {
		this.setOfResourceAccessPolicySet = setOfResourceAccessPolicySet;
	}

	/**
	 * @return the setOfResourceAccessPolicy
	 */
	public Set<ResourceAccessPolicy> getSetOfResourceAccessPolicy() {
		if (setOfResourceAccessPolicy == null){
			setOfResourceAccessPolicy = new HashSet<ResourceAccessPolicy>();
		}
		return setOfResourceAccessPolicy;
	}

	/**
	 * @param setOfResourceAccessPolicy the setOfResourceAccessPolicy to set
	 */
	public void setSetOfResourceAccessPolicy(Set<ResourceAccessPolicy> setOfResourceAccessPolicy) {
		this.setOfResourceAccessPolicy = setOfResourceAccessPolicy;
	}

}

