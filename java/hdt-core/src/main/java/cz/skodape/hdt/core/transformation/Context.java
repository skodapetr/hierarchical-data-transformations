package cz.skodape.hdt.core.transformation;

import cz.skodape.hdt.core.output.Output;
import cz.skodape.hdt.core.source.BaseSource;
import cz.skodape.hdt.model.TransformationFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class Context {

    public TransformationFile transformation;;

    public Map<String, BaseSource> sources = new HashMap<>();

    /**
     * Store path to currently node the is currently being processed.
     */
    protected final Stack<Step> path = new Stack<>();

    public Output output;

}
