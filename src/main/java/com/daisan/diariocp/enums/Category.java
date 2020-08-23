package com.daisan.diariocp.enums;

public enum Category {
    ARGENTINA("Argentina"),ASIA("Asia y Medio Oriente"), AFRICA("África"), EEUU("Estados Unidos"),
    EUROPA("Europa"), HISTORIA("Historia"), LATINOAMERICA("Latinoamérica"), UCA("Avisos");

	private String category;

	private Category(String category) {
		this.category=category;
	}

	public String getUserClass() {
		return category;
	}

	public String toString() {
		return category;
	}
}

