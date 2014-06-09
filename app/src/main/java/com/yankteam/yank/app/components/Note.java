package com.yankteam.yank.app.components;

/* representation of the yank Note model */
public class Note {

    private Integer nid;
    private String  owner;
    private String  content;

    public Note(Integer _nid, String _owner, String _content) {
        nid     = _nid;
        owner   = _owner;
        content = _content;
    }

    public String getUsername() {
        return owner;
    }
    public String getContent() {
        return content;
    }
}
