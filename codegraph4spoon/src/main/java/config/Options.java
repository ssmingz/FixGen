package config;

import config.pojo.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
public class Options {
    private static final Logger logger = LoggerFactory.getLogger(Options.class);

    private static final String OPTIONS_FILE = "option.yml";

    public static Option parse() {
        Yaml yaml = new Yaml();
        return yaml.loadAs(Options.class.getClassLoader().getResourceAsStream(OPTIONS_FILE), Option.class);
    }
}
