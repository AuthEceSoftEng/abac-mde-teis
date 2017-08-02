package eu.fp7.scase.restreviews.utilities.authorization.attr;

public class StringAttribute extends AttributeValue {

	private String value;
	
	@Override
	public String getType() {
		return "String";
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	public StringAttribute(String value){
		this.value = value;
	}
	
    public boolean equals(Object o) {
        if (!(o instanceof StringAttribute))
            return false;

        StringAttribute other = (StringAttribute) o;
        return (value.equals(other.value));
    }
}

