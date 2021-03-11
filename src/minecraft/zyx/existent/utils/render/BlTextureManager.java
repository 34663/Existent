package zyx.existent.utils.render;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.Callable;

public class BlTextureManager {
    private static final Logger logger = LogManager.getLogger();
    private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
    private final Map<ResourceLocation, ITextureObject> mipMapTextures = Maps.<ResourceLocation, ITextureObject>newHashMap();
    private IResourceManager theResourceManager;

    public void bindTextureMipmapped(ResourceLocation resourceLocation) {
        ITextureObject itextureobject = (ITextureObject) this.mipMapTextures.get(resourceLocation);

        if (itextureobject == null) {
            itextureobject = new SimpleTexture(resourceLocation);
            this.loadTextureMipMap(resourceLocation, itextureobject);
        }

        GlStateManager.bindTexture(itextureobject.getGlTextureId());
    }

    public boolean loadTextureMipMap(ResourceLocation resourceLocation, final ITextureObject textureObject) {
        boolean flag = true;

        try {
            ((ITextureObject) textureObject).loadTexture(this.theResourceManager);
        } catch (IOException ioexception) {
            logger.warn((String) ("Failed to load texture: " + resourceLocation), (Throwable) ioexception);
            Object textObject1 = TextureUtil.MISSING_TEXTURE;
            this.mipMapTextures.put(resourceLocation, (ITextureObject) textureObject);
            flag = false;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
            crashreportcategory.addCrashSection("Resource location", resourceLocation);
            crashreportcategory.addCrashSectionThrowable("Texture object class", new Throwable() {
                private static final String __OBFID = "CL_00001065";

                public String call() {
                    return textureObject.getClass().getName();
                }
            });
            throw new ReportedException(crashreport);
        }

        this.mipMapTextures.put(resourceLocation, (ITextureObject) textureObject);
        return flag;
    }


}
