package eu.fp7.scase.restreviews.utilities.authorization.core;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import eu.fp7.scase.restreviews.utilities.authorization.enums.AttributeCategory;
import eu.fp7.scase.restreviews.utilities.authorization.operators.EvaluationResult;

@Embeddable
public class ResourceAccessAttribute {

	@Enumerated(EnumType.STRING)
	private AttributeCategory attributeCategory;

	private String resourceType;
	
	private String propertyName;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> value;

	public ResourceAccessAttribute(){
		
	}
	
	//Constructor for the creation of a resource attribute
	public ResourceAccessAttribute(AttributeCategory attributeCategory, String resourceType, String propertyName){
		if (attributeCategory.equals(AttributeCategory.CONSTANT)) throw new IllegalArgumentException("Resource attributes can't be of category CONSTANT.");
		this.setAttributeCategory(attributeCategory);
		this.setResourceType(resourceType);
		this.setPropertyName(propertyName);
	}
	
	//Constructor for the creating of a multi constant value attribute
	public ResourceAccessAttribute(List<String> value,String type){
		this((String[]) value.toArray(),type);
	}
	
	//Constructor for the creating of a multi constant value attribute
	public ResourceAccessAttribute(String[] value,String type){
		this.setAttributeCategory(AttributeCategory.CONSTANT);
		this.setResourceType(type);
		for (int i = 0 ; i < value.length ;i++){
			this.getValue().add(value[i]);
		}
	}
	
	//Constructor for the creation of a single constant value attribute
	public ResourceAccessAttribute(String value,String type){
		this.setAttributeCategory(AttributeCategory.CONSTANT);
		this.setResourceType(type);
		this.getValue().add(value);
	}
	
	public EvaluationResult resolve(AbstractEvaluationContext evaluationContext) {
		return evaluationContext.getAttributeFinder().findAttribute(this, evaluationContext);
	}
	
	/**
	 * @return the attributeCategory
	 */
	public AttributeCategory getAttributeCategory() {
		return attributeCategory;
	}

	/**
	 * @param attributeCategory the attributeCategory to set
	 */
	public void setAttributeCategory(AttributeCategory attributeCategory) {
		this.attributeCategory = attributeCategory;
	}

	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @return the value
	 */

	public List<String> getValue() {
		if (value == null){
			value = new ArrayList<String>();
		}
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(List<String> value) {
		this.value = value;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	@Override
	public String toString(){
		return "{Category:" + attributeCategory + " Type:" + resourceType + " PropertyName:" + propertyName + "}";
	}
}

