package manager.database;

import javax.persistence.*;

@Entity
@Table(name = "USERS", schema="SYSTEM")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class User 
{
	private String name;
	private String phone;
	private String address;
	private String role;
	
	public User(String name, String phone, String address, String role)
	{
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.role = role;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getPhone()
	{
		return phone;
	}
	
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	public String getRole()
	{
		return role;
	}
	
	public void setRole(String role)
	{
		this.role = role;
	}
}
