package eu.fp7.scase.restreviews.utilities.authorization.finders;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessPolicySet;
import eu.fp7.scase.restreviews.utilities.HibernateController;

public class PolicyFinder {
	
	HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
	
	public List<ResourceAccessPolicySet> findPolicy(String accessedResourceType){		
		return oHibernateController.getPolicySetByResource(accessedResourceType);
	}
}
