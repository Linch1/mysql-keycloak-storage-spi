package mysql.storage.enums;

public enum UserGenre {
	M("M"),
	F("F");
	
	private final String displayValue;
	
	private UserGenre(String displayValue) {
		this.displayValue = displayValue;
	}
	
	public String getDisplayValue() {
		return displayValue;
	}
}
