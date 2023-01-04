package cz.skodape.hdt.model.transformation;

import cz.skodape.hdt.model.navigation.PathItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Value selected from source.
 */
public class KindSelect implements KindPrimitive.Value {

    public List<PathItem> path = new ArrayList<>();

}
