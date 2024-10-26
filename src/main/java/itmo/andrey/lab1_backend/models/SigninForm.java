package itmo.andrey.lab1_backend.models;

public class SigninForm {
	private String name;
	private String password;

	public String getEmail() {
		return name;
	}

	public void setEmail(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
