package online.stringtek.distributed.toy.rpc.demo.provider.listener;

import online.stringtek.distributed.toy.rpc.server.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RpcServerListener implements ApplicationRunner {
    @Value("${toy.rpc.host}")
    private String host;
    @Value("${toy.rpc.port}")
    private int port;
    @Autowired
    private RpcServer rpcServer;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        rpcServer.start(host,port);
    }
}
