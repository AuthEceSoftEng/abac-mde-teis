package eu.fp7.scase.restreviews.utilities.authorization.attr;

import java.util.Objects;

public class BooleanAttribute extends AttributeValue{

	private boolean value;
	
	private final static String type;
	private final static BooleanAttribute falseInstance;
	private final static BooleanAttribute trueInstance;
	
	static{
		type = "Boolean";
        trueInstance = new BooleanAttribute(true);
        falseInstance = new BooleanAttribute(false);
	}
	
	private BooleanAttribute(boolean value){
		super();
		this.value= value;
	}
	
	public static BooleanAttribute getInstance(boolean value) {
		if(value) return trueInstance;
		return falseInstance;
	}
	
	public static BooleanAttribute getFalseInstance() {
		return falseInstance;
	}

	public static BooleanAttribute getTrueInstance() {
		return trueInstance;
	}
	
	/**
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}

	/**
	 * @return the type
	 */
	@Override
	public String getType() {
		return type;
	}
	
	@Override
    public boolean equals(Object o) {
        if (!(o instanceof BooleanAttribute))
            return false;

        BooleanAttribute other = (BooleanAttribute) o;

        return (value == other.value);
    }

	@Override
	public int hashCode(){
		return Objects.hash(value);
	}

}

