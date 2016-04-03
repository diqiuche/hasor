/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.center.server.launcher;
import java.net.URL;
import java.util.Map;
import org.more.future.BasicFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.utils.NameThreadFactory;
/***
 * 基于Netty的简易HttpClient
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class HttpClient {
    private final String         centerHost;
    private final int            centerPort;
    private final EventLoopGroup workerGroup;
    //
    public HttpClient(AbstractRsfContext rsfContext) {
        //        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        this.centerHost = rsfContext.getSettings().getCenterAddress();
        this.centerPort = rsfContext.getSettings().getCenterPort();
        this.workerGroup = rsfContext.get.getWorkLoopGroup();
    }
    //
    public BasicFuture<HttpResponse> request(String requestPath, Map<String, String> reqParams) throws Exception {
        //
        // 初始化Netty
        final Bootstrap b = new Bootstrap();
        final BasicFuture<HttpResponse> future = new BasicFuture<HttpResponse>();
        b.group(this.workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                ch.pipeline().addLast(new HttpResponseDecoder());
                // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                ch.pipeline().addLast(new HttpRequestEncoder());
                ch.pipeline().addLast(new ResponseRead(future));
            }
        });
        //
        // 连接Server
        ChannelFuture f = b.connect(this.centerHost, this.centerPort).sync();
        //
        // 构建http请求
        URL reqPath = new URL("http", this.centerHost, this.centerPort, requestPath);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, reqPath.toString());
        request.headers().set(HttpHeaders.Names.HOST, this.centerHost);
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
        //
        // 发送http请求
        request.content().writeBytes("Are you ok?".getBytes("UTF-8"));
        f.channel().write(request);
        f.channel().flush();
        //
        // 返回异步对象
        f.channel().closeFuture().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture nettyFuture) throws Exception {
                future.cancel();//因连接关闭而取消
            }
        });
        return future;
    }
}
class ResponseRead extends ChannelInboundHandlerAdapter {
    private BasicFuture<HttpResponse> future;
    public ResponseRead(BasicFuture<HttpResponse> future) {
        this.future = future;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            future.completed(response);
        }
    }
}