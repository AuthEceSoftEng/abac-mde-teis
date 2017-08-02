package eu.fp7.scase.restreviews.utilities.authorization.attr;

import java.util.Collection;

public abstract class CollectionOfAttributes extends AttributeValue {

	@Override
	public boolean isCollection(){
		return true;
	}

	public abstract String getCollectionType();
	@Override
	public abstract Collection<AttributeValue> getValue();
}

