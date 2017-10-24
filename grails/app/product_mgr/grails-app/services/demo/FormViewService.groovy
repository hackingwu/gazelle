package demo

import framework.biz.GenericService
import grails.transaction.Transactional

@Transactional(readOnly = true)
class FormViewService extends GenericService<FormView,Long> {

    public FormViewService(){
        super(FormView.class)

    }




}