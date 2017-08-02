package eu.fp7.scase.restreviews.utilities.authorization.attr;

import java.util.Objects;

public class DoubleAttribute extends AttributeValue {

	private double value;
	
	@Override
	public String getType() {
		return "Double";
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	public DoubleAttribute(double value){
		this.value = value;
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof DoubleAttribute))
            return false;

        DoubleAttribute other = (DoubleAttribute) o;

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

