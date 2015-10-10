package database.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import task.IDBTable;
import util.DBUtil;
import util.FormsUtil;

public class DBCitation implements IDBTable
{
	static String CITATION_FORM = "Citation";
	Region form;

	@Override public Region getForm()
	{
		if (form == null)
		{
			VBox container = new VBox(6);
			container.setPadding(new Insets(20));
			HBox idBox = FormsUtil.formbox( "ID", "id", 50);
			HBox author = FormsUtil.promptedText( "Primary Author", "author", 150);
			HBox title = FormsUtil.promptedText( "Title", "title", 350);
			HBox pub = FormsUtil.promptedText( "Published by", "pub", 350);
			HBox date = FormsUtil.promptedText( "Date", "date", 100);
			HBox isbn = FormsUtil.promptedText( "ISBN", "isbn", 150);
			HBox also = FormsUtil.promptedText( "Secondary Authors", "also", 350);
	
			container.getChildren().addAll(idBox, author, title, date, isbn, pub, also);
			form =container;
		}
		return form;
	}

	@Override public String getTableName()	{		return CITATION_FORM;	}

	@Override public String getSchema()
	{
		 return DBUtil.createSchema("Citation", "author _STR, \n title _STR,\n pub _STR,\n date TIMESTAMP(8),\nisbn _SHORT,\n also _STR");
	}

	@Override public ObservableList<String> getFieldList()
	{
		ObservableList<String> fields = FXCollections.observableArrayList();
		fields.addAll("id", "author", "title", "pub", "date", "url", "isbn", "also");
		return fields;
	}

	@Override public void install(ObservableMap<String, String> fields)
	{
		DBUtil.install(form, fields);
	
	}

	@Override public void extract(ObservableMap<String, String> fields)
	{
		DBUtil.extract(form, fields);
	}


}