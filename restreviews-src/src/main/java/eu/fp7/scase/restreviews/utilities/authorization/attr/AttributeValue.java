package eu.fp7.scase.restreviews.utilities.authorization.attr;

public abstract class AttributeValue {
	public abstract String getType();
	public boolean isCollection(){
		return false;
	}
	public abstract Object getValue();
}

