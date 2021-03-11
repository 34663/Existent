package zyx.existent.gui.altmanager.althening.api.api.data;

import zyx.existent.gui.altmanager.althening.api.api.info.AccountInfo;

public class AccountData
{
    private String token;
    private String password;
    private String username;
    private boolean limit;
    private AccountInfo info;
    
    public String getToken() {
        return this.token;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public boolean isLimit() {
        return this.limit;
    }
    
    public AccountInfo getInfo() {
        return this.info;
    }
    
    @Override
    public String toString() {
        return String.format("AccountData[%s:%s:%s:%s]", this.token, this.username, this.password, this.limit);
    }
    
    @Override
    public boolean equals(final Object v1) {
        if (!(v1 instanceof AccountData)) {
            return false;
        }
        final AccountData v2 = (AccountData)v1;
        return v2.getUsername().equals(this.username) && v2.getToken().equals(this.token);
    }
}