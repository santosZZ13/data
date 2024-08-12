package org.data.conts;

public enum FetchStatus {
	FETCHED,
	NOT_FETCHED,
	FETCHING;


	public static FetchStatus fromString(String status) {
		switch (status) {
			case "FETCHED":
				return FETCHED;
			case "NOT_FETCHED":
				return NOT_FETCHED;
			case "FETCHING":
				return FETCHING;
			default:
				throw new IllegalArgumentException("Invalid status: " + status);
		}
	}
}
