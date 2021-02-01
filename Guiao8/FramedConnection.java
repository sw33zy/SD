import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class FramedConnection implements AutoCloseable{
    private final Socket s;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final Lock wlock = new ReentrantLock();
    private final Lock rlock = new ReentrantLock();

    public FramedConnection(Socket socket) throws IOException{
        this.s = socket;
        this.is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(byte[] data) throws IOException{
        try{
            wlock.lock();
            os.writeInt(data.length);
            os.write(data);
            os.flush();
        } finally {wlock.unlock();}
    }

    public byte[] receive() throws IOException{
        byte[] data;
        try{
            rlock.lock();
            data = new byte[is.readInt()];
            is.readFully(data);
        } finally {rlock.unlock();}
        return data;
    }

    public void close() throws IOException{
        s.close();
    }
    
}
