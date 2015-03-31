package nl.jk_5.pumpkin.server.permission.api;

import com.google.common.collect.ForwardingMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class PermissionList extends ForwardingMap<String, String> {

    private static final Logger logger = LogManager.getLogger();
    private static final String PERMISSION_ASTERIX = "*";
    private static final String PERMISSION_FALSE = "false";
    private static final String PERMISSION_TRUE = "true";

    private final Map<String, String> underlying;

    public PermissionList() {
        this(new HashMap<String, String>());
    }

    public PermissionList(Map<String, String> underlying) {
        this.underlying = underlying;
    }

    public void dump(){
        StringBuilder builder = new StringBuilder(this.size() * 5);
        builder.append("PermissionList entries:");
        for (Entry<String, String> entry : this.entrySet()) {
            builder.append('\n');
            if(entry.getValue().equals(PERMISSION_TRUE)){
                builder.append(entry.getKey());
            }else if(entry.getValue().equals(PERMISSION_FALSE)){
                builder.append('-').append(entry.getValue());
            }else{
                builder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        logger.info(builder.toString());
    }

    @Override
    protected Map<String, String> delegate() {
        return this.underlying;
    }
}
