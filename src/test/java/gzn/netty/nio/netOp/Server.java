package gzn.netty.nio.netOp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

import static gzn.netty.nio.byteBuffer.ByteBufferUtil.debugAll;

@Slf4j
public class Server {

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                int length = i - source.position() + 1;
                ByteBuffer byteBuffer = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    byteBuffer.put(source.get());
                }
                debugAll(byteBuffer);
            }
        }
        source.compact();
    }

    public static void main(String[] args) throws IOException {
        // 1. 创建 selector, 管理多个 channel
        Selector selector = Selector.open();
        ServerSocketChannel scc = ServerSocketChannel.open();
        scc.configureBlocking(false);  // 设置为非阻塞模式

        // 2. 建立 selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个channel的事件
        SelectionKey sccKey = scc.register(selector, 0, null);
        // key 只关注 accept 事件
        sccKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("sccKey:{}", sccKey);
        scc.bind(new InetSocketAddress(8080));

        while (true) {
            // 3. select 方法, 没有事件发生，线程阻塞，有事件，线程才会恢复运行
            // select 在事件未处理时，它不会阻塞, 事件发生后要么处理，要么取消，不能置之不理
            selector.select();

            // 4. 处理事件, selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 处理key 时，要从 selectedKeys 集合中删除，否则下次处理就会有问题
                iter.remove();
                log.debug("key: {}", key);

                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);

                    // 将一个 byteBuffer 作为附件关联到 selectionKey 上
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0, byteBuffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                    log.debug("scKey:{}", scKey);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();  // 拿到触发事件的channel
                        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();  // 获取 selectionKey 上的附件
                        int read = channel.read(byteBuffer);  // 如果是正常断开，read 的方法的返回值是 -1
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(byteBuffer);

                            if (byteBuffer.position() == byteBuffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2);
                                byteBuffer.flip();
                                newBuffer.put(byteBuffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();  // 因为客户端断开了,因此需要将 key 取消（从 selector 的 keys 集合中真正删除 key）
                    }
                }
            }
        }

    }
}
