package com.j_ssh.model.objects;

import com.j_ssh.api.AlertHandler;
import com.j_ssh.api.MyUserInfo;
import com.jcraft.jsch.*;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection {
    @Getter
    private String username;
    @Getter
    private String host;
    @Getter
    private String password;
    @Getter
    private int port;

    @Getter
    private Session session;
    @Getter
    private Channel channel;
    private PipedOutputStream pipe;
    private InputStream in;
    private final BlockingQueue<byte[]> incoming = new LinkedBlockingQueue<>();
    private final OutputStream remoteOutput = new OutputStream() {
        @Override
        public void write(int b) {
            incoming.offer(new byte[]{(byte) b});
        }

        @Override
        public void write(byte[] buffer, int offset, int length) {
            if (length <= 0) {
                return;
            }
            incoming.offer(Arrays.copyOfRange(buffer, offset, offset + length));
        }
    };
    private String error = null;
    public Connection(String username, String host, String password, int port) {
        this.username = username;
        this.host = host;
        this.password = password;
        this.port = port;
        JSch.setLogger(new MyLogger());
    }

    public boolean addKnownHost() {
        Session session = null;
        try {
            JSch ssh = new JSch();
            ssh.setKnownHosts("knownHosts.txt");
            session = ssh.getSession(this.username, this.host,22);
            MyUserInfo ui = new MyUserInfo();
            ui.setPassword(this.password);
            ui.setQuiet(true);
            session.setUserInfo(ui);
            session.setConfig(
                    "PreferredAuthentications", "password,keyboard-interactive");
            session.setPassword(this.password);

            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e1) {
            e1.printStackTrace();
            try {
                if (session == null || session.getHostKey() == null) return false;
                String hostKeyEntry = this.host + " ssh-rsa " + session.getHostKey().getKey();
                if (!this.isHostKeyPresent("knownHosts.txt", hostKeyEntry)) {
                    FileWriter tmpwriter = new FileWriter("knownHosts.txt", true);
                    tmpwriter.append(hostKeyEntry).append("\n");

                    tmpwriter.flush();
                    tmpwriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        session.disconnect();
        return true;
    }

    private boolean isHostKeyPresent(String knownHostsFile, String hostKeyEntry) {
        try (BufferedReader reader = new BufferedReader(new FileReader(knownHostsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(hostKeyEntry)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean connect() {
        JSch jsch = new JSch();
        try {
            jsch.setKnownHosts("knownHosts.txt");
            Session session = jsch.getSession(this.username, this.host, this.port);
            session.setPassword(this.password);
            com.j_ssh.api.MyUserInfo ui = new com.j_ssh.api.MyUserInfo();
            ui.setPassword(this.password);
            session.setUserInfo(ui);
            session.connect();
            this.session = session;

            this.in = new PipedInputStream(65536);
            this.pipe = new PipedOutputStream((PipedInputStream) this.in);
        } catch (JSchException | IOException e) {
            this.error = e.getMessage();
            AlertHandler.triggerExceptionAlert("Connection Error", "Error Encountered", e);
            return false;
        }
        return true;
    }

    public boolean start() {
        try {
            ChannelShell shell = (ChannelShell) this.session.openChannel("shell");
            shell.setPtyType("xterm");
            shell.setPtySize(120, 40, 800, 600);
            shell.setInputStream(this.in);
            shell.setOutputStream(this.remoteOutput);
            shell.connect();
            this.channel = shell;
        } catch (JSchException e) {
            this.error = e.getMessage();
            AlertHandler.triggerExceptionAlert("Connection Error", "Error Encountered", e);
            return false;
        }
        return true;
    }

    public boolean sendCommand(String cmd) {
        try {
            synchronized (this.pipe) {
                this.pipe.write(cmd.getBytes(StandardCharsets.UTF_8));
                this.pipe.flush();
            }
        } catch (IOException e) {
            this.error = e.getMessage();
            AlertHandler.triggerExceptionAlert("Connection Error", "Error Encountered", e);
            return false;
        }
        return true;
    }

    /**
     * Blocks until the server sends something, then returns that data plus anything
     * already queued behind it.
     */
    public byte[] readIncoming() throws InterruptedException {
        byte[] first = incoming.take();
        if (incoming.isEmpty()) {
            return first;
        }
        List<byte[]> chunks = new ArrayList<>();
        chunks.add(first);
        incoming.drainTo(chunks);
        int size = 0;
        for (byte[] chunk : chunks) {
            size += chunk.length;
        }
        byte[] merged = new byte[size];
        int offset = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, merged, offset, chunk.length);
            offset += chunk.length;
        }
        return merged;
    }

    public void disconnect() {
        this.channel.disconnect();
        this.session.disconnect();
    }

    public boolean isConnected() {
        if (this.session.isConnected())
            if (this.channel.isConnected())
                return true;
        return false;
    }

    public String error() {
        return this.error;
    }

    public InputStream getInputStream() {
        return this.in;
    }
    private static class MyLogger implements com.jcraft.jsch.Logger {
        static java.util.Hashtable<Integer, String> name = new java.util.Hashtable<>();
        static {
            name.put(DEBUG, "DEBUG: ");
            name.put(INFO, "INFO: ");
            name.put(WARN, "WARN: ");
            name.put(ERROR, "ERROR: ");
            name.put(FATAL, "FATAL: ");
        }

        public boolean isEnabled(int level) {
            return true;
        }

        public void log(int level, String message) {
            System.out.print(name.get(level));
            System.out.println(message);
        }
    }
}
