package com.j_ssh.model.objects;

import com.j_ssh.api.AlertHandler;
import com.jcraft.jsch.*;

import java.io.*;

public class Connection {
    private String username;
    private String host;
    private String password;
    private int port;

    private Session session;
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
    }

    public boolean addKnownHost() {
        Session session = null;
        try {
            JSch ssh = new JSch();
            ssh.setKnownHosts("knownHosts.txt");
            session = ssh.getSession(this.username, this.host,22);
            session.setPassword(this.password);

            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e1) {
            e1.printStackTrace();
            try {
                if (session == null || session.getHostKey() == null) return false;
                FileWriter tmpwriter = new FileWriter("knownHosts.txt",true);

                tmpwriter.append(this.host + " ssh-rsa " + session.getHostKey().getKey() + "\n");

                tmpwriter.flush();
                tmpwriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        session.disconnect();
        return true;
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

    public String getUsername() {
        return this.username;
    }
    public String getPassword() {
        return this.password;
    }
    public String getHost() {
        return this.host;
    }
    public int getPort() {
        return this.port;
    }
    public Session getSession() {
        return this.session;
    }
    public Channel getChannel() {
        return this.channel;
    }
    public InputStream getInputStream() {
        return this.in;
    }
    public ByteArrayOutputStream getOutputStream() {
        return this.out;
    }
}
