package publish;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import util.FileUtil;
import util.StringUtil;
import xml.XMLTreeItem;

public class EDLParsingHelper
{
	public EDLParsingHelper(TreeTableView<Node> xmlTree)
	{
		TreeTableColumn<Node, String> nameColumn = new TreeTableColumn<Node, String>("Name");
		nameColumn.setPrefWidth(500);
		TreeTableColumn<Node, String> idCol = new TreeTableColumn<Node, String>("Id");
		idCol.setPrefWidth(100);
		xmlTree.getColumns().setAll(new TreeTableColumn[] {nameColumn, idCol});

		nameColumn.setCellValueFactory(p ->
		{
			Node f = p.getValue().getValue();
			String text = "error";
			if (f != null)
				text = getNameCellText(f);
			return new ReadOnlyObjectWrapper<String>(text);
		});
		
		idCol.setCellValueFactory(p ->
		{
			Node f = p.getValue().getValue();
			String text = "error";
			if (f != null)
				text = getIDCellText(f);
			return new ReadOnlyObjectWrapper<String>(text);
		});
	}
	//-------------------------------------------------------------------
	// EDL specific hacks to make XML more human readable
	// Map terms, and selectively replace the node name with 
	// 		the node's type or name attributes
	
	String  getNameCellText(Node f)	
	{
		String text = "";
		text = lookup(f.getNodeName());
		
		if (text.equals("Object") || text.equals("Method")|| text.equals("Machine"))
		{
			Node n = f.getAttributes().getNamedItem("Type");
			if (n != null)
				text = n.getTextContent();
		} else
		{
			Node n = f.getAttributes().getNamedItem("Name");
			if (n != null)
				text = n.getTextContent();
		}
		text = lookup(text);
		if (text.length() > 100)
			text = text.substring(0,100) + "...";
		return text;
	}
	
	String  getIDCellText(Node f)	
	{
		String text = "";
		if (f.getNodeName().equals("Restriction"))
		{
			Node n = f.getAttributes().getNamedItem("MinOccur");
			if (n != null) 	text = n.getTextContent();
			n = f.getAttributes().getNamedItem("MaxOccur");
			if (n != null) 	text += ", " + n.getTextContent();
		}
		else
		{
			text = f.getTextContent();
			if (text.length() > 64)
				text = text.substring(0,64) + "...";
			
			Node n = f.getAttributes().getNamedItem("UID");
			if (n != null)	text = n.getTextContent();
			n = f.getAttributes().getNamedItem("Value");
			if (n != null)
			{
				text = n.getTextContent();
				n = f.getAttributes().getNamedItem("Unit");
				if (n != null) text += " " + n.getTextContent();
			}
		}
		return text;
	}
	
	//-------------------------------------------------------------------
	static String lookup(String orig)
	{
		if (orig == null) return "";
		String val = lookupMap.get(orig);
		return (val == null) ? orig : val;
	}
	//-------------------------------------------------------------------
	static Map<String,String> lookupMap = new HashMap<String,String>();
	
	public static String[] strs = new String[] { "Filter Samples", "Organize Files", "Image Processing", "Edge Detection", "Feature Recognition", "Quantification", "Parametric Normalization", "Labeling"};
	public static String[] stepList = new String[] { "3Read Zip File", "0Check Manifest", "3Queue Files", "3Read CSV Files", "3Ranges Set", "4Distributions Set", "3Gutter Gates Applied", "4Statistics Generated", "3Smoothed", "3Distriubtions Regenerated", "2Normalized", "3Baseline Peak Found", "2Populations Gated"};
	public static String[] interrog = new String[] { "Activation", "Stimulation", "Memory", "Expression", "Regulation", "Promotion", "Inhibition", "Apoptosis" };
	public static String[] viz = new String[] { "QC Montage", "Stats Panel", "Backgating", "Correlation", "Heat Map", "Hover Plot", "Drill Down Chart", "Tree Map", "Anova", "Cytoscape", "VISNE", "SPADE"};

	void setupDictionary()
	{
		lookupMap.put("Inputobjects", "Input");
		lookupMap.put("Outputobjects", "Output");
		lookupMap.put("PrimaryContainer", "Container");
		lookupMap.put("ObjRef", "Reference");
		lookupMap.put("Obj", "Object");
		lookupMap.put("SpecificParameter", "Parameter");
		lookupMap.put("SpecificParameters", "Parameters");
		lookupMap.put("ObjectConnector", "Connection");
		lookupMap.put("EncapsulatedObjects", "Objects");
		lookupMap.put("EncapsulatedMethods", "Steps");
		lookupMap.put("EncapsulatedObjectsRef", "References");
		lookupMap.put("EncapsulatedMethodsRef", "Method References");
		lookupMap.put("Meth", "Method");
		lookupMap.put("MethRef", "Method Reference");
		lookupMap.put("MethodHistory", "History");
	}
	//--------------------------------------------------------------------------------
	static private File findFile(File[] dir, String idName)
	{
		for (File child : dir)
			if (StringUtil.chopExtension(child.getName()).equals(idName))
				return child;
		return null;
	}
	
	static public String[] dims = new String[]{"CD3", "CD25","CD4", "CD19", "CD38", "CD39", "CD161", "CD27" };
	static String[] suppressNames = new String[]{ "SpecificParameters", "Environment", "Machine", "MethodHistory"};		// close the disclosure triangle, as they may be big

	//--------------------------------------------------------------------------------

	//--------------------------------------------------------------------------------
	
	static private void addScanJobsDirectory(File f,ListView<ScanJob> scans)
	{
		for (File kid : f.listFiles())
		{
			if (FileUtil.isImageFile(kid))
			{
				String id = kid.getParentFile().getParentFile().getParentFile().getName();
				scans.getItems().add(new ScanJob(id, kid));
			}
			else if (kid.isDirectory())
				addScanJobsDirectory(kid, scans);
		}
	}

		//--------------------------------------------------------------------------------
	static private void addSegmentsDirectory(File f,ListView<Segment> segments)
	{
		try
		{
			File[] kids = f.listFiles();
			for (File kid : kids)
			{
				String id = kid.getName();
				if (kid.isDirectory())
				{
					File[] grandkids = kid.listFiles();
					for (File gkid : grandkids)
						if (FileUtil.isCSV(gkid))
						{
							Segment seg = new Segment(id, gkid);	// read the file, build the table
							if (seg.getData() != null)
								segments.getItems().add(seg);	
						}
				}
			}
		}
		catch (Exception e) {			e.printStackTrace();		}
	}

	//--------------------------------------------------------------------------------
	public static void addCSVFilesToSegments(File fileOrDir, ListView<Segment> segments)
	{
		if (fileOrDir.isDirectory())
		{
			File[] kids = fileOrDir.listFiles();
			for (File kid : kids)
				if (FileUtil.isCSV(kid))
				{
					Segment seg = new Segment(kid.getName(), kid);	// read the file, build the table
					if (seg.getData() != null)
						segments.getItems().add(seg);	
				}
		}
		else
		{
			Segment seg = new Segment(fileOrDir.getName(), fileOrDir);	// read the file, build the table
			if (seg.getData() != null)
				segments.getItems().add(seg);	
		}
	}

	public static ObservableList<String> organismList = FXCollections.observableArrayList("Human", "Mouse", "Cow", "Dog", "Pig", "Yeast", "More...");
	public static ObservableList<String> speciesList = FXCollections.observableArrayList("Mouse", "Human", "Yeast", "More...");
	public static ObservableList<String> cellTypes = FXCollections.observableArrayList("Jurkat Cells", "HEK293 Cells", "T Cells", "B Cells", "NK Cells", "More...");
	public static ObservableList<String> technologyList = FXCollections.observableArrayList("APMS", "ChipCytometry", "PCR", "HPLC", "More...");


	public static ObservableList<SOPLink> getSOPLinks()
	{
		ObservableList<SOPLink> links = FXCollections.observableArrayList();
		links.add(new SOPLink("https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4332878", "Morris, Nature Protocols, 2014."));
		links.add(new SOPLink("https://www.ncbi.nlm.nih.gov/pubmed/18653769", "Protein Prospector"));
		
		links.add(new SOPLink("https://github.com/everschueren/mist", "MiST software"));
		links.add(new SOPLink("http://saint-apms.sourceforge.net/Main.html", "SAINT software"));
		links.add(new SOPLink("http://www.cytoscape.org", "Cytoscape software"));
//		links.add(new SOPLink("url", "label"));
		return links;
	}
}

//links.add(new SOPLink("http://chipcytometry.com/Blog/", "Chip Cytometry SOPs"));
//links.add(new SOPLink("http://www.protocol-online.org/prot/Cell_Biology/Flow_Cytometry__FCM_/", "FACS Protocols"));
//links.add(new SOPLink("https://www.thermofisher.com/us/en/home/references/"
//+ "protocols/cell-and-tissue-analysis/flow-cytometry-protocol.html", "ThermoFisher"));
