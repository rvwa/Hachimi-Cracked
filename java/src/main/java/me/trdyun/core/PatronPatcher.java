package me.trdyun.core;

import me.trdyun.patcher.utils.HachimiHelper;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class PatronPatcher {
    private static native void hookInstall();
    private static native void hookUninstall();

    private static void serverStartup(){
        Thread daemon = new Thread(() -> {
            ServerSocketChannel serverSocketChannel;
            try {
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(7337));
                serverSocketChannel.configureBlocking(true);
            }catch (Exception e) {
                e.printStackTrace();
                Runtime.getRuntime().exit(1);
                return;
            }

            while(true){
                try {
                    SocketChannel channel = serverSocketChannel.accept();
                    new Thread(() ->{
                        try {
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            channel.read(byteBuffer);
                            byteBuffer.flip();

                            String clientRequest = StandardCharsets.UTF_8.decode(byteBuffer).toString().replace("\r", "").replace("\n", "");
                            byteBuffer.clear();
                            String serverResponse = HachimiHelper.responseGenerate(clientRequest);
                            byteBuffer.put(serverResponse.getBytes(StandardCharsets.UTF_8));
                            byteBuffer.flip();
                            channel.write(byteBuffer);
                            byteBuffer.clear();
                            channel.close();
                            Runtime.getRuntime().exit(0);
                        }catch (Exception e) {

                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        daemon.setDaemon(true);
        daemon.start();
    }

    public static void onHackPreLoad() {
        try {
            File libFile = File.createTempFile("patron", ".dll");
            libFile.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(libFile);
            InputStream inputStream = PatronPatcher.class.getResourceAsStream("/PatronCore_64.dll");
            int size;
            byte[] arrayOfByte = new byte[2048];
            while ((size = inputStream.read(arrayOfByte)) != -1) {
                fileOutputStream.write(arrayOfByte, 0, size);
            }
            fileOutputStream.close();
            System.load(libFile.getAbsolutePath());
            Thread.sleep(5000);
            hookInstall();
        }catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed when startHook.");
        }
    }

    public static void disablePatron() {
        hookUninstall();
    }

    public static void main(String[] args) {
        serverStartup();
        JOptionPane.showMessageDialog(null, "Hachimi authentication server is started and will automatically exit after verification\nEnjoy your game :)", "Patron Server (Powered by Triticum Technology)", JOptionPane.INFORMATION_MESSAGE);
    }
}
