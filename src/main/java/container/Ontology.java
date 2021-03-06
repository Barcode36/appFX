package container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import model.AttributeValue;
import util.StringUtil;

// reading the Cell Type Ontology from a file
// paper is at:  http://www.ncbi.nlm.nih.gov/pmc/articles/PMC551541
// file downloaded from https://bioportal.bioontology.org/ontologies/CL

// 15 March 2016  Adam Treister
// https://github.com/AdamStuart/


public class Ontology
{
	List<Onterm> terms = new ArrayList<Onterm>();
	Map<String, Onterm> map = new HashMap<String, Onterm>();
	TreeItem<Onterm> root;
	
	
	public Ontology()
	{
		
	}
	public TreeItem<Onterm> createTree()
	{
		Onterm top = new Onterm("root");
		top.setName("Cell Type Ontology");
		root = new TreeItem<Onterm>(top);
		root.setExpanded(true);
		for (Onterm term : terms)
		{
			List<AttributeValue> ids = term.getProperties("is_a");
			for (AttributeValue kv : ids)
			{
				String id = StringUtil.firstWord(kv.getValue());
				Onterm parent = map.get(id);
				if (parent != null)
					parent.addChild(term);
			}
		}
		Onterm cell = map.get("CL:0000000");
		if (cell != null)
			traverse(root, cell);
		
		return root;
	}
	
	// recursive tree traversal core
	private void traverse(TreeItem<Onterm> parent, Onterm term)
	{
		TreeItem<Onterm> self = new TreeItem<Onterm>(term);
		Label label = new Label("?");
		self.setGraphic(label);
		Tooltip.install(label, new Tooltip(term.getDescriptor()));
		self.setExpanded(true);
		parent.getChildren().add(self);
		for (Onterm subterm : term.getChildren())
			traverse(self,subterm);
	}
	//--------------------------------------------------------------
	public TreeItem<Onterm> createShallowTree()
	{
		Onterm top = new Onterm("root");
		top.setName("Cell Type Ontology");
		TreeItem<Onterm> root = new TreeItem<Onterm>(top);
		
		root.getChildren().addAll(
			terms.stream()
				.filter(term -> term.getId().startsWith("CL:"))
	//			.filter(term -> term.getProperties("is_a").size() <= 6)
				.map(term -> new TreeItem<Onterm>(term))
				.collect(Collectors.toList()));
		return root;
	}
	
	//--------------------------------------------------------------
	public String createSIF(String filterByPrefix)
	{
		StringBuilder builder = new StringBuilder();
		for (Onterm term : terms)
		{
			if (term.getId().startsWith(filterByPrefix))
			{
				List<AttributeValue> ids = term.getProperties("is_a");
				for (AttributeValue kv : ids)
				{
					String id = StringUtil.firstWord(kv.getValue());
					Onterm parent = map.get(id);
					if (parent != null)
						builder.append(term.getId()).append("\tis_a\t").append(parent.getId()).append("\n");
				}
			}
		}
		return builder.toString();
	}
	//--------------------------------------------------------------
	public void addTerm(Onterm t)		
	{ 
		String name = t.getName();
		if (name != null && !name.toUpperCase().startsWith("OBSOLETE"))
		{
			terms.add(t);	
			map.put(t.getId(), t);
		}
	}
	//--------------------------------------------------------------
	// TODO crude debug message to report by nParents
	public void dump()
	{
		int orphans = 0, singleParents = 0; 
		int doubleParents = 0, multiParents = 0; 
		int tripleParents = 0, quadParents = 0; 
		
		for (Onterm term : terms)
			if (term.getId().startsWith("CL"))
			{
				List<AttributeValue> parents = term.getProperties("is_a");
				int nParents = parents.size();
				
				if (nParents == 0)	orphans++;
				else if (nParents == 1)	singleParents++;
				else if (nParents == 2)	doubleParents++;
				else if (nParents == 3)	tripleParents++;
				else if (nParents == 4)	quadParents++;
				else multiParents++;
//				term.dump();
			}
		System.out.println("Parent Count: " + orphans + " / " + singleParents + " / " + doubleParents + 
				" / " + tripleParents + " / " + quadParents + " / " + multiParents );
		
	}
}
