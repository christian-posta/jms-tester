package com.fusesource.forge.jmstest.jmxsupport;

// TODO: Clean this up properly

public class JMXUtils {

//    public static MBeanInfo getMBeanInfo(String brokerName, String type, String pattern) throws Exception {
//    	
//    	Set<?> mBeans = getMBeans(brokerName, type, pattern);
//    	Assert.assertEquals(1, mBeans.size());
//    	
//        return getMBeanServerConnection(brokerName).getMBeanInfo(getObjectName(brokerName, type, pattern));
//    }
//    
//    public static Set<?> getMBeans(String brokerName, String type) throws Exception {
//    	return getMBeans(brokerName, type, "*", 0);
//    }
//
//    public static Set<?> getMBeans(String brokerName, String type, String pattern) throws Exception {
//        return getMBeans(brokerName, type, pattern, 0); 
//    }
//
//    public static Set<?> getMBeans(String brokerName, String type, String pattern, int timeout) throws Exception {
//    	
//        final long expiryTime = System.currentTimeMillis() + timeout;
//        final ObjectName beanName = getObjectName(brokerName, type, pattern);
//        
//        Set<?> mbeans = null;
//        do {
//            if (timeout > 0) {
//                Thread.sleep(100);
//            }
//            MBeanServerConnection mbsc = getMBeanServerConnection(brokerName);
//            if (mbsc != null) {
//                LOG.info("Query name: " + beanName);
//                mbeans = mbsc.queryMBeans(beanName, null);
//            }
//        } while ((mbeans == null || mbeans.isEmpty()) && expiryTime > System.currentTimeMillis());
//        return mbeans;
//    }
//
//    public static MBeanServerConnection getMBeanServerConnection(String brokerName) throws MalformedURLException, Exception {
//    	
//    	int jmxPort = getBrokerService(brokerName).getManagementContext().getConnectorPort();
//    	
//        final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + jmxPort + "/jmxrmi");
//        MBeanServerConnection mbsc = null;
//        try {
//            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
//            mbsc = jmxc.getMBeanServerConnection();
//        } catch (Exception ignored) {
//        }
//        return mbsc;
//    }
//
//    public static Object getAttribute(String brokerName, String type, String pattern, String attrName) throws Exception {
//        MBeanInfo info = getMBeanInfo(brokerName, type, pattern);
//        Object obj = getMBeanServerConnection(brokerName).getAttribute(getObjectName(brokerName, type, pattern), attrName);
//        return obj;
//    }
//    
//    public static void queryMBean(String brokerName, String type, String pattern) throws Exception {
//      ObjectName objName = getObjectName(brokerName, type, pattern);
//      MBeanInfo info = getMBeanServerConnection(brokerName).getMBeanInfo(objName);
//      
//      for(MBeanAttributeInfo attr: info.getAttributes()) {
//    	  LOG.info("Found attribute : " + attr.getName());
//      }
//      for(MBeanOperationInfo op: info.getOperations()) {
//    	  LOG.info("Found operation : " + op.getName());
//      }
//      
//      getMBeanServerConnection(brokerName).getAttribute(getObjectName(brokerName, type, pattern), "ConsumerCount");
//    }
//    
//    public static ObjectName getObjectName(String brokerName, String type, String pattern) throws Exception {
//      ObjectName beanName = new ObjectName(
//        "org.apache.activemq:BrokerName=" + brokerName + ",Type=" + type +"," + pattern
//      );
//      
//      return beanName;
//    }
}
