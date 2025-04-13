package gzn.netty.nio.netOp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel scc = ServerSocketChannel.open();
        scc.configureBlocking(false);
        scc.register(selector, SelectionKey.OP_ACCEPT);
        scc.bind(new InetSocketAddress(8080));

        while(true){
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if(key.isAcceptable()){
                    SocketChannel sc = scc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ);

                    // 1、向客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i< 5000000;i++){
                        sb.append("a");
                    }
                    ByteBuffer byteBuffer = Charset.defaultCharset().encode(sb.toString());

                    // 2、返回值代表实际写入字节数
                    int write = sc.write(byteBuffer);
                    System.out.println(write);

                    // 3、判断是否有剩余未写入内容
                    if(byteBuffer.hasRemaining()){
                        // 4. 关注可写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // 5. 把未写完的数据挂到 sckey 上
                        scKey.attach(byteBuffer);
                    }
                }else if(key.isWritable()){
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    int write = sc.write(byteBuffer);
                    System.out.println(write);

                    // 6. 清理操作
                    if (!byteBuffer.hasRemaining()) {
                        key.attach(null); // 需要清除buffer
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);//不需关注可写事件
                    }
                }
            }
        }
    }
}
