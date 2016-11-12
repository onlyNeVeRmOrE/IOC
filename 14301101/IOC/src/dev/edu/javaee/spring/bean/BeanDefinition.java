package dev.edu.javaee.spring.bean;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {
	private Object bean;
	
	private Class<?> beanClass;
	
	private String beanClassName;
	
	private PropertyValues propertyValues;

	private List<String> reflist = new ArrayList<String>();
	private List<String> refNameList = new ArrayList<String>();
	
	public List<String> getRef() {
		return reflist;
	}
	
	public List<String> getRefName(){
		return refNameList;
	}

	public void addRefAndRefName(String ref,String refName) {
		this.reflist.add(ref);
		this.refNameList.add(refName);
	}

	public PropertyValues getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(PropertyValues propertyValues) {
		this.propertyValues = propertyValues;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public String getBeanClassName() {
		return beanClassName;
	}

	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}
	
}
