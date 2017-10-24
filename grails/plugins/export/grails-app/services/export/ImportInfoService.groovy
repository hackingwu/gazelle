package export

import framework.biz.GenericService
import grails.transaction.Transactional

/**
 * @author Administrator
 * @since 2015-01-30
 */
@Transactional(readOnly = true)
class ImportInfoService extends GenericService<ImportInfo,Long>{

    public ImportInfoService(){
        super(ImportInfo.class)

    }

    /**
     * 保存数据
     * @param instance
     */
    @Transactional
    def save(ImportInfo info) throws Exception {
        info.save()
    }

}