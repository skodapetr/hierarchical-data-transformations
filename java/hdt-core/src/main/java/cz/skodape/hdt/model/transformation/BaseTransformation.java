package cz.skodape.hdt.model.transformation;

import cz.skodape.hdt.model.navigation.BaseNavigation;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for transformation templates. This class contain common
 * configuration that can be used in every object.
 */
public abstract class BaseTransformation {

    public String source;

    public List<BaseNavigation> navigation = new ArrayList<>();

}
