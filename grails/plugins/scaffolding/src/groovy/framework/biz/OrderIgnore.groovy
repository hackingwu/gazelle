package framework.biz

import org.hibernate.criterion.Order

/**
 * 用于忽略闭包中order排序条件
 * @since
 * @author xfb
 */
class OrderIgnore {

    def order(String propertyName, String direction){
//        println "Ignore order ${propertyName},${direction}"
    }

    def order(Order o){

    }

    def order(String propertyName) {
//        println "Ignore order ${propertyName}"
    }

}
