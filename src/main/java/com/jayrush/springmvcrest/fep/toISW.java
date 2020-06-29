package com.jayrush.springmvcrest.fep;

import com.jayrush.springmvcrest.domain.domainDTO.host;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

//import io.vertx.core.AbstractVerticle;

/**
 * @author JoshuaO
 */

public class toISW {
    static Vertx vertx=Vertx.vertx();
    public Future<byte[]> start(final byte[]request, host host) {
        NetClientOptions options = new NetClientOptions().setConnectTimeout(10000).
                setReconnectAttempts(10)
                .setReconnectInterval(500)
                .setLogActivity(true);
//                .setSsl(false).
//                setTrustAll(true);

        NetClient tcpClient = vertx.createNetClient(options);
        final byte[][] result = {null};
        tcpClient.connect(host.getHostPort(), host.getHostIp(), res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                NetSocket socket = res.result();

                socket.write(Buffer.buffer(request));
                socket.handler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        result[0] = buffer.getBytes();
                    }
                });


            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());
            }
        });
        tcpClient.close();
        return Future.succeededFuture(result[0]);

    }
}