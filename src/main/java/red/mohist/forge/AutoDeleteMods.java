package red.mohist.forge;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import red.mohist.util.FindClassInJar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import red.mohist.util.i18n.Message;

public class AutoDeleteMods {

    public static void jar() throws Exception {
        System.out.println(Message.getString("update.mods"));
        String libDir = "mods";
        JsonElement root = null;
        URLConnection request;
        try {
            request = new URL("https://shawiizz.github.io/mods.json").openConnection();
            request.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            request.connect();
            root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
        } catch (JsonIOException | JsonSyntaxException | IOException | NullPointerException ignored) {
        }

        for (String classname : new ArrayList<>(Arrays.asList(root.getAsJsonObject().get("list").toString().split(",")))) {
            classname = classname.replaceAll("\\.", "/") + ".class";

            FindClassInJar ins = new FindClassInJar(libDir, classname);
            ins.checkDirectory(libDir);
        }
    }
}