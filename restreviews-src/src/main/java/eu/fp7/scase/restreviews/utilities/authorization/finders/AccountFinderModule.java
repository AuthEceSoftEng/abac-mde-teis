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
import eu.fp7.scase.restreviews.account.JavaaccountModel;
import eu.fp7.scase.restreviews.product.JavaproductModel;
import eu.fp7.scase.restreviews.order.JavaorderModel;
import eu.fp7.scase.restreviews.review.JavareviewModel;
import eu.fp7.scase.restreviews.utilities.HibernateController;

public class AccountFinderModule extends AttributeFinderModule{
	
	@Override
	public Set<String> getSupportedType() {
		Set<String> types = new HashSet<String>();
		types.add("account");
		return types;
	}

	@Override
	public Set<AttributeCategory> getSupportedCategory() {
		Set<AttributeCategory> categories = new HashSet<AttributeCategory>();
		categories.add(AttributeCategory.ACCESSED_RESOURCE);
		categories.add(AttributeCategory.INCLUDED_RESOURCE);
		categories.add(AttributeCategory.ACCESS_SUBJECT);
		categories.add(AttributeCategory.PARENT_RESOURCE);
		return categories;
	}
	
	@Override
	public EvaluationResult findAttribute(ResourceAccessAttribute attribute, AbstractEvaluationContext evaluationContext) {

		AzRequest<JavaaccountModel> request = (AzRequest<JavaaccountModel>) evaluationContext.getRequest();
		JavaaccountModel oJavaaccountModel = null;

		if(attribute.getAttributeCategory().equals(AttributeCategory.PARENT_RESOURCE)){
			String parentResourceType;

			parentResourceType = request.getAction().equals(Action.POST) ? request.getPostedResourceParentType() : request.getAccessedResourceType();
			if (parentResourceType == null){
				return new EvaluationResult("No parent type found.");
			}

			if (parentResourceType.equalsIgnoreCase("account")){
				try{
					oJavaaccountModel = getAccount(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
			}


			if(parentResourceType.equalsIgnoreCase("product")){
				try{
					oJavaaccountModel =  getProductParentAccount(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
			}
			if(parentResourceType.equalsIgnoreCase("order")){
				try{
					oJavaaccountModel =  getOrderParentAccount(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
			}
			if(parentResourceType.equalsIgnoreCase("review")){
				try{
					oJavaaccountModel =  getReviewParentAccount(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
			}

			if (oJavaaccountModel == null){
				return new EvaluationResult("Parent resource not found");
			}
		}


		if(attribute.getAttributeCategory().equals(AttributeCategory.ACCESSED_RESOURCE)){
			if (!(request.getResourceId() == null)){
				try{
					oJavaaccountModel = getAccount(new Integer(request.getResourceId()));
				}catch(NumberFormatException e){
					return new EvaluationResult("Syntax error : Not valid id parameter");
				}
				if (oJavaaccountModel == null){
					return new EvaluationResult("Accessed resource not found");
				}
			}
		}
		if(attribute.getAttributeCategory().equals(AttributeCategory.INCLUDED_RESOURCE)){
			oJavaaccountModel = (JavaaccountModel)request.getIncludedResource();
			if (oJavaaccountModel  == null){
				return new EvaluationResult("Included resource not included in request");
			}
		}
		if(attribute.getAttributeCategory().equals(AttributeCategory.ACCESS_SUBJECT)){
			oJavaaccountModel = request.getAccessSubject();
			if (oJavaaccountModel == null){
				return new EvaluationResult("Access subject not included in request");
			}
		}
		if (oJavaaccountModel == null){
			return new EvaluationResult("Unsupported Category");
		}

		AttributeValue resultValue = getProperty(attribute, oJavaaccountModel);
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

	private AttributeValue getProperty(ResourceAccessAttribute attribute, JavaaccountModel oJavaaccountModel){
		if(attribute.getPropertyName().equalsIgnoreCase("email")){
			return new StringAttribute(oJavaaccountModel.getemail());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("password")){
			return new StringAttribute(oJavaaccountModel.getpassword());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("role")){
			return new StringAttribute(oJavaaccountModel.getrole());
		}
		if(attribute.getPropertyName().equalsIgnoreCase("accountId")){
			return new IntegerAttribute(oJavaaccountModel.getaccountId());
		}
		return null;
	}

	private JavaaccountModel getAccount(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaaccountModel oJavaaccountModel = new JavaaccountModel();
		oJavaaccountModel.setaccountId(id);
		return oHibernateController.getaccount(oJavaaccountModel);	
	}

	private JavaaccountModel getProductParentAccount(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaproductModel oJavaproductModel = new JavaproductModel();
		oJavaproductModel.setproductId(id);
		oJavaproductModel = oHibernateController.getproduct(oJavaproductModel);

		JavaaccountModel oJavaaccountModel = new JavaaccountModel();
		oJavaaccountModel.setaccountId(oJavaproductModel.getaccountId());
		oJavaaccountModel = oHibernateController.getaccount(oJavaaccountModel);
		return oJavaaccountModel;
	}
	private JavaaccountModel getOrderParentAccount(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavaorderModel oJavaorderModel = new JavaorderModel();
		oJavaorderModel.setorderId(id);
		oJavaorderModel = oHibernateController.getorder(oJavaorderModel);

		JavaaccountModel oJavaaccountModel = new JavaaccountModel();
		oJavaaccountModel.setaccountId(oJavaorderModel.getaccountId());
		oJavaaccountModel = oHibernateController.getaccount(oJavaaccountModel);
		return oJavaaccountModel;
	}
	private JavaaccountModel getReviewParentAccount(Integer id) {
		HibernateController oHibernateController = HibernateController.getHibernateControllerHandle();
		JavareviewModel oJavareviewModel = new JavareviewModel();
		oJavareviewModel.setreviewId(id);
		oJavareviewModel = oHibernateController.getreview(oJavareviewModel);

		return oJavareviewModel.getaccount();
	}
}

