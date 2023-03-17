package builder;

import model.pattern.Pattern;

public class PatternAbstracter {
    int _threshold = 0;

    public PatternAbstracter(int thr) {
        _threshold = thr;
    }
    public Pattern abstractPattern(Pattern pat) {
        collectAttributes(pat);
        voteAttributes(pat);
        return pat;
    }

    /**
     * collect feature values for each element in a pattern
     * @param pat
     */
    private void collectAttributes(Pattern pat) {
        // node attributes

        // edge attributes
    }

    /**
     * determine which features to keep
     */
    private void voteAttributes(Pattern pat) {
        // node

        // edge
    }
    
    
}
