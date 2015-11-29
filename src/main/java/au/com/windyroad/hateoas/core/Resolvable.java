package au.com.windyroad.hateoas.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import au.com.windyroad.hateoas.annotations.Label;
import au.com.windyroad.hateoas.annotations.Nature;
import au.com.windyroad.hateoas.server.serialization.MessageSourceAwareSerializer;

abstract public class Resolvable {

    private Set<String> natures = new HashSet<>();

    @Nullable
    String label = null;

    public Resolvable(String... args) {
        Nature natureAnnotation = this.getClass().getAnnotation(Nature.class);
        if (natureAnnotation != null) {
            Collections.addAll(natures, natureAnnotation.value());
        }
        natures.add(this.getClass().getSimpleName());

        Label titleAnnotation = this.getClass().getAnnotation(Label.class);
        if (titleAnnotation != null) {
            label = interpolate(titleAnnotation.value(), args);
        }
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

    public Resolvable(Set<String> natures, String label) {
        this.natures = natures;
        this.label = label;
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
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public void setLabel(String label, Map<String, String> context) {
        this.label = label;
    }

}
