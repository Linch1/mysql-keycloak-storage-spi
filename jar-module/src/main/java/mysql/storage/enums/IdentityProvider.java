package mysql.storage.enums;

public enum IdentityProvider {
	EMAIL("EMAIL"),
	FACEBOOK("FACEBOOK"),
	GOOGLE("GOOGLE"),
	EMPTY("EMPTY");
	
	private final String displayValue;
	
	private IdentityProvider(String displayValue) {
		this.displayValue = displayValue;
	}
	
	public String getDisplayValue() {
		return displayValue;
	}
}
