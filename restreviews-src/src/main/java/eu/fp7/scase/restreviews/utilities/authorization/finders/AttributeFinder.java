package eu.fp7.scase.restreviews.utilities.authorization.finders;

import java.util.HashSet;
import java.util.Set;
import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.operators.EvaluationResult;

public class AttributeFinder{
	
	private Set<AttributeFinderModule> attributeFinderModules;
	
	public AttributeFinder(){
		attributeFinderModules = new HashSet<AttributeFinderModule>();
		attributeFinderModules.add(new ConstantsModule());
		attributeFinderModules.add(new AccountFinderModule());
		attributeFinderModules.add(new ProductFinderModule());
		attributeFinderModules.add(new OrderFinderModule());
		attributeFinderModules.add(new ReviewFinderModule());
		
		
	}
	
	public EvaluationResult findAttribute(ResourceAccessAttribute attribute, AbstractEvaluationContext evaluationContext){
		//System.out.println("Resolving attribute : " + attribute.toString());
		for (AttributeFinderModule module : attributeFinderModules){
			if (module.getSupportedCategory() != null && module.getSupportedType() != null){
				if (module.getSupportedCategory().contains(attribute.getAttributeCategory()) && module.getSupportedType().contains(attribute.getResourceType())){
					return module.findAttribute(attribute, evaluationContext);
				}	
			}
		}		
		return new EvaluationResult("No attributeFinderModule was found for attribute : " + attribute.toString());
	}
}

