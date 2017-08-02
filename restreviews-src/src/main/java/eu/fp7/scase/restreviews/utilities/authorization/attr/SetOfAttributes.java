package eu.fp7.scase.restreviews.utilities.authorization.attr;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import java.util.Iterator;

public class SetOfAttributes extends CollectionOfAttributes{

	private Set<AttributeValue> values;
	
	@Override
	public String getType() {
		return values.iterator().next().getType();
	}
	
	@Override
	public String getCollectionType() {
		return "Set";
	}
	
	/**
	 * @return a copy of the values
	 */
	@Override
	public Set<AttributeValue> getValue() {
		if (values == null){
			values = new HashSet<AttributeValue>();
		}
		return values;
	}

	public SetOfAttributes(AttributeValue[] values){
		this.values = new HashSet<AttributeValue>(Arrays.asList(values));
	}
	
	public SetOfAttributes(Set<AttributeValue> values){
		this.values = values;
	}
	
    public boolean equals(Object o) {
        if (!(o instanceof SetOfAttributes))
            return false;

        SetOfAttributes other = (SetOfAttributes) o;

        if(other.getValue().size() != this.getValue().size()){
        	return false;
        }
        
	    Iterator<AttributeValue> i = this.getValue().iterator();
	    while (i.hasNext()){
	    	if (!other.getValue().contains(i.next())){
	    		return false;
	    	}
	    }
        return true;
    }

}

