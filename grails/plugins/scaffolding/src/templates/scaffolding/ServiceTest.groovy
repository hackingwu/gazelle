<%=packageName ? "package ${packageName}\n" : ''%>

/**
 * @author ${System.getProperty('user.name')}
 * @since ${new Date().format('yyyy-MM-dd')}
 */
class ${className}ServiceTest extends GroovyTestCase {

    def ${domainClass.propertyName}Service

    void setUp() {

    }

    void tearDown() {

    }


    void testAdd(){
        ${className} instance = new ${className}()
        ${domainClass.propertyName}Service.save(instance)

    }

    void testDel(){
        ${className} instance = new ${className}()
        ${domainClass.propertyName}Service.save(instance)
        ${domainClass.propertyName}Service.delete(instance)
    }

    void testUpdate(){
        ${className} instance = new ${className}()
        ${domainClass.propertyName}Service.save(instance)
        ${domainClass.propertyName}Service.update(instance)
    }

    void testSearch(){
        ${className} instance = new ${className}()
        ${domainClass.propertyName}Service.save(instance)

        assert  ${domainClass.propertyName}Service.findAll().size() > 0
    }

}