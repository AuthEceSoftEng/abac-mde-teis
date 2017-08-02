package eu.fp7.scase.restreviews.utilities.authorization.finders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.fp7.scase.restreviews.utilities.authorization.attr.AttributeValue;
import eu.fp7.scase.restreviews.utilities.authorization.attr.IntegerAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.attr.DoubleAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.attr.ListOfAttributes;
import eu.fp7.scase.restreviews.utilities.authorization.attr.StringAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AttributeCategory;
import eu.fp7.scase.restreviews.utilities.authorization.operators.EvaluationResult;

public class ConstantsModule extends AttributeFinderModule {
	@Override
	public Set<String> getSupportedType() {
		Set<String> types = new HashSet<String>();
		types.add("String");
		types.add("Integer");
		types.add("Double");
		return types;
	}

	@Override
	public Set<AttributeCategory> getSupportedCategory() {
		Set<AttributeCategory> categories = new HashSet<AttributeCategory>();
		categories.add(AttributeCategory.CONSTANT);
		return categories;
	}
	
	@Override
	public EvaluationResult findAttribute(ResourceAccessAttribute attribute, AbstractEvaluationContext evaluationContext) {
		if (!this.getSupportedCategory().contains(attribute.getAttributeCategory())){
			return new EvaluationResult("Unsuported Category");
		}
		List<AttributeValue> values = new ArrayList<AttributeValue>();
		String type = attribute.getResourceType();
		for (String value: attribute.getValue()){
			if (!getSupportedType().contains(type)){
				return new EvaluationResult("Unsupported value type");
			}
			if(type.equalsIgnoreCase("String")){
				values.add(new StringAttribute(value));
			}
			if(type.equalsIgnoreCase("Integer")){
				int intValue ;
				try{
					intValue = Integer.parseInt(value);
				}
				catch(NumberFormatException e){
					return new EvaluationResult("Syntex error : argument is not a proper numeric representation");
				}
				values.add(new IntegerAttribute(intValue));
			}
			if(type.equalsIgnoreCase("Double")){
				double doubleValue ;
				try{
					doubleValue = Double.parseDouble(value);
				}
				catch(NumberFormatException e){
					return new EvaluationResult("Syntex error : argument is not a proper numeric representation");
				}
				values.add(new DoubleAttribute(doubleValue));
			}
		}
		if (values.isEmpty()){
			return new EvaluationResult("Attribute has no values");
		}
		if (values.size() == 1){
			return new EvaluationResult(values.get(0));
		}
		return new EvaluationResult(new ListOfAttributes(values));
	}
}

