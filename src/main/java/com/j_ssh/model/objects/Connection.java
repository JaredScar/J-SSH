package com.j_ssh.model.objects;

import com.j_ssh.api.AlertHandler;
import com.j_ssh.api.MyUserInfo;
import com.jcraft.jsch.*;
import lombok.Getter;

import java.io.*;

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
    private PipedOutputStream pipe = new PipedOutputStream();
    private InputStream in;
    private ByteArrayOutputStream out;
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
            session.connect();
            this.session = session;

            this.pipe = new PipedOutputStream();
            this.in = new PipedInputStream(this.pipe);
            this.out = new ByteArrayOutputStream();

            this.channel = this.session.openChannel("shell");
            this.channel.setInputStream(this.in);
            this.channel.setOutputStream(this.out);
            this.channel.connect();
        } catch (JSchException | IOException e) {
            this.error = e.getMessage();
            AlertHandler.triggerExceptionAlert("Connection Error", "Error Encountered", e);
            return false;
        }
        return true;
    }

    public boolean sendCommand(String cmd) {
        try {
            this.pipe.write((cmd + "\n").getBytes());
        } catch (IOException e) {
            this.error = e.getMessage();
            AlertHandler.triggerExceptionAlert("Connection Error", "Error Encountered", e);
            return false;
        }
        return true;
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
    public ByteArrayOutputStream getOutputStream() {
        return this.out;
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
