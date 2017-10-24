package framework.biz

import framework.util.StringUtil



/**
 * 分页对象,分页，记录数相关
 * @author xfb
 * @since 2014-09-11
 */
class Page {

    int currentPage = 1

    int pageSize = 15

    /**
     * 是否计算总数
     */
    boolean isCount = true

    /**
     * 总记录数
     */
    int totalResults = 0

    private int firstResult = -1

    /**
     * 创建分页对象
     * @param currentPage
     * @param pageSize
     * @return
     */
    static Page createPage(int currentPage,int pageSize) {
        Page page = new Page()

        if(currentPage < 1){
            currentPage = 1
        }

        if(pageSize < 1){
            pageSize = 15
        }
        page.setPageSize(pageSize)
        page.setCurrentPage(currentPage)
        return page
    }

    static Page createPage(String currentPage,String pageSize) {
        int c = 1
        int p = 15

        try {
            c = Integer.parseInt(currentPage)
        } catch (NumberFormatException e) {

        }

        try {
            p = Integer.parseInt(pageSize)
        } catch (NumberFormatException e) {
        }

        return createPage(c,p)
    }

    static Page createPage(Map params){
        Page page = new Page()
        int offset = 0
        int limit = 15
        if (!StringUtil.isEmpty(params.offset)||!StringUtil.isEmpty(params.limit)){
            try{
                offset = Integer.valueOf(params.offset)
            }catch (NumberFormatException e){
            }
            try{
                limit = Integer.valueOf(params.limit?:"15")
            }catch (NumberFormatException e){
            }
            page.setFirstResult(offset)
            page.setPageSize(limit)
        }else if(params.page){
            createPage(params.page,params.pageSize?:"15")
        }
        return page
    }
    /**
     * 分页起始记录
     * @return
     */
    int getFirstResult(){
        return firstResult ==-1 ? (currentPage-1) * this.pageSize : this.firstResult
    }


    /**
     * pageSize,单页数量
     * @return
     */
    int getMaxResults() {
        return this.pageSize
    }

    void setFirstResult(int firstResult) {
        this.firstResult = firstResult
    }

    void setPageSize(int pageSize) {
        this.pageSize = (pageSize < 1 ? 1 :pageSize)
    }

    int getTotalPages() {

        int r =  this.totalResults % this.pageSize

        if(r == 0){
            return this.totalResults/this.pageSize
        }
        return this.totalResults/this.pageSize + 1
    }

    List pageList(List datas){
        setTotalResults(datas.size())
        if (getFirstResult() >= datas.size()) return Collections.emptyList()
        int end = getFirstResult()+getPageSize()
        if (end > datas.size()) end = datas.size()
        return datas.subList(getFirstResult(),end)
    }
}
