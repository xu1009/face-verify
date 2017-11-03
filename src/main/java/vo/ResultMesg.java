package vo;

public class ResultMesg {
	private int success = 1; // 1,success;0,failed
	private Object data; // 返回数据
	private int errorCode;
	private String errorMsg; // 错误信息

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "ResultMesg{" +
				"success=" + success +
				", data=" + data +
				", errorCode=" + errorCode +
				", errorMsg='" + errorMsg + '\'' +
				'}';
	}
}
