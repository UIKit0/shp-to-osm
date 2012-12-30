package com.yellowbkpk.geo.shp;

import java.util.List;

import com.yellowbkpk.osm.primitive.Tag;

public class Rule {

    private String type;
    private String srcKey;
    private String srcValue;
    private boolean useOriginalValue = false;
    private String targetKey;
    private String targetValue;

    /**
     * 
     */
    public Rule(String type, String srcKey, String srcValue, String targetKey, String targetValue) {
        this.type = type;
        this.srcKey = srcKey;
        this.srcValue = srcValue;
        this.targetKey = targetKey;
        this.targetValue = targetValue;
    }

    /**
     * Constructor that uses the original shapefile's value as the value of the
     * tag.
     * 
     */
    public Rule(String type, String srcKey, String srcValue, String targetKey) {
        this(type, srcKey, srcValue, targetKey, null);
        this.useOriginalValue = true;
    }

    public String toString() {
        return type + ": " + srcKey + "=" + srcValue + " => " + targetKey + "=" + targetValue;
    }

    public Tag createTag(String srcKey, String originalValue) {
        String key;
        String value;

        // Only create the tag if their key matches ours
        if (srcKey.equals(this.srcKey)) {

            // If they list a source key value, then only create the tag if their key
            // value matches ours
            if (srcValue != null) {
                if (originalValue.equals(this.srcValue)) {
                    key = targetKey;

                    if (useOriginalValue) {
                        value = originalValue;
                    } else {
                        value = targetValue;
                    }

                    return new Tag(key, value);
                }
            } else {
                // If they didn't specify a value for the source key, then we
                // always apply the tag

                if (useOriginalValue) {
                    value = originalValue;
                } else {
                    value = targetValue;
                }

                return new Tag(targetKey, value);
            }
        }

        return null;
    }

	public Rule applyVariables(List<Tag> vars) {
		if ( vars.isEmpty() ) return this;
		Rule rule;
		
		if ( this .useOriginalValue )
			rule = new Rule(this.type, this.srcKey, this.srcValue, this.targetKey);
		else
			rule = new Rule(this.type, this.srcKey, this.srcValue, this.targetKey, this.targetValue);
		
		for( Tag var : vars)
		{
			String target = "${" + var.getKey() + "}";
			String replacement = var.getValue();
			
			rule.srcKey = replace( rule.srcKey, target, replacement);
			rule.srcValue = replace(rule.srcValue, target, replacement);
			rule.targetKey = replace(rule.targetKey, target, replacement);
			rule.targetValue = replace(rule.targetValue, target, replacement);
		}
		return rule;
	}
	
	private static String replace(String s, String target, String replacement)
	{
		if ( s == null ) return null;
		return s.replace(target, replacement);
	}
}
