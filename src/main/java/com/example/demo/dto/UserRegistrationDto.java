package com.example.demo.dto;

public class UserRegistrationDto {
    
    private String uemail;
    private String uname;
    private Long unumber; // <-- CHANGED: Was String, now Long to match your entity
    private String upassword;

    // --- Getters and Setters for all fields ---
    
    public String getUemail() { return uemail; }
    public void setUemail(String uemail) { this.uemail = uemail; }
    
    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }
    
    public Long getUnumber() { return unumber; } // <-- CHANGED
    public void setUnumber(Long unumber) { this.unumber = unumber; } // <-- CHANGED
    
    public String getUpassword() { return upassword; }
    public void setUpassword(String upassword) { this.upassword = upassword; }
}