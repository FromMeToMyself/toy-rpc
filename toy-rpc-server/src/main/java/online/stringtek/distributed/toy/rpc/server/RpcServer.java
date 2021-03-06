package online.stringtek.distributed.toy.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import online.stringtek.distributed.toy.rpc.core.common.RpcConstant;
import online.stringtek.distributed.toy.rpc.core.common.RpcRequest;
import online.stringtek.distributed.toy.rpc.core.handler.RpcRequestDecoder;
import online.stringtek.distributed.toy.rpc.core.serializer.JSONSerializer;
import online.stringtek.distributed.toy.rpc.server.handler.RpcRequestHandler;
import online.stringtek.distributed.toy.rpc.core.handler.RpcResponseEncoder;

import java.net.InetSocketAddress;
@Slf4j
public class RpcServer {
    private final RpcRequestHandler rpcRequestHandler;
    public RpcServer(RpcRequestHandler rpcRequestHandler){
        this.rpcRequestHandler=rpcRequestHandler;
    }
    public void start(String hostname,int port){
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        //入站
                        nioSocketChannel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(RpcConstant.MAX_FRAME_LENGTH,0,4,0,4))
                                .addLast(new RpcRequestDecoder(RpcRequest.class,new JSONSerializer()))
                                .addLast(rpcRequestHandler);
                        //出站
                        nioSocketChannel.pipeline()
                                .addLast(new RpcResponseEncoder(new JSONSerializer()));
                    }
                });
        try{
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(hostname, port)).sync();
            //同步监听
            log.info("rpc server is listening on "+hostname+":"+port);
//            future.channel().closeFuture().sync();
//            System.out.println("rpc server is closed.");
        }catch (Exception e){
            e.printStackTrace();
        }finally{
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
        }
    }
}
