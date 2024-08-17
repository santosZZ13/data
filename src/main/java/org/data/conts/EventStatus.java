package org.data.conts;

public enum EventStatus {
	FINISHED("finished"),
	NOT_STARTED("notstarted")
	;

	private String status;

	EventStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static EventStatus fromString(String status) {
		switch (status) {
			case "finished":
				return FINISHED;
			case "notstarted":
				return NOT_STARTED;
			default:
				throw new IllegalArgumentException("Invalid status: " + status);
		}
	}
}
