package com.model;

public class Student{
	public Student(Integer Id,String GroupId,String Name){
		this.Id = Id;
		this.GroupId = GroupId;
		this.Name = Name;
	}
	
	private Integer Id;
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getGroupId() {
		return GroupId+Name;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	private String GroupId;
	private String Name;
	
}
