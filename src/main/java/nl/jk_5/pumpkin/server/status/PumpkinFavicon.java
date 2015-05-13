package nl.jk_5.pumpkin.server.status;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import nl.jk_5.pumpkin.api.status.Favicon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class PumpkinFavicon implements Favicon {

    private final String encoded;
    private final BufferedImage decoded;

    public PumpkinFavicon(BufferedImage decoded) throws IOException {
        this.decoded = checkNotNull(decoded, "decoded");
        this.encoded = encode(decoded);
    }

    public PumpkinFavicon(String encoded) throws IOException {
        this.encoded = checkNotNull(encoded, "encoded");
        this.decoded = decode(encoded);
    }

    public String getEncoded() {
        return this.encoded;
    }

    @Override
    public BufferedImage getImage() {
        return this.decoded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PumpkinFavicon)) {
            return false;
        }

        PumpkinFavicon that = (PumpkinFavicon) o;
        return Objects.equal(this.encoded, that.encoded);

    }

    @Override
    public int hashCode() {
        return this.encoded.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.decoded)
                .toString();
    }

    public static Favicon load(String raw) throws IOException {
        return new PumpkinFavicon(raw);
    }

    public static Favicon load(File file) throws IOException {
        return load(ImageIO.read(file));
    }

    public static Favicon load(URL url) throws IOException {
        return load(ImageIO.read(url));
    }

    public static Favicon load(InputStream in) throws IOException {
        return load(ImageIO.read(in));
    }

    public static Favicon load(BufferedImage image) throws IOException {
        return new PumpkinFavicon(image);
    }

    private static final String FAVICON_PREFIX = "data:image/png;base64,";

    private static String encode(BufferedImage favicon) throws IOException {
        checkArgument(favicon.getWidth() == 64, "favicon must be 64 pixels wide");
        checkArgument(favicon.getHeight() == 64, "favicon must be 64 pixels high");

        ByteBuf buf = Unpooled.buffer();
        try {
            ImageIO.write(favicon, "PNG", new ByteBufOutputStream(buf));
            ByteBuf base64 = Base64.encode(buf);
            try {
                return FAVICON_PREFIX + base64.toString(Charsets.UTF_8);
            } finally {
                base64.release();
            }
        } finally {
            buf.release();
        }
    }

    private static BufferedImage decode(String encoded) throws IOException {
        checkArgument(encoded.startsWith(FAVICON_PREFIX), "Unknown favicon format");
        ByteBuf base64 = Unpooled.copiedBuffer(encoded.substring(FAVICON_PREFIX.length()), Charsets.UTF_8);
        try {
            ByteBuf buf = Base64.decode(base64);
            try {
                BufferedImage result = ImageIO.read(new ByteBufInputStream(buf));
                checkState(result.getWidth() == 64, "favicon must be 64 pixels wide");
                checkState(result.getHeight() == 64, "favicon must be 64 pixels high");
                return result;
            } finally {
                buf.release();
            }
        } finally {
            base64.release();
        }
    }
}
