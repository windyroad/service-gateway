package au.com.windyroad.hateoas.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import au.com.windyroad.hateoas.server.serialization.MessageSourceAwareSerializer;

abstract public class Titled {

    private Set<String> natures = new HashSet<>();

    @Nullable
    String title = null;

    public Titled() {
        // Nature natureAnnotation =
        // this.getClass().getAnnotation(Nature.class);
        // if (natureAnnotation != null) {
        // Collections.addAll(natures, natureAnnotation.value());
        // }
        // natures.add(this.getClass().getSimpleName());
    }

    public Titled(String title) {
        this();

        // Label titleAnnotation = this.getClass().getAnnotation(Label.class);
        // if (titleAnnotation != null) {
        // setTitle(titleAnnotation.value(), args);
        // }
        this.title = title;
    }

    void setTitle(String template, String... args) {
        title = interpolate(template, args);
    }

    private String interpolate(String value, String... args) {
        if (args.length == 0) {
            return value;
        } else {
            Pattern patt = Pattern.compile("\\{(.*?)\\}");
            Matcher m = patt.matcher(value);
            StringBuffer sb = new StringBuffer(value.length());
            for (int i = 0; m.find(); ++i) {
                String code = m.group(1);
                m.appendReplacement(sb, Matcher.quoteReplacement(args[i]));
            }
            m.appendTail(sb);
            return sb.toString();
        }
    }

    public Titled(Set<String> natures, String title) {
        this.natures = natures;
        this.title = title;
    }

    /**
     * @return the natures
     */
    @JsonProperty("class")
    public Set<String> getNatures() {
        return natures;
    }

    /**
     * @param natures
     *            the natures to set
     */
    public void setNatures(Set<String> natures) {
        this.natures = natures;
    }

    /**
     * @return the label
     */
    @JsonSerialize(using = MessageSourceAwareSerializer.class)
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the label to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(String title, Map<String, String> context) {
        this.title = title;
    }

    public boolean hasNature(String nature) {
        return this.getNatures().contains(nature);
    }

}
