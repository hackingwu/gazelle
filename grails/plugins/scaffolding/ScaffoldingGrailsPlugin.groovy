/*
 * Copyright 2004-2013 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager

import java.lang.reflect.Method
import java.lang.reflect.Modifier

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.compiler.injection.NamedArtefactTypeAstTransformation
import org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator
import org.codehaus.groovy.grails.scaffolding.GrailsTemplateGenerator
import org.codehaus.groovy.grails.scaffolding.view.ScaffoldingViewResolver
import org.codehaus.groovy.grails.web.pages.FastStringWriter
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateRenderer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext

class ScaffoldingGrailsPlugin {
	private Logger log = LoggerFactory.getLogger(getClass())

	String version = '1.0.1'
	String grailsVersion = '2.3.8 > *'
	def pluginExcludes = ['grails-app/domain/**', 'grails-app/conf/UrlMappings.groovy', 'grails-app/i18n/**']

	String title = 'Grails Scaffolding Plugin'
	String author = 'Burt Beckwith'
	String authorEmail = 'bbeckwith@gopivotal.com'
	String description = 'Handles the configuration of dynamic scaffolding'
	String documentation = 'http://grails.org/plugin/scaffolding'

	def observe = ['controllers', 'domainClass']
	def loadAfter = ['controllers', 'groovyPages']

	String license = 'APACHE'
	def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPSCAFFOLD']
	def scm = [url: 'https://github.com/grails-plugins/grails-scaffolding']
	def organization = [name: 'SpringSource', url: 'http://www.springsource.org/']

	def doWithSpring = {
		println("scaffolding 启动")
        ScaffoldingViewResolver.clearViewCache()

		scaffoldedActionMap(HashMap)

		controllerToScaffoldedDomainClassMap(HashMap)

		scaffoldingTemplateGenerator(DefaultGrailsTemplateGenerator, ref("classLoader")) {
			grailsApplication = ref("grailsApplication")
		}

		jspViewResolver(ScaffoldingViewResolver) { bean ->
			bean.lazyInit = true
			bean.parent = 'abstractViewResolver'

			templateGenerator = scaffoldingTemplateGenerator
			scaffoldedActionMap = ref("scaffoldedActionMap")
			scaffoldedDomains = controllerToScaffoldedDomainClassMap
		}
        //load configs
        loadConfigs(application,manager, new GroovyClassLoader(getClass().classLoader))

    }

	def doWithApplicationContext = { ctx ->


		if (application.warDeployed) {
			configureScaffolding ctx, application
			return
		}

		try {
            log.info "configuring scaffolding..."
			configureScaffolding ctx, application
            log.info "done configuring scaffolding."
		}
		catch (e) {
			log.error "Error configuration scaffolding: $e.message", e
		}

	}

	def onChange = { event ->
		ScaffoldingViewResolver.clearViewCache()
		if (event.ctx?.groovyPagesTemplateRenderer) {
			GroovyPagesTemplateRenderer renderer = event.ctx?.groovyPagesTemplateRenderer
			renderer.clearCache()
		}
		if (event.source && application.isControllerClass(event.source)) {
			GrailsControllerClass controllerClass = application.getControllerClass(event.source.name)
			configureScaffoldingController(event.ctx, event.application, controllerClass)
		}
		else {
			configureScaffolding(event.ctx, event.application)
		}
	}

	private void configureScaffolding(ApplicationContext ctx, GrailsApplication application) {
		for (controllerClass in application.controllerClasses) {
			configureScaffoldingController(ctx, application, controllerClass)
		}
	}

	private void configureScaffoldingController(ApplicationContext ctx, GrailsApplication application, GrailsControllerClass controllerClass) {

		def scaffoldProperty = controllerClass.getPropertyValue("scaffold", Object)
		if (!scaffoldProperty || !ctx) {
			return
		}

		Map scaffoldedActionMap = ctx.scaffoldedActionMap
		GrailsDomainClass domainClass = getScaffoldedDomainClass(application, controllerClass, scaffoldProperty)
		scaffoldedActionMap[controllerClass.logicalPropertyName] = []
		if (!domainClass) {
			log.error "Cannot generate controller logic for scaffolded class {}. It is not a domain class!", scaffoldProperty
			return
		}

		GrailsTemplateGenerator generator = ctx.scaffoldingTemplateGenerator
		ClassLoader parentLoader = ctx.classLoader

		Map scaffoldedDomains = ctx.controllerToScaffoldedDomainClassMap
		scaffoldedDomains[controllerClass.logicalPropertyName] = domainClass
		String controllerSource = generateControllerSource(generator, domainClass)
		def scaffoldedInstance = createScaffoldedInstance(parentLoader, controllerSource)
		ctx.autowireCapableBeanFactory.autowireBeanProperties(scaffoldedInstance, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false)
		List actionProperties = getScaffoldedActions(scaffoldedInstance)

		def metaClass = controllerClass.clazz.metaClass

		for (actionProp in actionProperties) {
			if (actionProp == null) {
				continue
			}

			String propertyName = actionProp instanceof MetaProperty ? actionProp.name : actionProp.method
			def mp = metaClass.getMetaProperty(propertyName)
			scaffoldedActionMap[controllerClass.logicalPropertyName] << propertyName

			if (!mp) {
				Closure propertyValue = actionProp instanceof MetaProperty ? actionProp.getProperty(scaffoldedInstance) : actionProp
				metaClass."${GrailsClassUtils.getGetterName(propertyName)}" = {->
					propertyValue.delegate = delegate
					propertyValue.resolveStrategy = Closure.DELEGATE_FIRST
					propertyValue
				}
			}
			controllerClass.registerMapping(propertyName)
		}
	}

	private GrailsDomainClass getScaffoldedDomainClass(application, GrailsControllerClass controllerClass, scaffoldProperty) {

		if (!scaffoldProperty) {
			return null
		}

		if (scaffoldProperty instanceof Class) {
			return application.getDomainClass(scaffoldProperty.name)
		}

		scaffoldProperty = controllerClass.packageName ? "${controllerClass.packageName}.${controllerClass.name}" : controllerClass.name
		return application.getDomainClass(scaffoldProperty)
	}

	private createScaffoldedInstance(ClassLoader parentLoader, String controllerSource) {
		def configuration = new CompilerConfiguration()
		configuration.addCompilationCustomizers(new ASTTransformationCustomizer(new NamedArtefactTypeAstTransformation(ControllerArtefactHandler.TYPE)))

		return new GroovyClassLoader(parentLoader, configuration).parseClass(controllerSource).newInstance()
	}

	private List getScaffoldedActions(scaffoldedInstance) {
		def actionProperties = scaffoldedInstance.metaClass.properties.findAll { MetaProperty mp ->
			try {
				return mp.getProperty(scaffoldedInstance) instanceof Closure
			}
			catch (Exception ignored) {}
		}

		def methodActions = scaffoldedInstance.getClass().declaredMethods.findAll { Method m ->
			def modifiers = m.modifiers
			Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isSynthetic(modifiers)
		}.collect { Method m -> scaffoldedInstance.&"$m.name"}
		actionProperties.addAll methodActions
		return actionProperties
	}

	private String generateControllerSource(GrailsTemplateGenerator generator, GrailsDomainClass domainClass) {
		def sw = new FastStringWriter()
		log.info "Generating controller logic for scaffolding domain: {}", domainClass.fullName
		generator.generateController domainClass, sw
		return sw.toString()
	}

    //load plugins config
    private void loadConfigs(GrailsApplication application,GrailsPluginManager manager,ClassLoader classLoader){
        List<String> configNames = ['AuthConfig','ScaffoldingConfig']
        Map configs =  application.config.configs
        List excluded = ['JqueryConfig']

        if(configs){
            if(configs.included){
                configNames += configs.included
            }
            if(configs.excluded){
                excluded += configs.excluded
            }
        }

        manager.allPlugins.each { plugin ->
            configNames << "${plugin.name.charAt(0).toUpperCase()}${plugin.name.substring(1)}Config".toString()
        }
        configNames.each { configName ->
            try{
                URL url = classLoader.getResource("${configName}.class")
                if(url != null  && !excluded.contains(configName)){
                   ConfigObject config = new ConfigSlurper().parse(classLoader.loadClass(configName))
                   application.config.merge(config)
                }
            }catch (Exception e){

            }
        }

        //application.config
    }
}
