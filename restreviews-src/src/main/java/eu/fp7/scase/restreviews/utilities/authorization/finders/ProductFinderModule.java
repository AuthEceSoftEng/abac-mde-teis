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
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.review.JavareviewModel;
import eu.fp7.scase.restreviews.order.JavaorderModel;
import eu.fp7.scase.restreviews.utilities.HibernateController;

public class ProductFinderModule extends AttributeFinderModule{
	
	@Override
	public Set<String> getSupportedType() {
		Set<String> types = new HashSet<String>();
		types.add("product");
		return types;
	}

	@Override
	public Set<AttributeCategory> getSupportedCategory() {
		Set<AttributeCategory> categories = new HashSet<AttributeCategory>();
		categories.add(AttributeCategory.ACCESSED_RESOURCE);
		categories.add(AttributeCategory.INCLUDED_RESOURCE);
		categories.add(AttributeCategory.PARENT_RESOURCE);
		categories.add(AttributeCategory.CHILD_RESOURCE);
		return categories;
	}
	
	@Override
	public EvaluationResult findAttribute(ResourceAccessAttribute attribute, AbstractEvaluationContext evaluationContext) {

		AzRequest<JavaaccountModel> request = (AzRequest<JavaaccountModel>) evaluationContext.getRequest();
		JavaproductModel oJavaproductModel = null;

		if(attribute.getAttributeCategory().equals(AttributeCategory.PARENT_RESOURCE)){
			String parentResourceType;

			parentResourceType = request.getAction().equals(Action.POST) ? request.getPostedResourceParentType() : request.getAccessedResourceType();
			if (parentResourceType == null){
				return new EvaluationResult("No parent type found.");
			}

			if (parentResourceType.equalsIgnoreCase("product")){
				try{
					oJavaproductModel = getProduct(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
			}


			if(parentResourceType.equalsIgnoreCase("review")){
				try{
					Set<JavaproductModel> setOfJavaproductModel =  getReviewParentProduct(new Integer(request.getResourceId()));
                    Set<AttributeValue> oSetOfAttributes = new HashSet<AttributeValue>();
                    Iterator<JavaproductModel> i = setOfJavaproductModel.iterator();
                    while(i.hasNext()){
                        AttributeValue oAttributeValue = getProperty(attribute, i.next());
                        if(oAttributeValue == null){
                            return new EvaluationResult("Syntax error : Property name is not a valid string");
                        }
                        oSetOfAttributes.add(oAttributeValue);
                    }
                    
                    return new EvaluationResult(new SetOfAttributes(oSetOfAttributes));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
			}

			if (oJavaproductModel == null){
				return new EvaluationResult("Parent resource not found");
			}
		}

		if(attribute.getAttributeCategory().equals(AttributeCategory.CHILD_RESOURCE)){
			Set<JavaproductModel> setOfJavaproductModel = null;
			if (request.getAccessedResourceType().equalsIgnoreCase("account")){
				setOfJavaproductModel = getAccountChildrenProduct(new Integer(request.getResourceId()));
			}
			if (request.getAccessedResourceType().equalsIgnoreCase("order")){
				setOfJavaproductModel = getOrderChildrenProduct(new Integer(request.getResourceId()));
			}
			if (setOfJavaproductModel == null){
				return new EvaluationResult("Could not get the children resources");
			}
			Iterator<JavaproductModel> i = setOfJavaproductModel.iterator();
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
					oJavaproductModel = getProduct(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
				if (oJavaproductModel == null){
					return new EvaluationResult("Accessed resource not found");
				}
			}
		}
		if(attribute.getAttributeCategory().equals(AttributeCategory.INCLUDED_RESOURCE)){
			oJavaproductModel = (JavaproductModel)request.getIncludedResource();
			if (oJavaproductModel  == null){
				return new EvaluationResult("Included resource not included in request");
			}
		}
		if (oJavaproductModel == null){
			return new EvaluationResult("Unsupported Category");
		}

		AttributeValue resultValue = getProperty(attribute, oJavaproductModel);
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

	private AttributeValue getProperty(ResourceAccessAttribute attribute, JavaproductModel oJavaproductModel){
		if(attribute.getPropertyName().equalsIgnoreCase("title")){
			return new StringAttribute(oJavaproductModel.gettitle());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("description")){
			return new StringAttribute(oJavaproductModel.getdescription());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("status")){
			return new StringAttribute(oJavaproductModel.getstatus());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("productId")){
			return new IntegerAttribute(oJavaproductModel.getproductId());
		}
		return null;
	}

	private JavaproductModel getProduct(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaproductModel oJavaproductModel = new JavaproductModel();
		oJavaproductModel.setproductId(id);
		return oHibernateController.getproduct(oJavaproductModel);	
	}

	private Set<JavaproductModel> getReviewParentProduct(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavareviewModel oJavareviewModel = new JavareviewModel();
		oJavareviewModel.setreviewId(id);
		oJavareviewModel = oHibernateController.getreview(oJavareviewModel);

		return oJavareviewModel.getSetOfParentJavaproductModel();
	}
	private Set<JavaproductModel> getAccountChildrenProduct(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaaccountModel oJavaaccountModel = new JavaaccountModel();
		oJavaaccountModel.setaccountId(id);
		oJavaaccountModel = oHibernateController.getaccount(oJavaaccountModel);
		return oJavaaccountModel.getSetOfJavaproductModel();	
	}

	private Set<JavaproductModel> getOrderChildrenProduct(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaorderModel oJavaorderModel = new JavaorderModel();
		oJavaorderModel.setorderId(id);
		oJavaorderModel = oHibernateController.getorder(oJavaorderModel);
		return oJavaorderModel.getSetOfJavaproductModel();	
	}

}

