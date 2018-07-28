package lab3;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import textproc.GeneralWordCounter;
import textproc.TextProcessor;

public class BookReaderController extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 500, 500);
		primaryStage.setTitle("BookReader");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		
		Scanner scan = new Scanner(new File("undantagsord.txt"));
		Set<String> stopwords = new HashSet<String>();
		while(scan.hasNext()) {
			stopwords.add(scan.next());
		}
		
		GeneralWordCounter counter = new GeneralWordCounter(stopwords);
		scan.close();

		Scanner s = new Scanner(new File("nilsholg.txt"));
		s.useDelimiter("(\\s|,|\\.|:|;|!|\\?|'|\\\")+"); // se handledning

		while (s.hasNext()) {
			String word = s.next().toLowerCase();
			counter.process(word);
		}
		s.close();
		//counter.report();
		
		ObservableList<Map.Entry<String, Integer>> words = FXCollections.observableArrayList(counter.getWords());
		ListView<Map.Entry<String, Integer>> listView = new ListView<Map.Entry<String, Integer>>(words);
		root.setCenter(listView);
		
		HBox hbox = new HBox();
		
		Button freq = new Button("Frequency");
		Button alph = new Button("Alphabetical");
		
		freq.setOnAction(event -> {
			words.sort((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)->o2.getValue()-o1.getValue());
		});
		
		alph.setOnAction(event -> {
//			words.sort((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> {
//				o1.getKey().compareTo(o2.getKey());
//			});
			words.sort(new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});
		});
		
		ToggleGroup group = new ToggleGroup();
		RadioButton alpha = new RadioButton("Alphabetical");
		alpha.setToggleGroup(group);
		alpha.setOnAction(event -> {
			words.sort(new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});
		});
		alpha.fire();
		alpha.setSelected(true);
		RadioButton frequ = new RadioButton("Frequency");
		frequ.setToggleGroup(group);
		frequ.setOnAction(event -> {
			if (group.getSelectedToggle().equals(frequ));
				words.sort((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)->o2.getValue()-o1.getValue());
		});
		
		
		
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Word not found");
		alert.setHeaderText("The searched for word does not appear in the text");
		
		TextField field =  new TextField();
		Button searchButton = new Button("Find");
		field.setOnAction(event -> searchButton.fire());
		searchButton.setOnAction(event -> {
			boolean found = false;
			for(Map.Entry<String, Integer> o: words) {
				String entry = field.getText();
				entry = entry.trim();
				entry = entry.toLowerCase();
				if(o.getKey().equals(entry)) {
					listView.scrollTo(o);
					listView.getSelectionModel().select(o);
					found = true;
					break;
				}
			}
			if (!found) {
				alert.show();
			}
		});
		
		hbox.getChildren().addAll(alpha, frequ, field, searchButton);
		hbox.setHgrow(field, Priority.ALWAYS);
		
		root.setBottom(hbox);
		
	}

	public static void main(String[] args) {
		Application.launch(args);

	}

}
