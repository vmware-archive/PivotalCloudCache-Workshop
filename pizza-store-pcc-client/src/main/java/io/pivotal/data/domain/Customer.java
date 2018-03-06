package io.pivotal.data.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

@Entity
@Table(name = "customer")
@Region(name="customer")
public class Customer {

	@Id
	@javax.persistence.Id
	private String id;
	private String name;
	private String email;
	private String address;
	private String birthday;

	public Customer() {}

	public Customer(String id, String name, String email, String address, String birthday) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.address = address;
		this.birthday = birthday;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", email=" + email
				+ ", address=" + address + ", birthday=" + birthday + "]";
	}
}
