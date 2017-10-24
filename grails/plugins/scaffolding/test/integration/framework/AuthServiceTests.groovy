package framework

/**
 *
 */
class AuthServiceTests extends GroovyTestCase {

    static transactional = false

    def authService

    def setup() {
    }

    def cleanup() {
    }

    void test按钮() {
        assert authService.Menu("ROLE_ADMIN", "homeIndex") == true
    }

    void test禁用菜单() {
        assert authService.Menu("ROLE_USER", "studentIndex") == false
    }

    void test未定义基本按钮() {
        assert authService.Button("ROLE_USER", "homeIndex", "create") == true
    }

    void test可用按钮() {
        assert authService.Button("ROLE_USER", "programmerIndex", "detail") == true
    }

    void test禁用按钮() {
        assert authService.Button("ROLE_USER", "programmerIndex", "create") == false
    }

    void test禁用菜单按钮() {
        assert authService.Button("ROLE_USER", "studentIndex", "create") == false
    }

    void test未定义属性获取() {
        log.info authService.DisableAttributes("ROLE_USER", "basicGrid","basicGridIndex")
    }

    void test已定义属性获取() {
        log.info authService.DisableAttributes("ROLE_USER", "teacher")
    }


//    void testTeacherMultiThreading() {
//
//        int counter=Teacher.count
//        int THREADS_NUM=100
//        int NUM_OF_CREATE=1000
//        List<Thread> threads=[]
//        for(int i=0;i<THREADS_NUM;i++)
//        {
//            threads << Thread.start{
//                for(int j=0; j<NUM_OF_CREATE; j++) {
//                    teacherService.save(new Teacher(name: "陈冠希${j}", gender: "男", description: "中国男人的偶像"))
//                    sleep(2)
//                }
//            }
//        }
//
//        threads*.join()
//
//        println "多线程创建"
//        println counter
//        println Teacher.count
//
//        assert counter + THREADS_NUM*NUM_OF_CREATE == Teacher.count
//    }
}
