import com.roc.HelloService;
import com.roc.annotation.RpcScan;
import com.roc.config.RpcServiceConfig;
import com.roc.remoting.transport.netty.server.NettyRpcServer;
import com.roc.service.impl.HelloServiceImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description Service: Automatic registration service via @RpcService annotation
 * @Author dongp
 * @Date 2022/7/29 0029 18:11
 */
@RpcScan(basePackage = {"com.roc"})
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // Register service manually
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder().group("test2").version("version2")
                .service(helloService2).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
