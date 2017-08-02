package eu.fp7.scase.restreviews.utilities.authorization.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.fp7.scase.restreviews.utilities.authorization.enums.AuthorizationResultCode;

public class AuthorizationResult {

	private AuthorizationResultCode resultCode;
	private List<String> errorMessages;
	
	public AuthorizationResult(AuthorizationResultCode resultCode, List<String> errorMessages){
		this.resultCode = resultCode;
		this.setErrorMessages(errorMessages);
	}
	
	public AuthorizationResult(AuthorizationResultCode resultCode, String error){
		this(resultCode,new ArrayList<String>(Arrays.asList(error)));
	}
	
	public AuthorizationResult(AuthorizationResultCode resultCode){
		this(resultCode,"");
	}
	
	public AuthorizationResultCode getResultCode() {
		return resultCode;
	}

	/**
	 * @return the error
	 */
	public List<String> getErrorMessages() {
		if (errorMessages == null){
			errorMessages = new ArrayList<String>();
		}
		return errorMessages;
	}

	/**
	 * @param errorMessages the errorMessages to set
	 */
	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	public void addErrorMessage(String errorMessage){
		this.errorMessages.add(errorMessage);
	}
}

