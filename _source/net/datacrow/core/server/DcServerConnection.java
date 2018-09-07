package net.datacrow.core.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.NoSuchPaddingException;

import net.datacrow.core.utilities.CompressedBlockInputStream;
import net.datacrow.core.utilities.CompressedBlockOutputStream;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DcServerConnection {

    private transient static Logger logger = Logger.getLogger(DcServerConnection.class);
    
    private boolean isAvailable = true;
    
    private Socket socket;

    private InputStream is;
    private OutputStream os;
    
    public DcServerConnection(Connector conn) throws IOException, SocketException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        socket = new Socket(conn.getServerAddress(), conn.getApplicationServerPort());
        socket.setKeepAlive(true);
        
        Security.addProvider(new BouncyCastleProvider()); 
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        is = new CompressedBlockInputStream(new DigestInputStream(socket.getInputStream(), hash));
        os = new CompressedBlockOutputStream(new DigestOutputStream(socket.getOutputStream(), hash), 1024);
    }
    
    public InputStream getInputStream() {
        return is;
    }
    
    public OutputStream getOutputStream() {
        return os;
    }
    
    public void disconnect() {
        try {
            is.close();
            os.close();
        } catch (Exception e) {
            logger.error("Error while closing connections", e);
        }
    }
    
    public void setAvailable(boolean b) {
        this.isAvailable = b;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public boolean isActive() {
        return !socket.isClosed();
    }
}
