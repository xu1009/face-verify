package vo;

public class FaceVo {
	private String data;
	private String id;

	public String getData() {
		return data;
	}

	public String getId() {
		return id;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "FaceVo{" +
				"data='" + data + '\'' +
				", id='" + id + '\'' +
				'}';
	}

}
