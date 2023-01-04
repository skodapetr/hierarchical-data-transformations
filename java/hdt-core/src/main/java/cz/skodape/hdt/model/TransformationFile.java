package cz.skodape.hdt.model;

import cz.skodape.hdt.model.transformation.BaseTransformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Complete transformation configuration.
 */
public class TransformationFile {

    public Map<String, TransformationSource> sources = new HashMap<>();

    public BaseTransformation transformation;

    public TransformationTarget target;

}
