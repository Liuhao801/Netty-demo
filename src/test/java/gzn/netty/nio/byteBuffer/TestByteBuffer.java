package gzn.netty.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        //FileChannel
        // 1. 输入输出流， 2. RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while(true){
                // 从 channel 读取数据，向 buffer 写入
                int len = channel.read(byteBuffer);
                log.debug("读取到的字节数 {}", len);
                if(len == -1){ // 没有内容了
                    break;
                }
                // 打印 buffer 的内容
                byteBuffer.flip(); // 切换至读模式
                while(byteBuffer.hasRemaining()){ // 是否还有剩余未读数据
                    byte b = byteBuffer.get();
                    log.debug("实际字节 {}", (char) b);
                }
                byteBuffer.clear(); // 切换为写模式
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
