package eu.fp7.scase.restreviews.utilities.authorization.finders;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

import eu.fp7.scase.restreviews.utilities.authorization.attr.* ;
import eu.fp7.scase.restreviews.utilities.authorization.core.AbstractEvaluationContext;
import eu.fp7.scase.restreviews.utilities.authorization.core.AzRequest;
import eu.fp7.scase.restreviews.utilities.authorization.enums.Action;
import eu.fp7.scase.restreviews.utilities.authorization.core.ResourceAccessAttribute;
import eu.fp7.scase.restreviews.utilities.authorization.enums.AttributeCategory;
import eu.fp7.scase.restreviews.utilities.authorization.operators.EvaluationResult;
import eu.fp7.scase.restreviews.review.JavareviewModel;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.utilities.HibernateController;

public class ReviewFinderModule extends AttributeFinderModule{
	
	@Override
	public Set<String> getSupportedType() {
		Set<String> types = new HashSet<String>();
		types.add("review");
		return types;
	}

	@Override
	public Set<AttributeCategory> getSupportedCategory() {
		Set<AttributeCategory> categories = new HashSet<AttributeCategory>();
		categories.add(AttributeCategory.ACCESSED_RESOURCE);
		categories.add(AttributeCategory.INCLUDED_RESOURCE);
		categories.add(AttributeCategory.CHILD_RESOURCE);
		return categories;
	}
	
	@Override
	public EvaluationResult findAttribute(ResourceAccessAttribute attribute, AbstractEvaluationContext evaluationContext) {

		AzRequest<JavaaccountModel> request = (AzRequest<JavaaccountModel>) evaluationContext.getRequest();
		JavareviewModel oJavareviewModel = null;


		if(attribute.getAttributeCategory().equals(AttributeCategory.CHILD_RESOURCE)){
			Set<JavareviewModel> setOfJavareviewModel = null;
			if (request.getAccessedResourceType().equalsIgnoreCase("product")){
				setOfJavareviewModel = getProductChildrenReview(new Integer(request.getResourceId()));
			}
			if (setOfJavareviewModel == null){
				return new EvaluationResult("Could not get the children resources");
			}
			Iterator<JavareviewModel> i = setOfJavareviewModel.iterator();
			Set<AttributeValue> oSetOfAttributes = new HashSet<AttributeValue>();
			while (i.hasNext()){
				AttributeValue oAttributeValue = getProperty(attribute, i.next());
				if (oAttributeValue == null){
					return new EvaluationResult("Syntax error : Property name is not a valid string");
				}
				oSetOfAttributes.add(oAttributeValue);
			}
			return new EvaluationResult(new SetOfAttributes(oSetOfAttributes));	
		}		

		if(attribute.getAttributeCategory().equals(AttributeCategory.ACCESSED_RESOURCE)){
			if (!(request.getResourceId() == null)){
				try{
					oJavareviewModel = getReview(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
				if (oJavareviewModel == null){
					return new EvaluationResult("Accessed resource not found");
				}
			}
		}
		if(attribute.getAttributeCategory().equals(AttributeCategory.INCLUDED_RESOURCE)){
			oJavareviewModel = (JavareviewModel)request.getIncludedResource();
			if (oJavareviewModel  == null){
				return new EvaluationResult("Included resource not included in request");
			}
		}
		if (oJavareviewModel == null){
			return new EvaluationResult("Unsupported Category");
		}

		AttributeValue resultValue = getProperty(attribute, oJavareviewModel);
		if(resultValue != null){
			if (resultValue.getValue() != null){
				return new EvaluationResult(resultValue);
			}else{
				return new EvaluationResult("Property is null.");	
			}
		}else{
			return new EvaluationResult("Syntax error : Property name is not a valid string");
		}

	}

	private AttributeValue getProperty(ResourceAccessAttribute attribute, JavareviewModel oJavareviewModel){
		if(attribute.getPropertyName().equalsIgnoreCase("title")){
			return new StringAttribute(oJavareviewModel.gettitle());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("description")){
			return new StringAttribute(oJavareviewModel.getdescription());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("reviewId")){
			return new IntegerAttribute(oJavareviewModel.getreviewId());
		}
		return null;
	}

	private JavareviewModel getReview(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavareviewModel oJavareviewModel = new JavareviewModel();
		oJavareviewModel.setreviewId(id);
		return oHibernateController.getreview(oJavareviewModel);	
	}

	private Set<JavareviewModel> getProductChildrenReview(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaproductModel oJavaproductModel = new JavaproductModel();
		oJavaproductModel.setproductId(id);
		oJavaproductModel = oHibernateController.getproduct(oJavaproductModel);
		return oJavaproductModel.getSetOfJavareviewModel();	
	}

}

