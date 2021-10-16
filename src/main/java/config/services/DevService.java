package config.services;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public interface DevService {
    List<String> start();

    default boolean isPortInUse(int port) {
        boolean result = false;

        try {
            (new Socket("", port)).close();
            result = true;
        } catch (SocketException e) {
            // Could not connect.
        } catch (UnknownHostException e) {
            // Host not found
        } catch (IOException e) {
            // IO exception
        }

        return result;
    }
}
