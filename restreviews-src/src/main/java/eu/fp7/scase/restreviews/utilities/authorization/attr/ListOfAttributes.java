package eu.fp7.scase.restreviews.utilities.authorization.attr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfAttributes extends CollectionOfAttributes{

	private List<AttributeValue> values;
	
	@Override
	public String getType() {
		return values.iterator().next().getType();
	}
	
	@Override
	public String getCollectionType() {
		return "List";
	}
	
	/**
	 * @return a copy of the values
	 */
	@Override
	public List<AttributeValue> getValue() {
		if (values == null){
			values = new ArrayList<AttributeValue>();
		}
		return values;
	}

	public ListOfAttributes(AttributeValue[] values){
		this.values = new ArrayList<AttributeValue>(Arrays.asList(values));
	}
	
	public ListOfAttributes(List<AttributeValue> values){
		this.values = values;
	}
	
    public boolean equals(Object o) {
        if (!(o instanceof ListOfAttributes))
            return false;

        ListOfAttributes other = (ListOfAttributes) o;

        if(other.getValue().size() != this.getValue().size()){
        	return false;
        }
        
        for (int i = 0 ; i < this.getValue().size() ; i++){
        	if (!this.getValue().get(i).equals(other.getValue().get(i))){
        		return false;
        	}
        }
        return true;
    }

}

