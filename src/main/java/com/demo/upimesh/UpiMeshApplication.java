package com.demo.upimesh;

import com.demo.upimesh.config.DesktopConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.awt.GraphicsEnvironment;

/**
 * Entry point for the offline UPI mesh demo.
 * Supports Swing UI when running in desktop mode.
 */
@SpringBootApplication
public class UpiMeshApplication {
    public static void main(String[] args) {
        DesktopConsole console = null;
        if (!GraphicsEnvironment.isHeadless()) {
            System.setProperty("java.awt.headless", "false");
            console = new DesktopConsole();
        }

        SpringApplication.run(UpiMeshApplication.class, args);

        if (console != null) {
            console.setRunning(8080);
        }
    }
}
