package mysql.storage.enums;

public enum Language {
	ITA("ITA"),
	ENG("ENG");
	
	private final String displayValue;
	
	private  Language(String displayValue) {
		this.displayValue = displayValue;
	}
	
	public String getDisplayValue() {
		return displayValue;
	}
}
