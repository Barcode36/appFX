package chart.flexiPie;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class AppPies extends Application 
{
    

    public static void main(final String[] args) {
        Application.launch(args);
    }
    static private String FXML = "";

    @Override public 
  void start(Stage stage) throws Exception 
    {
    	me = this;
    FXMLLoader fxmlLoader = new FXMLLoader();
    URL url = getClass().getResource(FXML + "PieChart.fxml");
    fxmlLoader.setLocation(url);
    AnchorPane appPane = fxmlLoader.load();
    
    Scene scene = new Scene(appPane, 1000, 800);
    stage.setScene(scene);
    stage.show();
  }

 static public AppPies getInstance()	{ return me;	}
 static private AppPies me;
 static private Stage theStage;
 
 public Stage getStage() { return theStage;  }
     
}