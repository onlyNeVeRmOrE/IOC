package test.edu.javaee.spring;

import dev.edu.javaee.spring.factory.BeanFactory;
import dev.edu.javaee.spring.factory.XMLBeanFactory;
import dev.edu.javaee.spring.resource.LocalFileResource;

public class test {

    public static void main(String[] args) {
        LocalFileResource resource = new LocalFileResource("bean.xml");
		
		BeanFactory beanFactory = new XMLBeanFactory(resource);
		boss boss = (boss) beanFactory.getBean("boss");
        System.out.println(boss.tostring());
    }
}