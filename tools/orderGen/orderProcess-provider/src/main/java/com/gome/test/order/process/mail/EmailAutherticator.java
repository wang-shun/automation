/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gome.test.order.process.mail;
/**
 * @author user
 */

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.io.Serializable;

public class EmailAutherticator extends Authenticator implements Serializable {
    String password = null;
    String username = null;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EmailAutherticator() throws Exception {
        super();

    }

    public EmailAutherticator(String user, String pwd) throws Exception {
        super();
        this.username = user;
        this.password = pwd;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }

}