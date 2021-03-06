package eu.fp7.scase.restreviews.utilities.authorization.attr;

import java.util.Objects;

public class IntegerAttribute extends AttributeValue{

	private int value;
	
	@Override
	public String getType() {
		return "Integer";
	}

	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}

	public IntegerAttribute(int value){
		this.value = value;
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof IntegerAttribute))
            return false;

        IntegerAttribute other = (IntegerAttribute) o;

        return (value == other.value);
    }
    
	@Override
	public int hashCode(){
		return Objects.hash(value);
	}

    public String toString(){
		return String.valueOf(value);  	
    }
	
}

