package chart.treemap;

import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author Tadas Subonis <tadas.subonis@gmail.com>
 */
public interface Item extends Comparable<Item> {
	
    Object getId();
    double getAmount();
    String getLabel();
    boolean isContainer();
    List<Item> getItems();
}
