import authority.AuthorityService
import com.nd.grails.plugins.log.LogService
import com.nd.grails.plugins.log.MemoryLogService
import com.nd.grails.plugins.log.RedisLogService

// Place your Spring DSL code here
beans = {
    redisLogService(RedisLogService){}

    memoryLogService(MemoryLogService){}

    logService(LogService){logService ->
        redisLogService = ref('redisLogService')
        memoryLogService = ref('memoryLogService')
        sessionFactory = ref('sessionFactory')
    }

    authService(AuthorityService){
        btnAndMenuService = ref('btnAndMenuService')
        roleAccessRelationService = ref('roleAccessRelationService')
    }
}
