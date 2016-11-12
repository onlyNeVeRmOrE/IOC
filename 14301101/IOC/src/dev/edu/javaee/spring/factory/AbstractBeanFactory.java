package dev.edu.javaee.spring.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.edu.javaee.spring.bean.BeanDefinition;

public abstract class AbstractBeanFactory implements BeanFactory{
	private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
	
	public Object getBean(String beanName)
	{
		return this.beanDefinitionMap.get(beanName).getBean();
	}
	
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
	{	
		beanDefinition = GetCreatedBean(beanDefinition,beanName);
		if (beanDefinition != null)
		this.beanDefinitionMap.put(beanName, beanDefinition);
	}
	
	public Object getMap() {
		return this.beanDefinitionMap;
	}
	
	protected abstract BeanDefinition GetCreatedBean(BeanDefinition beanDefinition,String beanName);
	
	public Map<String,BeanDefinition> getMapBeanDefinition(){
		return this.beanDefinitionMap;
	}
}
