package org.anoosh.soapws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.anoosh.soapws.Business.ProductServiceImpl;

/*
 * This class is our POJO, with one method, that will return
 * a hard coded(no DB used here), and will make this method 
 * into a Web Service by annotating it with @Webservice.
 * That's all it takes to turn any class into a Web Service, a simple
 * javax.jws.WebService annotation. Every public method is visible even without
 * using @webmethod annotation.
 * Now how do we access this ws? Do we need to write a client ws?
 * No , you can use your App server(GlassFish4) administration panel(localhost:4848)
 * and drill to===> Applications,Testmart,View EndPoint, Tester link
 * Other methods has been added.
 * Here in this example we are "Service First" or "Code First"  and the WSDL will auto generate
 * when we deploy into GlassFish server. This is not good for client codes that are using
 * the WSDL since every time we change anything in our ws impl code the auto generated 
 * WSDL will be different, and the client code that was using the previous WSDL would not 
 * work anymore due to tight coupling (a varying WSDL, that client code sees) .
 * "Service First/Code First" style is good for learning web services, but in production
 * environment we usually write the WSDL/"Contract first" and then generate code from it,
 * and add the logic to the code. So it's good to know the WSDL elements and xml.
 * Even in the Contract/WSDL first scenario, we always start with a java class and code 
 * and let glassFish generate the initial WSDL for us, because its very had to write a WSDL
 * from scratch. So even the Contract first, is actually done with code first, then we use
 * the auto-generated WSDL and customize it to fix the WSDL into a constant set of names
 * that will never change for clients code. How? Just use the @WebService and @WebMethod
 * parameters inside your java class implementation. Use Eclipse ctrl-space bar , to get
 * help with the available params, and see how your WSDL gets customized with these 
 * new params and fixed/stable from client's code point of view as GlassFish is forced to 
 * name these XML elements in your WSDL according to the specific parameters to these
 * two @ annotations. 
 * WSDL is like a Java interface, but since we do not know what language the client code
 * is written in(C++,Perl, Visual Basic,etc) we can't use Java interfaces. So we use the
 * WSDL which is XML. But the interface(WSDL) must be fixed and constant so client code
 * can use it without worrying about making changes every time the implementation classes
 * for the web service change. That's why we have the parameters to the 2 @ annotations.
 * It makes the WSDL fixed and unchangeable, regardless of the changes in the implementation
 * classes of the ws.
 * CUSTOMIZING THE WSDL: (add these params, republish, reload the wsdl url to see effect)
 * @WebService(name="fixed_portType_name") will fix the <portType name = ...> regardless 
 * of what you change your class name to be in java
 * @WebService(portName=...) fixes the <port name="..." sub-element of <service name=...>
 * @WebService(serviceName=...) fixes the <service name="..."> element in WSDL
 * @WebService(targetNameSpace="http://www.testmart.com") fixes the package
 * 							name(reverse order of your Java package)
 * in all the WSDL elements like <definitions xmnls:tns="...">, <types> <xsd:import>...namespace="..."
 * ,and all wsam:Action elements of <input message> or <output message> of <portType><operation>
 * NOTE:namespace in XML world is equivalent of Java package/grouping in Java World.
 *      It groups all the XML data types together
 *      
 *      
 * @WebMethod(exclude=true) will make sure that the auto generation of the WSDL will not 
 * include that method or input, output datatypes in the WSDL.
 * @WebMethod(operationName="fix_method_name") fixes the <operation name="..."> of the 
 * <portType>. NOTE:<portType> in WSDL = class name in Java.
 *                  <operation name=...> = method name in Java
 *                  <targetNameSpace="..."> = package name(reverse order) in Java
 * @WebMethod(action="...") fixes the action name in wsam:action property of <input> or <output> for
 * <operation> elements and the <soapaction> property of <soap:operation> elements.
 * 
 * NOW how do we customize  the details of the data types for our method parameters?
 * ===================================================================================
 * The default Soap Binding style is DOCUMENT, because it will generate a separate schema
 * for the data types involved in our class and imports that schema document(hence the 
 * name DOCUMENT for the style). This external schema document of the data types is
 * pointed to by the WSDL via an <xsd:import> inside the <types> and the url of it is
 *  specified in "schemaLocation" property of the <xsd:import> and gets imported into a
 *  namespace="..." to avoid type conflicts in our WSDL.
 * You can open it in browser using its url and see that each data type is defined as an 
 * xs:element XML element and the details of its definition are follows as
 * xs:complexType. You also have minOccurs which for 0 means it allows for null if String
 * is what you used as your method input type or output type. The DOCUMENT style default
 * is good if you want to validate the schema, but since the data types schema is external
 * in a schema document, and is not inline(inside WSDL) it makes it harder to read.
 * If we use the @SoapBinding(style=RPC) your WSDL will have an empty <types/> section
 * and all the data types are easily readable as they are all in-line, but then you loose
 * the advantage of validating the schema. 
 * Inside the schema document, you will see at the top <xs:element> for each input 
 * and each output data type for each method method. By default the input data types
 * are the same name as the method name and the output parameter data types are named as method name
 * suffixed with "Response". Regardless of the number of input parameters there is only one
 * such data type defined for input. 
 * <xs:element name="addProduct" type="tns:addProduct"/>   // INPUT DATA TYPE
 * <xs:element name="addProductResponse" type="tns:addProductResponse"/> //OUTPUT 
 * ......
 * These data type xs:element are later then described inside
 * <xs:complexType name="addProduct">
 * <xs:sequence>
 * <xs:element name="arg0" type="xs:string" minOccurs="0"/>
 * <xs:element name="arg1" type="xs:string" minOccurs="0"/>
 * </xs:sequence>
 * AND
 * <xs:complexType name="addProductResponse">
 * <xs:sequence>
 * <xs:element name="return" type="xs:boolean"/>
 * </xs:sequence>
 * </xs:complexType>
 * 
 * ALL these individual <xs:...> elements are from xs="http://www.w3.org/2001/XMLSchema"
 * standard w3 organization. As you notice the default name for the parameters of input is
 * "arg0","arg1",argx, etc. And for output parameter default name is "return".
 * We can modify these defaults by using customized annotations:
 * Follow the class annotation @WebService with @SOAPBinding(...) to effect Data types
 * and customize the WSDL and make it fixed/constant:
 * 
 * @WebService(.....)
 * @SOAPBinding(style=Style.RPC) will stop external schema document generation and all the
 * data types will be declared in-line/RPC style inside WSDL and become easier to read.
 * 
 * 
 * You will have no <types> section in your WSDL, instead the <message> sections 
 * for the input and output has the data types declared in-line. Also the <soap:binding>
 * will show the style='rpc' instead of the default 'document'. But the "arg0" and "return"
 * default names for the params still exist although now they will be rpc style/in-line.
 * 
 * Annotations to customize the input parameter and output parameters name from
 * "arg0" and "return" defaults are:
 * 
 * -- Input :     ....addProduct(@WebParam(partName="fixed_inputParamName" String prdct)
 * -- Output: @WebMethod(...)
 *            @WebResult(partName="fixed_outputParamName")
 * ==================================================================================
 * When to use 'literal' in @SOAPBinding(use=...) ? literal is default, encoded is not 
 * supported by the standards. So never use the "use" inside @SOAPBinding(...).
 * When to use RPC vs DOCUMENT? When the data types are complex or when you need to 
 * actually validate the schema, use DOCUMENT style(default). When you have really 
 * simple data types or want to make WSDL human readable use RPC style.
 * ====================================Custom Data Types============================================
 * To have your method return a custom data type(POJO) instead of the primitive java
 * data type(String, etc) just define a new POJO class(Product) with constructor, fields,
 * and get/set for its fields in a 'model' package. Then declare a v2 of your web service
 * method to return a List<Product>. Whenever you change the signature of a method you 
 * need to make it available as a new version, because the existing client code is 
 * coded for and expecting the old method signature. Everything else will be handled 
 * automatically by the SOAP and the web service as before. You can see it in the Tester
 * link of GlassFish, check the SOAP response and you will see it returns a List of your
 * POJO.
 * ===================================== Defining a SEI INTERFACE=======================
 * Best practice is not to lock yourself with a single implementation, and always to use
 * an interface. So instead of using all these customized annotations in a class, we will
 * use Eclipse "refactor"--> Extract the interface option and create a new interface file.
 * Then move all the @WebService and @WebMethod and @SoapBinding annotations to the interface
 * file. The only annotation that now will be in our web service implementation class should
 * be @WebService(endpointInterface="full_package_name_of_SEI_Interface")
 * Bug or Feature: Also add portName= & serviceName= of the customization for the WebService
 *  to the top of your implementation class. The port and service name customizations in
 *  the interface file does not work correctly when GlassFish auto generates the WSDL.
 * So add to your implementation class:
 *  @WebService(endpointInterface="...",portName="..." , serviceName="...").
 * 
 * This will allow clients of the WS to program to interface and set us free to change 
 * our implementation classes code or have many new implementations as we wish. And have 
 * your implementation class implement the interface as usual in Java.
 * =========================JAXB Customization with JAXB Annotations==================================================
 * JAXB is used to translate Java member variables to XML elements and vice versa.
 * By default it uses the name of the java variable to create the xml element name
 * ex: int count=15;  ----- > <count> 15 </count>
 * We can customize this name mapping with JAXB annotations in the java class.
 * ex:POJO marked with JAXB Annotation
 * @XmlRootElement(name="Cust")
 * public class Customer { 
 * String name;
 * 	int age;
 * 	int id;
 * @XmlElement
 *	public void setName(String name) {
 *		this.name = name;
 *	}
 * @XmlElement
 * 	public void setAge(int age) {
 *		this.age = age;
 *	}
 *  @XmlAttribute
 *  	public void setId(int id) {
 *  		this.id = id;
 *  	}
 *  -------------------- Java Class to to JAXB marshalling ---------------
 *  public static void main(){
 *  ....................instantiate and populate POJO
 *  try {
 
		File file = new File("C:\\file.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
 
		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
 
		jaxbMarshaller.marshal(customer, file);
		jaxbMarshaller.marshal(customer, System.out);
 
	      } catch (JAXBException e) {
		e.printStackTrace();
	      }
 * Will result in : 
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <cust id="100">
 *     <age>29</age>
 *     <name>mkyong</name>
 *  </cust>  
 *  because we pass it the name="cust" to change from default using of same java variable
 *  name. The same name="..." customization applies to @XMLElement or @XMLAttribute
 *  
 * --------------------------- To Unmarshall (XML-->Java) ---------------------
 * try {
 
		File file = new File("C:\\file.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Customer customer = (Customer) jaxbUnmarshaller.unmarshal(file);
		System.out.println(customer);
 
	  } catch (JAXBException e) {
		e.printStackTrace();
	  } 
 *  
 *  
 *  
 *  
 *  
 *  
 */
@WebService
public class ProductCatalog { 
	ProductServiceImpl ps = new ProductServiceImpl();//for delegation to business tier
	
	public List<String> getProductCategories(){		   
   return ps.getProductCategories();
	}
	
	public List<String> getProducts(String category){
		return ps.getProduct(category);
	}
	
	public boolean addProduct(String category, String Product){
		return ps.addProduct(category, Product);
	}
}
