package zyx.existent.gui.altmanager.althening.api.api;

import java.util.logging.*;
import java.io.*;
import com.google.gson.*;
import zyx.existent.gui.altmanager.althening.api.api.data.AccountData;
import zyx.existent.gui.altmanager.althening.api.api.data.LicenseData;
import zyx.existent.gui.altmanager.althening.api.api.util.HttpUtils;

import java.util.concurrent.*;

public class TheAltening extends HttpUtils {
    private final String apiKey;
    private final String endpoint = "http://api.thealtening.com/v1/";
    private final Logger logger;
    private final Gson gson;

    public TheAltening(final String v1) {
        this.logger = Logger.getLogger("TheAltening");
        this.gson = new Gson();
        this.apiKey = v1;
    }

    public LicenseData getLicenseData() {
        try {
            System.setProperty("http.agent", "chrome");
            final String v2 = this.connect(String.format("http://api.thealtening.com/v1/license?token=%s", this.apiKey));
            return (LicenseData) this.gson.fromJson(v2, (Class) LicenseData.class);
        } catch (IOException v3) {
            if (v3.getMessage().contains("401")) {
                this.logger.info("Invalid API Key provided");
            } else {
                this.logger.info("Failed to communicate with the website. Try again later");
            }
            return null;
        }
    }

    public AccountData getAccountData() {
        try {
            final String v2 = this.connect(String.format("http://api.thealtening.com/v1/generate?info=true&token=%s", this.apiKey));
            return (AccountData) this.gson.fromJson(v2, (Class) AccountData.class);
        } catch (IOException v3) {
            if (v3.getMessage().contains("401")) {
                this.logger.info("Invalid API Key provided");
            } else {
                this.logger.info("Failed to communicate with the website. Try again later");
            }
            return null;
        }
    }

    public boolean isPrivate(final String v1) {
        try {
            final String v2 = this.connect("http://api.thealtening.com/v1/private?acctoken=" + v1 + "&token=" + this.apiKey);
            final JsonObject v3 = (JsonObject) this.gson.fromJson(v2, (Class) JsonObject.class);
            return v3 != null && v3.has("success") && v3.get("success").getAsBoolean();
        } catch (IOException v4) {
            if (v4.getMessage().contains("401")) {
                this.logger.info("Invalid API Key provided");
            } else {
                this.logger.info("Failed to communicate with the website. Try again later");
            }
            return false;
        }
    }

    public boolean isFavorite(final String v1) {
        try {
            final String v2 = this.connect("http://api.thealtening.com/v1/favorite?acctoken=" + v1 + "&token=" + this.apiKey);
            final JsonObject v3 = (JsonObject) this.gson.fromJson(v2, (Class) JsonObject.class);
            return v3 != null && v3.has("success") && v3.get("success").getAsBoolean();
        } catch (IOException v4) {
            if (v4.getMessage().contains("401")) {
                this.logger.info("Invalid API Key provided");
            } else {
                this.logger.info("Failed to communicate with the website. Try again later");
            }
            return false;
        }
    }

    public static class Asynchronous {
        private TheAltening theAltening;

        public Asynchronous(final TheAltening v1) {
            this.theAltening = v1;
        }

        public CompletableFuture<LicenseData> getLicenseData() {
            return CompletableFuture.supplyAsync(this.theAltening::getLicenseData);
        }

        public CompletableFuture<AccountData> getAccountData() {
            return CompletableFuture.supplyAsync(this.theAltening::getAccountData);
        }

        public CompletableFuture<Boolean> isPrivate(final String v1) {
            return CompletableFuture.supplyAsync(() -> this.theAltening.isPrivate(v1));
        }

        public CompletableFuture<Boolean> isFavorite(final String v1) {
            return CompletableFuture.supplyAsync(() -> this.theAltening.isFavorite(v1));
        }
    }
}
