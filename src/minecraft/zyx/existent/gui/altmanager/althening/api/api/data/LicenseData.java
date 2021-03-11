package zyx.existent.gui.altmanager.althening.api.api.data;

import com.google.gson.annotations.*;

public class LicenseData
{
    private String username;
    private boolean premium;
    @SerializedName("premium_name")
    private String premiumName;
    @SerializedName("expires")
    private String expiryDate;
    
    public String getUsername() {
        return this.username;
    }
    
    public boolean isPremium() {
        return this.premium;
    }
    
    public String getPremiumName() {
        return this.premiumName;
    }
    
    public String getExpiryDate() {
        return this.expiryDate;
    }
    
    @Override
    public String toString() {
        return String.format("LicenseData[%s:%s:%s:%s]", this.username, this.premium, this.premiumName, this.expiryDate);
    }
    
    @Override
    public boolean equals(final Object v1) {
        if (!(v1 instanceof LicenseData)) {
            return false;
        }
        final LicenseData v2 = (LicenseData)v1;
        return v2.getExpiryDate().equals(this.getExpiryDate()) && v2.getPremiumName().equals(this.getPremiumName()) && v2.isPremium() == this.isPremium() && v2.getUsername().equals(this.getUsername());
    }
}