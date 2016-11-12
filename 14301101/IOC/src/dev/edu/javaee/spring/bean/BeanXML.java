package dev.edu.javaee.spring.bean;

public class BeanXML {
	private String id;
	private String beanClassName;
	private PropertyValues propertyValues;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBeanClassName() {
		return beanClassName;
	}
	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}
	public PropertyValues getPropertyValues() {
		return propertyValues;
	}
	public void setPropertyValues(PropertyValues propertyValues) {
		this.propertyValues = propertyValues;
	}
	
	
}
