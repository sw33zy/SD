import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientReader {
    public static void main(String[] args) throws IOException{
        Socket socket  = new Socket("localhost",34567);

        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        int elements = in.readInt();

        for(int i=0; i<elements; i++){
            Contact contact = Contact.deserialize(in);
            System.out.println(i+1 + ":" + contact.toString());
        }

        socket.shutdownInput();
        socket.close();

    }
}
