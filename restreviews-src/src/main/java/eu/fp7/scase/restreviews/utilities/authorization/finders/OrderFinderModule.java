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
import eu.fp7.scase.restreviews.order.JavaorderModel;
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.utilities.HibernateController;

public class OrderFinderModule extends AttributeFinderModule{
	
	@Override
	public Set<String> getSupportedType() {
		Set<String> types = new HashSet<String>();
		types.add("order");
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
		JavaorderModel oJavaorderModel = null;

		if(attribute.getAttributeCategory().equals(AttributeCategory.PARENT_RESOURCE)){
			String parentResourceType;

			parentResourceType = request.getAction().equals(Action.POST) ? request.getPostedResourceParentType() : request.getAccessedResourceType();
			if (parentResourceType == null){
				return new EvaluationResult("No parent type found.");
			}

			if (parentResourceType.equalsIgnoreCase("order")){
				try{
					oJavaorderModel = getOrder(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
			}


			if(parentResourceType.equalsIgnoreCase("product")){
				try{
					Set<JavaorderModel> setOfJavaorderModel =  getProductParentOrder(new Integer(request.getResourceId()));
                    Set<AttributeValue> oSetOfAttributes = new HashSet<AttributeValue>();
                    Iterator<JavaorderModel> i = setOfJavaorderModel.iterator();
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

			if (oJavaorderModel == null){
				return new EvaluationResult("Parent resource not found");
			}
		}

		if(attribute.getAttributeCategory().equals(AttributeCategory.CHILD_RESOURCE)){
			Set<JavaorderModel> setOfJavaorderModel = null;
			if (request.getAccessedResourceType().equalsIgnoreCase("account")){
				setOfJavaorderModel = getAccountChildrenOrder(new Integer(request.getResourceId()));
			}
			if (setOfJavaorderModel == null){
				return new EvaluationResult("Could not get the children resources");
			}
			Iterator<JavaorderModel> i = setOfJavaorderModel.iterator();
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
					oJavaorderModel = getOrder(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
				if (oJavaorderModel == null){
					return new EvaluationResult("Accessed resource not found");
				}
			}
		}
		if(attribute.getAttributeCategory().equals(AttributeCategory.INCLUDED_RESOURCE)){
			oJavaorderModel = (JavaorderModel)request.getIncludedResource();
			if (oJavaorderModel  == null){
				return new EvaluationResult("Included resource not included in request");
			}
		}
		if (oJavaorderModel == null){
			return new EvaluationResult("Unsupported Category");
		}

		AttributeValue resultValue = getProperty(attribute, oJavaorderModel);
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

	private AttributeValue getProperty(ResourceAccessAttribute attribute, JavaorderModel oJavaorderModel){
		if(attribute.getPropertyName().equalsIgnoreCase("discountCoupon")){
			return new StringAttribute(oJavaorderModel.getdiscountCoupon());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("orderId")){
			return new IntegerAttribute(oJavaorderModel.getorderId());
		}
		return null;
	}

	private JavaorderModel getOrder(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaorderModel oJavaorderModel = new JavaorderModel();
		oJavaorderModel.setorderId(id);
		return oHibernateController.getorder(oJavaorderModel);	
	}

	private Set<JavaorderModel> getProductParentOrder(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaproductModel oJavaproductModel = new JavaproductModel();
		oJavaproductModel.setproductId(id);
		oJavaproductModel = oHibernateController.getproduct(oJavaproductModel);

		return oJavaproductModel.getSetOfParentJavaorderModel();
	}
	private Set<JavaorderModel> getAccountChildrenOrder(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaaccountModel oJavaaccountModel = new JavaaccountModel();
		oJavaaccountModel.setaccountId(id);
		oJavaaccountModel = oHibernateController.getaccount(oJavaaccountModel);
		return oJavaaccountModel.getSetOfJavaorderModel();	
	}

}

