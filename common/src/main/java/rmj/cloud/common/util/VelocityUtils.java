package rmj.cloud.common.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

public class VelocityUtils {
    public static synchronized String velocityToString(String fileName, Map<String, String> model, String vmPath) {
        Properties p = new Properties();
        p.setProperty(Velocity.RESOURCE_LOADER, "file");
        p.setProperty("file.resource.loader.description", " Velocity File Resource Loader");
        p.setProperty("file.resource.loader.path", vmPath);
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        Velocity.init(p);
        Template template = Velocity.getTemplate(fileName, "UTF-8");

        VelocityContext context = new VelocityContext();
        if (model != null) {
            model.entrySet().stream().forEach(entry -> context.put(entry.getKey(), entry.getValue()));
        }
        StringWriter writer = new StringWriter();
        if (context != null)
            template.merge(context, writer);
        return writer.toString();
    }

}
