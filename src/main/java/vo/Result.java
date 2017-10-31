package vo;

public class Result {
	private boolean status;
	private String username;

	public boolean isStatus() {
		return status;
	}

	public String getUsername() {
		return username;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Result{" +
				"status=" + status +
				", username='" + username + '\'' +
				'}';
	}
}
