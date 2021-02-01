package g8;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable{
    private final Socket s;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final Lock wlock = new ReentrantLock();
    private final Lock rlock = new ReentrantLock();

    public static class Frame{
        public final int tag;
        public final byte[] data;
        public Frame(int tag, byte[] data){
            this.tag=tag;
            this.data=data;
        }
    }

    public TaggedConnection(Socket socket) throws IOException{
        this.s = socket;
        this.is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException{
        send(frame.tag, frame.data);
    }

    public void send(int tag, byte[] data) throws IOException{
        try{
            wlock.lock();
            os.writeInt(4 + data.length);
            os.writeInt(tag);
            os.write(data);
            os.flush();
        } finally {wlock.unlock();}
    }

    public Frame receive() throws IOException{
        byte[] data;
        int tag;
        try{
            rlock.lock();
            data = new byte[is.readInt() - 4];
            tag = is.readInt();
            is.readFully(data);
        } finally {rlock.unlock();}
        return new Frame(tag,data);
    }
   
    public void close() throws IOException{
        s.close();
    }
}
