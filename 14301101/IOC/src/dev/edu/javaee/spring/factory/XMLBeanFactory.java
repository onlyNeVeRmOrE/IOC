package dev.edu.javaee.spring.factory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import dev.edu.javaee.spring.bean.BeanDefinition;
import dev.edu.javaee.spring.bean.BeanUtil;
import dev.edu.javaee.spring.bean.BeanXML;
import dev.edu.javaee.spring.bean.PropertyValue;
import dev.edu.javaee.spring.bean.PropertyValues;
import dev.edu.javaee.spring.AC.*;
import dev.edu.javaee.spring.resource.Resource;

public class XMLBeanFactory extends AbstractBeanFactory {

	private String xmlPath;
	private List<BeanDefinition> beandefList = new ArrayList<BeanDefinition>();
	private List<String> namelist = new ArrayList<String>();
	private List<String> autonamelist = new ArrayList<String>();

	public XMLBeanFactory(Resource resource) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document document = dbBuilder.parse(resource.getInputStream());
			NodeList beanList = document.getElementsByTagName("bean");
			for (int i = 0; i < beanList.getLength(); i++) {
				Node bean = beanList.item(i);
				BeanDefinition beandef = new BeanDefinition();
				String beanClassName = bean.getAttributes().getNamedItem("class").getNodeValue();
				String beanName = bean.getAttributes().getNamedItem("id").getNodeValue();
				beandef.setBeanClassName(beanClassName);

				try {
					Class<?> beanClass = Class.forName(beanClassName);
					beandef.setBeanClass(beanClass);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				PropertyValues propertyValues = new PropertyValues();

				NodeList propertyList = bean.getChildNodes();
				for (int j = 0; j < propertyList.getLength(); j++) {
					// propertyvalue赋值
					Node property = propertyList.item(j);
					if (property instanceof Element) {
						Element ele = (Element) property;

						String name = ele.getAttribute("name");
						Class<?> type;
						try {
							type = beandef.getBeanClass().getDeclaredField(name).getType();
							Object value = ele.getAttribute("value");
							if (value != null && value.toString().length() > 0) {
								if (type == Integer.class) {
									value = Integer.parseInt((String) value);
								}
								propertyValues.AddPropertyValue(new PropertyValue(name, value));
							} else {
								String ref = ele.getAttribute("ref");
								if (ref == null || ref.length() == 0) {
									return;
								}
								beandef.addRefAndRefName(ref, name);
							}

						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				beandef.setPropertyValues(propertyValues);

				this.registerBeanDefinition(beanName, beandef);// 实例化bean
			}

			Map<String, BeanDefinition> mapBeanDef = this.getMapBeanDefinition();
			Iterator it = mapBeanDef.values().iterator();
			Iterator it1 = mapBeanDef.keySet().iterator();
			while (it.hasNext()) {
				String beanName = (String) it1.next();
				BeanDefinition def = (BeanDefinition) it.next();
				if (def.getRef() != null) {
					List<String> refList = def.getRef();
					List<String> refNameList = def.getRefName();
					Iterator itref = refList.iterator();
					Iterator itrefname = refNameList.iterator();
					while (itref.hasNext()) {
						def.getPropertyValues().AddPropertyValue(
								new PropertyValue((String) itrefname.next(), getBean((String) itref.next())));
					}
				}
				this.registerBeanDefinition(beanName, def);
			}

			Map<String, BeanDefinition> beanDefinitionMap = (Map<String, BeanDefinition>) this.getMap();
			AComponentBean cBean = new AComponentBean();
			List<BeanDefinition> autoCreateBeanList = cBean.CreateBean();

			List<String> autoCreateBeanNameList = cBean.getBeanName();
			for (int i = 0; i < autoCreateBeanList.size(); i++) {
				beanDefinitionMap.put(autoCreateBeanNameList.get(i), autoCreateBeanList.get(i));
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, BeanDefinition> map = this.getMapBeanDefinition();
		// 得到所有的Bean Name
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String str = (String) it.next();
			namelist.add(str);
		}
		this.AutoRegisterBean();

	}

	@Override
	protected BeanDefinition GetCreatedBean(BeanDefinition beanDefinition, String beanname) {

		Class<?> beanClass;
		Object bean = null;
		boolean flag = false;
		Constructor con = null;
		try {
			// set BeanClass for BeanDefinition

			beanClass = beanDefinition.getBeanClass();
			con = beanClass.getConstructors()[0];

			if (con.isAnnotationPresent(Autowired.class)) {

				autonamelist.add(beanname);
				beandefList.add(beanDefinition);
				/*
				 * Class[] parameters = con.getParameterTypes(); Object obj[]
				 * =new Object[parameters.length]; int i=0; for(Class pa :
				 * parameters){
				 * 
				 * Iterator it1 = namelist.iterator(); while(it1.hasNext()){
				 * String str = (String) it1.next();
				 * if(pa.equals(getBean(str).getClass())){ obj[i]=getBean(str);
				 * } } i++; } try { bean = con .newInstance(obj); } catch
				 * (IllegalArgumentException e) { // TODO Auto-generated catch
				 * block e.printStackTrace(); } catch (InvocationTargetException
				 * e) { // TODO Auto-generated catch block e.printStackTrace();
				 * } beanDefinition.setBean(bean); return beanDefinition;
				 */
			} else {
				// set Bean Instance for BeanDefinition
				bean = beanClass.newInstance();

				List<PropertyValue> fieldDefinitionList = beanDefinition.getPropertyValues().GetPropertyValues();
				for (PropertyValue propertyValue : fieldDefinitionList) {
					BeanUtil.invokeSetterMethod(bean, propertyValue.getName(), propertyValue.getValue());
				}

				beanDefinition.setBean(bean);

				return beanDefinition;
			}

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void AutoRegisterBean() {
		Class<?> beanClass;
		Object bean = null;
		boolean flag = false;
		Constructor con = null;
		int index = 0;
		for (BeanDefinition beanDefinition : beandefList) {
			beanClass = beanDefinition.getBeanClass();
			con=beanClass.getConstructors()[0];
			Class[] parameters = con.getParameterTypes();
			Object obj[] = new Object[parameters.length];
			int i = 0;
			for (Class pa : parameters) {

				Iterator it1 = namelist.iterator();
				while (it1.hasNext()) {
					String str = (String) it1.next();
					if (pa.equals(getBean(str).getClass())) {
						obj[i] = getBean(str);
					}
				}
				i++;
			}
			try {
				try {
					bean = con.newInstance(obj);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			beanDefinition.setBean(bean);
			Map<String, BeanDefinition> map = this.getMapBeanDefinition();
			map.put(autonamelist.get(index), beandefList.get(index));
			index++;
		}
	}

}
