package com.jayrush.springmvcrest.KeysModel;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "terminalKeys")
public class TerminalKeys {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;
    @Column(name = "TerminalID")
    private String TerminalID;
    @Column(name = "MasterKey")
    private String MasterKey;
    @Column(name = "SessionKey")
    private String SessionKey;
    @Column(name = "PinKey")
    private String Pinkey;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTerminalID() {
        return TerminalID;
    }

    public void setTerminalID(String terminalID) {
        TerminalID = terminalID;
    }

    public String getMasterKey() {
        return MasterKey;
    }

    public void setMasterKey(String masterKey) {
        MasterKey = masterKey;
    }

    public String getSessionKey() {
        return SessionKey;
    }

    public void setSessionKey(String sessionKey) {
        SessionKey = sessionKey;
    }

    public String getPinkey() {
        return Pinkey;
    }

    public void setPinkey(String pinkey) {
        Pinkey = pinkey;
    }
}
