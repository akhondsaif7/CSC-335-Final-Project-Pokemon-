// inspiration image: https://miro.medium.com/v2/resize:fit:640/format:webp/1*32fIBxrW1-k7qoyZJovi7A.jpeg

// UI SIZE:
// Width: 480
// Height: 320

package view;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import mons.*;

import controller.CombatController;
import controller.CombatController.ReadWriteServer;
//import controller.NetworkController;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import networking.Server;

/**
 * PokemonView builds and contains the logic for the UI. Its split into 3 sections, the Main Menu, Battle scene and Challenges. 
 * @author Matt Lagier
 * @author Bradley Adams
 */
@SuppressWarnings("deprecation")
public class PokemonView extends Application implements Observer {
	
	//Main Menu
	private VBox mainMenu = new VBox();
	private VBox multiplayerMenu = new VBox();
	private ImageView pokemonTitle;
	private ImageView backGround;
	private Scene mainMenuScene;
	
	//Battle Scene
	private AnchorPane screen = new AnchorPane();
	private VBox mainLayout = new VBox();
	private StackPane bottomBar = new StackPane();
	private VBox battleArea = new VBox();
	private Scene battleScene;
	
	//Battle Players
	private PokemonModel model = new PokemonModel();
	private Player p1 = model.getPlayer();
	private ChallengesHolder challenges = model.getChallenges();
	Player p2 = challenges.getChallengesAt(0).getEnemy();
	CombatController cc = new CombatController(p1, p2);
	Move[] moves = cc.getPlayerMoves(1);
	Mon[] mpMon = {new Altaria(), new Bibarel(), new Breloom(), new Bulbasaur(), new Feraligatr(), new Flygon(), new Lapras(), new Luxray(), new Rampardos(), new Swampert(), new Toxicroak(), new Typhlosion(), new Venusaur(), new Weavile()};
	
	//Level scene
	private Scene levelScene;
	private Pane levelMenuRoot = new Pane();
	private HBox levelMenu = new HBox();
	private VBox levelButtons = new VBox();
	private VBox levelPreview = new VBox(); 
	private Challenges selectedChallenge = challenges.getChallengesAt(0);
	private ImageView levelsBackground;
	
	//Team Preview Scene
	private Scene teamScene;
	private HBox teamPreview;
	private ToggleButton pokedexBttns[] = new ToggleButton[20];
	private ToggleButton teamBttns[] = new ToggleButton[6];
	
	//Multiplayer Scene
	private Scene multiplayerScene;
	private boolean hasP1Chosen = false;

	// Important UI elements stored here to allow for easy visibility changes
	private StackPane moveStack = new StackPane();																		// Fight/Bag/Pokemon menu
	private Text notificationText = new Text();																			// Text for notification Bar
	private StackPane attackPane = new StackPane();																		// Pokemon move bar
	private StackPane monSelect = new StackPane();																		// Pokemon selection bar
	private ArrayList<Text> playerMoves = new ArrayList<>();
	
	//Fonts
	private Font pokemonFont = Font.loadFont(getClass().getResourceAsStream("/media/mainmenu/pokemon_fire_red.ttf"), 20);
	private Font statsFont = Font.loadFont(getClass().getResourceAsStream("/media/mainmenu/pokemon_fire_red.ttf"), 15);

	
	// Player
	private Text battlePlayerPokeName = new Text(p1.getActiveMon().getName());							// Current player name
	private ProgressBar battlePlayerHealthBar = new ProgressBar();														// Current player health bar
	private Text battlePlayerHP = new Text(" HP: " + p1.getActiveMon().getCurrHp());									// Current player HP
	private GridPane movePane = new GridPane();																			// Current pokemon moves
	private ImageView playerSpriteView;
	
	//Networking
	private Server serv;
	private boolean toggledEndGameScreen = false;
	private Text pokeSlot1Name = new Text("-");
	private Text pokeSlot2Name = new Text("-");
	private Text pokeSlot3Name = new Text("-");
	
	//NPC
	Text battleNPCPokeName = new Text(p2.getActiveMon().getName());
	private ProgressBar battleNPCHealthBar = new ProgressBar();
	Text battleNPCHealthText = new Text("HP: " + p2.getActiveMon().getCurrHp());
	private ImageView player2SpriteView;

	// Colors
	Color pokePurple = Color.web("6D6482");
	Color pokeBlue = Color.web("294E68");
	Color pokeOrange = Color.web("CCA553");
	Color pokeWhite = Color.web("F8F8F8");
	Color pokeTan = Color.web("F8F9D6");
	
	Stage stage;

	@Override
	public void start(Stage stage) throws Exception {
		mainMenuScene = makeMainMenu();
		stage.setTitle("Pokemon Game - Main Menu");
        stage.setScene(mainMenuScene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(E -> {
        	stage.close();
            Platform.exit();
            System.exit(0);
		});
        this.stage = stage;
	}
	/**
	 * Creates the MainMenu scene
	 * @throws UnknownHostException 
	 */
	public Scene makeMainMenu() throws UnknownHostException 
	{
		Label titleLabel = new Label("CSC 335 Version");
		titleLabel.setFont(pokemonFont);
		Button storyButton = new Button("Story Mode");
		Button multiButton = new Button("Multiplayer");
		Button quitButton = new Button("Quit");
		storyButton.setFont(pokemonFont);
		multiButton.setFont(pokemonFont);
		quitButton.setFont(pokemonFont);
		Image pokemonTitleImage = new Image(getClass().getResourceAsStream("/media/mainmenu/PokemonLogo.png"));
		pokemonTitle = new ImageView();
		pokemonTitle.setImage(pokemonTitleImage);
		pokemonTitle.setPreserveRatio(true);
		pokemonTitle.setFitHeight(100);
		Image backGroundImage = new Image(getClass().getResourceAsStream("/media/mainmenu/BackgroundPokemon.png"));
		backGround = new ImageView();
		backGround.setImage(backGroundImage);
		//Title Animation
		ScaleTransition grow = new ScaleTransition(Duration.seconds(1.0), pokemonTitle);
		ScaleTransition shrink = new ScaleTransition(Duration.seconds(1.0), pokemonTitle);
		grow.setToX(1.25);
		grow.setToY(1.25);
		shrink.setToX(1);
		shrink.setToY(1);
		SequentialTransition loop = new SequentialTransition(grow, shrink);
		loop.setCycleCount(SequentialTransition.INDEFINITE);
		loop.play();
		
        mainMenu.setSpacing(10);
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.getChildren().addAll(pokemonTitle, titleLabel, storyButton, multiButton, quitButton);
        BackgroundImage backGroundImg = new BackgroundImage(backGroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(480, 320, false, false, false, true));
        Background backGround = new Background(backGroundImg);
        mainLayout.setBackground(backGround);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(mainMenu, bottomBar);
        Scene mainMenuScene = new Scene(mainLayout, 480, 320);
        storyButton.setOnAction(e -> {
            System.out.println("Story Mode clicked");
            cc.setMultiplayer(false);
            cc.setNetworkMultiplayer(false);
            mainLayout = new VBox();
            //battleScene = battleUITesting(stage);
            //stage.setScene(battleScene);
            levelScene = levelsUI();
            stage.setScene(levelScene);
        });

        multiButton.setOnAction(e -> {
            System.out.println("Multiplayer clicked");
            cc.setMultiplayer(true);
            cc.setNetworkMultiplayer(true);
            mainLayout = new VBox();
            Scene multiplayerScene;
            try {
				multiplayerScene = multiplayerMenu();
				stage.setScene(multiplayerScene);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
        });

        quitButton.setOnAction(e -> {
            stage.close();
            Platform.exit();
            System.exit(0);
        });
        return mainMenuScene;
	}
	/**
	 * Creates levelScene
	 * @return Scene levelScene
	 */
	public Scene levelsUI() 
	{
		cleanUI();
		Button lvlButtons[] = new Button[challenges.levelCount()];
		int count = 0;
		HBox rows = new HBox();
		for (int i = 0; i < challenges.levelCount(); i++) 
		{
			rows.setSpacing(10);
			rows.setAlignment(Pos.CENTER);
			if (count >= 3) 
			{
				levelButtons.getChildren().add(rows);
				rows = new HBox();
				count = 0;
			}
			count++;
			int selectedIndex = i;
			Button lvlBttn = new Button(challenges.getName(i));
			lvlBttn.setFont(pokemonFont);
			lvlBttn.setDisable(challenges.getChallengesAt(i).isLocked());
			lvlBttn.setOnAction(e -> {
				selectedChallenge = challenges.getChallengesAt(selectedIndex);
				levelMenuRoot.getChildren().remove(levelPreview);
				levelPreview = makePreview();
				levelPreview.setLayoutX(480 - levelPreview.prefWidth(-1) - 5);
				levelMenuRoot.getChildren().add(levelPreview);
	        });
			rows.getChildren().add(lvlBttn);
		}
		levelButtons.getChildren().add(rows);
		levelButtons.setLayoutX(30);
		levelButtons.setLayoutY(10);
		Button teamBttn = new Button("Edit/View Team");
		teamBttn.setFont(pokemonFont);
		teamBttn.setOnAction(e -> {
			makeTeamPreview();
			teamScene = makeTeamPreview();
            stage.setScene(teamScene);
        });
		teamBttn.setLayoutY(275);
		teamBttn.setLayoutX(10);
		levelButtons.setSpacing(20);
		levelButtons.setAlignment(Pos.CENTER);
		levelPreview = makePreview();
		levelPreview.setLayoutX(480 - levelPreview.prefWidth(-1) - 5);
		//levelMenu.getChildren().addAll(levelButtons);
		levelMenu.setAlignment(Pos.CENTER);
		levelMenu.setSpacing(50);
		levelMenu.setLayoutX(levelPreview.prefWidth(-1) + 5);
		levelMenuRoot.getChildren().addAll(levelButtons, teamBttn, levelPreview);
		Image img = new Image(getClass().getResourceAsStream("/media/mainmenu/levelsBackground.png"));
		levelsBackground = new ImageView();
		BackgroundImage backgroundImage = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(480, 320, false, false, false, false));
		Background background = new Background(backgroundImage);
		levelMenuRoot.setBackground(background);
		levelScene = new Scene(levelMenuRoot, 480, 320);
		if (model.isTimeToUnlock()) 
		{
			levelMenuRoot.getChildren().add(makeUnlocks());
		}
		return levelScene;
	}

	/**
	 * Recreates any UI elements so they don't have duplicates when
	 * remaking any scenes
	 */
	private void cleanUI() 
	{
		levelMenuRoot = new Pane();
		playerMoves = new ArrayList<>();
		mainMenu = new VBox();
		//Battle Scene
		screen = new AnchorPane();
		mainLayout = new VBox();
		bottomBar = new StackPane();
		battleArea = new VBox();
		//Level scene
		levelMenu = new HBox();
		levelButtons = new VBox();
		levelPreview = new VBox(); 
		// Important UI elements stored here to allow for easy visibility changes
		moveStack = new StackPane();																		// Fight/Bag/Pokemon menu
		notificationText = new Text();																			// Text for notification Bar
		attackPane = new StackPane();																		// Pokemon move bar
		monSelect = new StackPane();																		// Pokemon selection bar
		// Player
		battlePlayerPokeName = new Text(p1.getActiveMon().getName());							// Current player name
		battlePlayerHealthBar = new ProgressBar();														// Current player health bar
		battlePlayerHP = new Text(" HP: " + p1.getActiveMon().getCurrHp());									// Current player HP
		movePane = new GridPane();																			// Current pokemon moves
		pokeSlot1Name = new Text("-");
		pokeSlot2Name = new Text("-");
		pokeSlot3Name = new Text("-");
		//NPC
		battleNPCPokeName = new Text(p2.getActiveMon().getName());
		battleNPCHealthBar = new ProgressBar();
		battleNPCHealthText = new Text("HP: " + p2.getActiveMon().getCurrHp());
	}
	/**
	 * Creates Team Preview for Enemy on level
	 * @return Team preview as a VBox
	 */
	public VBox makePreview() 
	{
		Button selectLevel = new Button("Battle!");
		selectLevel.setOnAction(e -> {
			boolean temp = cc.getNetworkMultiplayer();
			int playerNum = cc.getPlayerID();
			setUpBattle();
			toggledEndGameScreen = false;
			cc.setPlayerID(playerNum);
			System.out.println("PlayerID: "+ playerNum);
			cc.setNetworkMultiplayer(temp);
			battleScene = battleUITesting(stage);
            stage.setScene(battleScene);
        });
		selectLevel.setFont(pokemonFont);
		Label previewLabel;
		levelPreview = new VBox();
		previewLabel = new Label(challenges.getName(selectedChallenge) + " Team Preview");
		previewLabel.setFont(pokemonFont);
		selectLevel.setDisable(false);
		VBox team = new VBox();
		p2 = selectedChallenge.getEnemy();
		HBox row = new HBox();
		int count = 0;
		for (int i = 0; i < 6; i++) 
		{
			if (count == 2) 
			{
				team.getChildren().add(row);
				row = new HBox();
				count = 0;
			}
			Image pokemonSprite;
			if (i >= p2.teamSize())
			{
				pokemonSprite = new Image(getClass().getResourceAsStream("/media/mainmenu/emptySlot.png"), 80, 80, false, false);
			}
			else 
			{
				pokemonSprite = new Image(getClass().getResourceAsStream(p2.getMonAt(i).getOppSprite()), 80, 80, false, false);
			}
			ImageView pokemonSpriteView = new ImageView();
			pokemonSpriteView.setImage(pokemonSprite);
			count++;
			row.getChildren().add(pokemonSpriteView);
		}
		if (count != 0) 
		{
			team.getChildren().add(row);
		}
		levelPreview.getChildren().addAll(previewLabel, team, selectLevel);
		levelPreview.setAlignment(Pos.CENTER);
		return levelPreview;
	}
	/**
	 * Creates unlocks Pane for unlocking new Pokemon from winning a Challenge/Level
	 * @return Pane unlocksPane
	 */
	public Pane makeUnlocks() 
	{
		Label title = new Label("Congrats, Please take a pokemon as a prize");
		title.setFont(pokemonFont);
		VBox team = new VBox();
		Mon[] unlocks = model.unlockTeam();
		HBox row = new HBox();
		int count = 0;
		Pane pane = new Pane();
		for (int i = 0; i < 6; i++) 
		{
			boolean isValid = false;
			if (count == 3) 
			{
				team.getChildren().add(row);
				row = new HBox();
				count = 0;
			}
			Image pokemonSprite;
			if (i >= unlocks.length)
			{
				pokemonSprite = new Image(getClass().getResourceAsStream("/media/mainmenu/emptySlot.png"), 80, 80, false, false);
				isValid = false;
			}
			else 
			{
				pokemonSprite = new Image(getClass().getResourceAsStream(unlocks[i].getOppSprite()), 80, 80, false, false);
				if (!model.getPlayer().isInDex(unlocks[i])) 
				{
					isValid = true;
				}
			}
			final int tempI = i;
			ImageView pokemonSpriteView = new ImageView();
			pokemonSpriteView.setImage(pokemonSprite);
			Button unlockBttn = new Button();
			unlockBttn.setGraphic(pokemonSpriteView);
			unlockBttn.setDisable(!isValid);
			unlockBttn.setOnAction(e -> {
				model.getPlayer().addToDex(unlocks[tempI]);
				model.unlocked();
				cleanUI();
				levelScene = levelsUI();
	            stage.setScene(levelScene);
	        });
			count++;
			row.getChildren().add(unlockBttn);
		}
		title.setLayoutX(20);
		title.setLayoutY(45);
		team.getChildren().add(row);
		team.setLayoutX(10);
		team.setLayoutY(75);
		pane.getChildren().addAll(team, title);
		return pane;
	}
	/**
	 * Creates Scene for teamPreview. Allowing to see own team
	 * and calling to see Pokedex
	 * @return Scene teamPreview
	 */
	public Scene makeTeamPreview() 
	{
		teamBttns = new ToggleButton[6];
		Button backBttn = new Button("Go to Levels");
		if (cc.getNetworkMultiplayer()) {
			backBttn.setText("Battle!");
		}
		
		backBttn.setFont(pokemonFont);
		backBttn.setOnAction(e -> {
			if (!cc.getNetworkMultiplayer()) {
				cleanUI();
				levelScene = levelsUI();
	            stage.setScene(levelScene);
			} else if (cc.getNetworkMultiplayer()) {
        		setUpBattle();
        		battleScene = battleUITesting(stage);
                stage.setScene(battleScene);
			}
			
        });
		backBttn.setFont(statsFont);
		Label previewLabel;
		teamPreview = new HBox();
		previewLabel = new Label("Your Team Preview");
		previewLabel.setFont(pokemonFont);
		VBox team = new VBox();
		HBox row = new HBox();
		int count = 0;
		team.getChildren().add(previewLabel);
		for (int i = 0; i < 6; i++) 
		{
			if (count == 2) 
			{
				team.getChildren().add(row);
				row = new HBox();
				count = 0;
			}
			Image pokemonSprite;
			if (i >= p1.teamSize())
			{
				pokemonSprite = new Image(getClass().getResourceAsStream("/media/mainmenu/emptySlot.png"), 60, 60, false, false);
			}
			else 
			{
				pokemonSprite = new Image(getClass().getResourceAsStream(p1.getMonAt(i).getOppSprite()), 60, 60, false, false);
			}
			final int tempI = i;
			ToggleButton pokemonSpriteBttn = new ToggleButton();
			pokemonSpriteBttn.setOnAction(e -> {
				for (int j = 0; j < pokedexBttns.length; j++) 
				{
					if (pokedexBttns[j].isSelected()) 
					{
						p1.replaceMon(j, p1.getPokedexAt(tempI));
						makeTeamPreview();
						teamScene = makeTeamPreview();
			            stage.setScene(teamScene);
					}
				}
				for (int j = 0; j < teamBttns.length; j++) 
				{
					if (teamBttns[j].isSelected()) 
					{
						if (j < p1.teamSize()) 
						{
							team.getChildren().remove(team.getChildren().size()-1);
							team.getChildren().add(pokemonStatsAndMoves(p1.getMon(j)));
						}
						if (j < p1.teamSize() && tempI < p1.teamSize() && j != tempI) 
						{
							p1.swapMon(tempI, j);
							makeTeamPreview();
							teamScene = makeTeamPreview();
				            stage.setScene(teamScene);
						}
						if (teamBttns[j].isSelected() && j != tempI) 
						{
							teamBttns[j].setSelected(false);
						}
						
					}
				}
	        });
			ImageView pokemonSpriteView = new ImageView();
			pokemonSpriteView.setImage(pokemonSprite);
			pokemonSpriteBttn.setGraphic(pokemonSpriteView);
			count++;
			teamBttns[i] = pokemonSpriteBttn;
			row.getChildren().add(pokemonSpriteBttn);
		}
		if (count != 0) 
		{
			team.getChildren().add(row);
		}
		team.setAlignment(Pos.CENTER);
		team.getChildren().add(pokemonStatsAndMoves(p1.getActiveMon()));
		teamPreview.getChildren().addAll(team, makePokedex());
		teamPreview.setAlignment(Pos.CENTER);
		teamPreview.setLayoutX(12);
		backBttn.setLayoutX(392);
		backBttn.setLayoutY(293);
		Pane pane = new Pane(teamPreview, backBttn);
		teamScene = new Scene(pane, 480, 320);
		return teamScene;
	}
	/**
	 * Creates pokedex preview to see Dex and swap Mons on team
	 * @return VBox pokedexPreview
	 */
	private VBox makePokedex() 
	{
		Label pokedexLabel = new Label("Your Box");
		pokedexLabel.setFont(pokemonFont);
		VBox team = new VBox();
		HBox row = new HBox();
		team.getChildren().add(pokedexLabel);
		int count = 0;
		pokedexBttns = new ToggleButton[20];
		for (int i = 0; i < 20; i++) 
		{
			boolean isEmpty = false;
			boolean isInTeam = false;
			if (count == 4) 
			{
				team.getChildren().add(row);
				row = new HBox();
				count = 0;
			}
			Image pokemonSprite;
			if (i >= p1.pokedexSize())
			{
				pokemonSprite = new Image(getClass().getResourceAsStream("/media/mainmenu/emptySlot.png"), 60, 60, false, false);
				isEmpty = true;
			}
			else 
			{
				pokemonSprite = new Image(getClass().getResourceAsStream(p1.getPokedexAt(i).getOppSprite()), 60, 60, false, false);
				if (p1.monIsInTeam(p1.getPokedexAt(i))) 
				{
					isInTeam = true;
				}
			}
			final int tempI = i;
			ToggleButton pokemonSpriteBttn = new ToggleButton();
			pokemonSpriteBttn.setOnAction(e -> {
				for (int j = 0; j < teamBttns.length; j++) 
				{
					if (teamBttns[j].isSelected()) 
					{
						p1.replaceMon(j, p1.getPokedexAt(tempI));
						makeTeamPreview();
						teamScene = makeTeamPreview();
			            stage.setScene(teamScene);
					}
				}
				for (int j = 0; j < pokedexBttns.length; j++) 
				{
					if (pokedexBttns[j].isSelected()) 
					{
						pokedexBttns[j].setSelected(false);
					}
				}
	        });
			ImageView pokemonSpriteView = new ImageView();
			pokemonSpriteView.setImage(pokemonSprite);
			pokemonSpriteBttn.setGraphic(pokemonSpriteView);
			pokemonSpriteBttn.setDisable(isInTeam);
			if (isEmpty) 
			{
				pokemonSpriteBttn.setDisable(isEmpty);
			}
			count++;
			pokedexBttns[i] = pokemonSpriteBttn;
			row.getChildren().add(pokemonSpriteBttn);
		}
		return team;
	}
	/**
	 * Creates a box of current selected pokemons moves and stats
	 * @return VBox of stats and moves
	 */
	private VBox pokemonStatsAndMoves(Mon activeMon)
	{
		VBox rows = new VBox();
		Label hp = new Label("HP: " + activeMon.getMaxHp() + "  ");
		Label attack = new Label("Attack: " + activeMon.getAtt() + "");
		Label defense = new Label("Defense: " + activeMon.getDef() + "  ");
		Label sAttack = new Label("Special Attack: " + activeMon.getSAtt() + "  ");
		Label sDefense = new Label("Special Defense: " + activeMon.getSDef() + "  ");
		Label move1 = new Label(activeMon.getMove(0).getName() + "  ");
		Label move2 = new Label(activeMon.getMove(1).getName() + "  ");
		Label move3 = new Label(activeMon.getMove(2).getName() + "  ");
		Label move4 = new Label(activeMon.getMove(3).getName() + "  ");
		Label moves = new Label("Moves:");
		moves.setFont(statsFont);
		HBox statsRow1 = new HBox();
		HBox statsRow2 = new HBox();
		HBox movesRow1 = new HBox();
		HBox movesRow2 = new HBox();
		hp.setFont(statsFont);
		attack.setFont(statsFont);
		defense.setFont(statsFont);
		sAttack.setFont(statsFont);
		sDefense.setFont(statsFont);
		move1.setFont(statsFont);
		move2.setFont(statsFont);
		move3.setFont(statsFont);
		move4.setFont(statsFont);
		statsRow1.getChildren().addAll(hp, attack);
		statsRow1.setAlignment(Pos.CENTER);
		statsRow2.getChildren().addAll(defense, sAttack);
		statsRow2.setAlignment(Pos.CENTER);
		movesRow1.getChildren().addAll(move1, move2);
		movesRow1.setAlignment(Pos.CENTER);
		movesRow2.getChildren().addAll(move3, move4);
		movesRow2.setAlignment(Pos.CENTER);
		rows.setAlignment(Pos.CENTER);
		rows.getChildren().addAll(statsRow1, statsRow2, sDefense, moves, movesRow1, movesRow2);
		return rows;
	}
	
	/**
	 * Creates the multiplayer menu and handles moving the UI to the local or networking screens. 
	 * @return	Returns a scene of the multiplayer menu
	 * @throws IOException
	 */
	private Scene multiplayerMenu() throws IOException  {
		Image pokemonTitleImage = new Image(getClass().getResourceAsStream("/media/mainmenu/PokemonLogo.png"));
		pokemonTitle = new ImageView();
		pokemonTitle.setImage(pokemonTitleImage);
		pokemonTitle.setPreserveRatio(true);
		pokemonTitle.setFitHeight(100);
		Image backGroundImage = new Image(getClass().getResourceAsStream("/media/mainmenu/BackgroundPokemon.png"));
		backGround = new ImageView();
		backGround.setImage(backGroundImage);
		//Title Animation
		ScaleTransition grow = new ScaleTransition(Duration.seconds(1.0), pokemonTitle);
		ScaleTransition shrink = new ScaleTransition(Duration.seconds(1.0), pokemonTitle);
		grow.setToX(1.25);
		grow.setToY(1.25);
		shrink.setToX(1);
		shrink.setToY(1);
		SequentialTransition loop = new SequentialTransition(grow, shrink);
		loop.setCycleCount(SequentialTransition.INDEFINITE);
		loop.play();
		
		VBox multiplayerLayout = new VBox();
		String localHost = InetAddress.getLocalHost().toString();
		ArrayList<String> checkString = new ArrayList<>();
		boolean isSlash = false;
		for (int i = 0; i < localHost.length(); i++) {
			String check = localHost.substring(i, i+1);
			if (check.equals("/")) {
				isSlash = true;
			}
			if (isSlash) {
				checkString.add(localHost.substring(i, i+1));
			}
		}
		localHost = String.join("", checkString);
		localHost.substring(1);
		System.out.println(localHost);
		if (localHost.substring(0, 4).equals("/127")) {
			Socket sock = new Socket();
			sock.connect(new InetSocketAddress("google.com", 80));
			localHost = sock.getLocalAddress().toString();
		}
        Text myIP = new Text(localHost.substring(1));
        myIP.setStroke(Color.BLACK);
        myIP.setFill(Color.WHITE);
        myIP.setStrokeWidth(0.3);
        myIP.setFont(new Font(20));
        TextField ipInput = new TextField();
        ipInput.setAlignment(Pos.CENTER);
        ipInput.setPrefWidth(100);
        ipInput.setMaxWidth(100);
        Text serverStatus = new Text("");
        serverStatus.setVisible(false);
        
        Button localMultiplayer = new Button("Local Multiplayer");
        localMultiplayer.setOnAction(e -> {
        	toggledEndGameScreen = false;
        	setUpBattle();
        	cc.setNetworkMultiplayer(false);
        	cc.setMultiplayer(true);
           	teamScene = makeTeamPreview();
            stage.setScene(teamScene);
        });
        Button hostButton = new Button("Host game");
        hostButton.setOnAction(e -> { 
        	Server.main(null);
        	serverStatus.setText("Server Started!");
        	serverStatus.setVisible(true);
        });
        Button joinButton = new Button("Join game");
        joinButton.setOnAction(e -> {
        	cc.addObserver(this);
        	String ip = ipInput.getText();
        	cc.setMultiplayer(true);
        	cc.setNetworkMultiplayer(true);
        	cc.connectToServer(ip);
        	toggledEndGameScreen = false;
        	if (cc.getPlayerID() == 1) {
        		setUpBattle();
        		mainLayout = new VBox();
                cc.setMultiplayer(true);
            	cc.setNetworkMultiplayer(true);
            	for (Mon pmon : mpMon) {
            		p1.addToDex(pmon);
            	}
            	teamScene = makeTeamPreview();
                stage.setScene(teamScene);
               
        	} else {
        		setUpBattle();
        		cc.setMultiplayer(true);
             	cc.setNetworkMultiplayer(true);
        		for (Mon pmon : mpMon) {
            		p1.addToDex(pmon);
            	}
            	teamScene = makeTeamPreview();
                stage.setScene(teamScene);
        	}
        });
        Button backButton = new Button("Main Menu");
        backButton.setOnAction(e -> {
        	cc.setMultiplayer(false);
        	cc.setNetworkMultiplayer(false);
            stage.setScene(mainMenuScene);
        });
        
        multiplayerLayout.getChildren().addAll(myIP, ipInput, localMultiplayer, hostButton, joinButton, backButton);
        multiplayerLayout.setAlignment(Pos.CENTER);
        multiplayerLayout.setSpacing(10);
        
        multiplayerMenu.setSpacing(10);
        multiplayerMenu.setAlignment(Pos.CENTER);
        multiplayerMenu.getChildren().addAll(pokemonTitle, multiplayerLayout);
        BackgroundImage backGroundImg = new BackgroundImage(backGroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(480, 320, false, false, false, true));
        Background backGround = new Background(backGroundImg);
        VBox multiplayerMenu2 = new VBox();
        multiplayerMenu2.setBackground(backGround);
        multiplayerMenu2.setAlignment(Pos.CENTER_RIGHT);
        multiplayerMenu2.getChildren().addAll(multiplayerMenu, bottomBar);
        multiplayerScene = new Scene(multiplayerMenu2, 480, 320);
        return multiplayerScene;
        
        
	}

	/**
	 * Sets up the battle
	 */
	private void setUpBattle() 
	{
		selectedChallenge.resetEnemy();
		p1.resetMons();
		p2 = selectedChallenge.getEnemy();
		boolean mpTemp = cc.getMultiplayer();
		boolean networkTemp = cc.getNetworkMultiplayer();
		int playerID = cc.getPlayerID();
		ReadWriteServer serv = cc.getRWR();
		cc = new CombatController(p1, p2);
		cc.setPlayerTurn(true);
		cc.updateRWR(serv);
		cc.setPlayerID(playerID);
		cc.setMultiplayer(mpTemp);
		cc.setNetworkMultiplayer(networkTemp);
		cc.addObserver(this);
		moves = cc.getPlayerMoves(1);
		cleanUI();
	}
	/**
	 * Calls necessary calls to create BattleUI Scene 
	 * @return battleScene
	 */
	private Scene battleUITesting(Stage stage) 
	{
		battleUIBattleScreen();
		battleUIBottomBar();
		mainLayout.getChildren().add(battleArea);
		mainLayout.getChildren().add(bottomBar);
		Scene battleScene = new Scene(mainLayout, 480, 320);
		return battleScene;
	}

	/**
	 * battleUIBattleScreen builds a 4 quadrant group of containers that contain - Top Left: P2 Health, Top Right: P2 Mon Sprite, Bottom Left: P1 Mon Sprite
	 * and Bottom Right: P1 health. Each quadrant is 240x110 pixels big. 
	 */
	private void battleUIBattleScreen() {
		VBox battleSides = new VBox();
		HBox monHealthPlayerSides = new HBox();
		HBox monHealthCPUSides = new HBox();

		// Player Pokemon Sprite
		StackPane playerSpriteStack = new StackPane();
		Rectangle battlePlayerBackground = new Rectangle(0, 0, 240, 110);
		battlePlayerBackground.setFill(pokeWhite);
		Image playerPokemon = new Image(getClass().getResourceAsStream(p1.getActiveMon().getPlayerSprite()), 100, 100, false, false);
		playerSpriteView = new ImageView();
		playerSpriteView.setImage(playerPokemon);
		playerSpriteView.setTranslateY(30);
		playerSpriteStack.getChildren().addAll(battlePlayerBackground, playerSpriteView);

		// Player Pokemon HP Bar
		StackPane playerHealth = new StackPane();
		Rectangle battlePlayerHealthBackground = new Rectangle(0, 0, 240, 110);
		battlePlayerHealthBackground.setFill(pokeWhite);

		// Player health background
		Rectangle battlePlayerHealth = new Rectangle(0, 0, 175, 55);
		battlePlayerHealth.setStroke(Color.BLACK);
		battlePlayerHealth.setStrokeWidth(3);
		battlePlayerHealth.setX(0);
		battlePlayerHealth.setY(0);
		battlePlayerHealth.setFill(pokeTan);
		battlePlayerHealth.setArcHeight(10);
		battlePlayerHealth.setArcWidth(10);
		battlePlayerHealth.setTranslateX(20);
		
		// Setting up the boxes to seperate the Health screen elements
		VBox battlePlayerBox = new VBox();
		HBox battlePlayerNameBar = new HBox();
		battlePlayerNameBar.setMaxWidth(150);
		
		// Adding text to Player health screen
		battlePlayerPokeName.setFont(new Font(17));
		battlePlayerPokeName.setTranslateX(2);
		battlePlayerPokeName.setTextAlignment(TextAlignment.LEFT);
		battlePlayerHealthBar.setTranslateX(10);
		battlePlayerHealthBar.setTranslateY(3);
		battlePlayerHealthBar.setProgress(1);

		// Setting P1 HP font size
		battlePlayerHP.setFont(new Font(17));
		
		//Adding player health elements to boxes
		battlePlayerNameBar.getChildren().addAll(battlePlayerHP, battlePlayerHealthBar);
		battlePlayerBox.getChildren().addAll(battlePlayerPokeName, battlePlayerNameBar);
		battlePlayerBox.setTranslateX(55);
		battlePlayerBox.setTranslateY(30);
		
		// Adding those boxes to the larger UI boxes
		playerHealth.getChildren().addAll(battlePlayerHealthBackground, battlePlayerHealth, battlePlayerBox);
		monHealthPlayerSides.getChildren().addAll(playerSpriteStack, playerHealth);

		// NPC Pokemon Sprite
		StackPane player2SpriteStack = new StackPane();
		Rectangle battlePlayer2Background = new Rectangle(0, 0, 240, 110);
		battlePlayer2Background.setX(0);
		battlePlayer2Background.setY(0);
		battlePlayer2Background.setFill(pokeWhite);
		Image player2Pokemon = new Image(getClass().getResourceAsStream(p2.getActiveMon().getOppSprite()), 100, 100, false, false);
		player2SpriteView = new ImageView();
		player2SpriteView.setImage(player2Pokemon);
		player2SpriteView.setTranslateY(30);
		player2SpriteStack.getChildren().addAll(battlePlayer2Background, player2SpriteView);

		// NPC Pokemon HP Bar
		StackPane npcHealth = new StackPane();
		Rectangle battleNPCHealthBackground = new Rectangle(0, 0, 240, 110);
		battleNPCHealthBackground.setFill(pokeWhite);
		
		// NPC Pokemon health background
		Rectangle battleNPCHealth = new Rectangle(0, 0, 175, 55);
		battleNPCHealth.setStroke(Color.BLACK);
		battleNPCHealth.setStrokeWidth(3);
		battleNPCHealth.setX(0);
		battleNPCHealth.setY(0);
		battleNPCHealth.setFill(pokeTan);
		battleNPCHealth.setArcHeight(10);
		battleNPCHealth.setArcWidth(10);
		battleNPCHealth.setTranslateX(-20);
		
		// Setting up the boxes to separate the Health screen elements
		VBox battleNPCPlate = new VBox();
		HBox batleNPCHealth = new HBox();
		batleNPCHealth.setMaxWidth(150);
		
		// Setting P2 HP font size
		battleNPCPokeName.setFont(new Font(17));
		
		// Setting default health bar parameters
		battleNPCHealthBar.setProgress(1);
		battleNPCHealthBar.setTranslateX(10);
		battleNPCHealthBar.setTranslateY(3);

		// P2 health text size
		battleNPCHealthText.setFont(new Font(17));
		
		// Moving p2 health UI into position
		battleNPCPlate.setTranslateX(25);
		battleNPCPlate.setTranslateY(30);
		
		// Adding NPC health UI elements to boxes
		batleNPCHealth.getChildren().addAll(battleNPCHealthText, battleNPCHealthBar);
		battleNPCPlate.getChildren().addAll(battleNPCPokeName, batleNPCHealth);

		// Adding those boxes to the overall boxes
		npcHealth.getChildren().addAll(battleNPCHealthBackground, battleNPCHealth, battleNPCPlate);
		monHealthCPUSides.getChildren().addAll(npcHealth, player2SpriteStack);
		
		// Adding the Player 1 and Player 2 HBoxes to a VBox
		battleSides.getChildren().addAll(monHealthCPUSides, monHealthPlayerSides);
		
		// Adding that VBox to the battle VBox
		battleArea.getChildren().addAll(battleSides);
		battleArea.setPrefHeight(220);
		battleArea.setPrefWidth(480);
		
	}

	/**
	 * battleUIBottomBar creates the bar at the bottom which contains the controls for playing the game. Bottom Bar itself creates the base layer bar
	 * and the basic 4 interaction functions which other methods build on-top of.
	 */
	private void battleUIBottomBar() {
		StackPane textStack = new StackPane();

		notificationText.setText("What will\n" + p1.getActiveMon().getName() + " do?");
		notificationText.setFill(Color.WHITE);
		notificationText.setFont(new Font(20));
		notificationText.setTextAlignment(TextAlignment.LEFT);
		notificationText.setTranslateY(22);
		Rectangle notiBackgroundBlack = new Rectangle(0, 0, 480, 100);
		notiBackgroundBlack.setFill(Color.BLACK);
		notiBackgroundBlack.setStroke(Color.BLACK);
		Rectangle notiBackgroundOrange = new Rectangle(5, 5, 475, 95);
		notiBackgroundOrange.setFill(pokeOrange);
		notiBackgroundOrange.setArcHeight(20);
		notiBackgroundOrange.setArcWidth(20);
		Rectangle notiBackgroundWhite = new Rectangle(15, 15, 465, 85);
		notiBackgroundWhite.setArcWidth(20);
		notiBackgroundWhite.setArcHeight(20);
		notiBackgroundWhite.setFill(pokeWhite);
		Rectangle notiBackgroundText = new Rectangle(25, 25, 455, 75);
		notiBackgroundText.setFill(pokeBlue);
		VBox notificationBox = new VBox();
		notificationBox.getChildren().add(notificationText);
		StackPane notificationStack = new StackPane();
		
		// Networking button to sync clients when both are ready
		Button networkingButton = new Button("Click when ready!");
		networkingButton.setTranslateX(-100);
		networkingButton.setFont(new Font(20));
		
		networkingButton.setOnAction(e -> {
			try {
				cc.notifyReadWrite();
				networkingButton.setVisible(false);
				textStack.setVisible(true);
			} catch (InterruptedException e1) {
				System.out.println("Error unpausing thread using in game button! " + e1);
			}
		});
		
		if (!cc.getNetworkMultiplayer()) {
			networkingButton.setVisible(false);
		} else {
			textStack.setVisible(false);
		}

		textStack.getChildren().add(notificationBox);
		textStack.setPrefHeight(80);
		textStack.setPrefWidth(220);
		// textStack.setAlignment(Pos.CENTER_LEFT); // Move StackPane to left side
		textStack.setTranslateX(20); // Move X 20 pixels to align within notiBackgroundText box
		notificationStack.setPrefHeight(100);
		notificationStack.setPrefWidth(480);
		notificationStack.getChildren().addAll(notiBackgroundBlack, notiBackgroundOrange, notiBackgroundWhite,
				notiBackgroundText, textStack, networkingButton);

		bottomBar.setPrefHeight(100);
		bottomBar.setPrefWidth(480);
		bottomBar.getChildren().add(notificationStack);
		bottomBar.getChildren().add(battleUIPlayerMove());
		bottomBar.setAlignment(Pos.BOTTOM_LEFT);
		moveStack.setVisible(true);
	}
	
	/**
	 * Creates the player action section of the bottom bar, and handles button logic for basic actions
	 * like attack, run, and pokemon switch.
	 * @return
	 */
	private StackPane battleUIPlayerMove() {
		moveStack.setAlignment(Pos.CENTER);
		moveStack.setTranslateX(120);
		Rectangle moveBackground = new Rectangle(0, 0, 240, 100);
		moveBackground.setFill(Color.BLACK);
		moveBackground.setStroke(Color.BLACK);
		Rectangle moveBackgroundPurple = new Rectangle(5, 5, 235, 95);
		moveBackgroundPurple.setFill(pokePurple);
		moveBackgroundPurple.setArcHeight(8);
		moveBackgroundPurple.setArcWidth(8);
		Rectangle moveBackgroundWhite = new Rectangle(20, 20, 220, 80);
		moveBackgroundWhite.setFill(pokeWhite);
		moveBackgroundWhite.setArcHeight(8);
		moveBackgroundWhite.setArcWidth(8);

		// Building the Bottom Bar grid for the selection boxes and text
		GridPane actionGrid = new GridPane();
		actionGrid.setPrefHeight(80);
		actionGrid.setPrefWidth(220);
		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints();
		col1.setPercentWidth(0);
		col2.setPercentWidth(0);
		actionGrid.getColumnConstraints().addAll(col1, col2);
		RowConstraints row1 = new RowConstraints();
		RowConstraints row2 = new RowConstraints();
		row1.setPercentHeight(0);
		row2.setPercentHeight(0);
		actionGrid.getRowConstraints().addAll(row1, row2);

		// Bottom bar Selection Text
		DropShadow textShadow = new DropShadow();
		textShadow.setRadius(4);
		textShadow.setOffsetX(0);
		textShadow.setOffsetY(2);
		textShadow.setColor(Color.color(0.4, 0.5, 0.5));
		Text fightText = new Text("FIGHT");
		fightText.setFont(new Font(18));
		fightText.setEffect(textShadow);
		fightText.setMouseTransparent(true);
		Text switchText = new Text("POKeMON");
		switchText.setFont(new Font(18));
		switchText.setEffect(textShadow);
		switchText.setMouseTransparent(true);
		Text bagText = new Text("BAG");
		bagText.setFont(new Font(18));
		bagText.setEffect(textShadow);
		bagText.setMouseTransparent(true);
		Text runText = new Text("EXIT");
		runText.setFont(new Font(18));
		runText.setEffect(textShadow);
		runText.setMouseTransparent(true);

		StackPane fightStack = new StackPane();
		StackPane switchStack = new StackPane();
		StackPane bagStack = new StackPane();
		StackPane runStack = new StackPane();

		// Creating the bottom bar selection boxes, and defining their behavior that
		// reside behind the Text defined above
		Rectangle fightButton = new Rectangle(0, 0, 100, 30);
		fightButton.setFill(pokeWhite);
		fightButton.setOnMouseEntered(event -> {
			fightText.setFill(Color.RED);
		});

		fightButton.setOnMouseClicked(event -> {
			attackPane.setVisible(true);
			monSelect.setVisible(false);
		});

		fightButton.setOnMouseExited(event -> {
			fightText.setFill(Color.BLACK);
		});

		Rectangle switchButton = new Rectangle(0, 0, 100, 30);
		switchButton.setFill(pokeWhite);
		switchButton.setOnMouseEntered(event -> {
			switchText.setFill(Color.RED);
		});
		
		switchButton.setOnMouseExited(event -> {
			switchText.setFill(Color.BLACK);
		});
		
		switchButton.setOnMouseClicked(event -> {
			monSelect.setVisible(true);
		});
		
		
		Rectangle bagButton = new Rectangle(0, 0, 100, 30);
		bagButton.setFill(pokeWhite);
		bagButton.setOnMouseEntered(event -> {
			bagText.setFill(Color.RED);
		});
		
		bagButton.setOnMouseExited(event -> {
			bagText.setFill(Color.BLACK);
		});
		
		
		Rectangle runButton = new Rectangle(0, 0, 100, 30);
		runButton.setFill(pokeWhite);
		runButton.setOnMouseEntered(event -> {
			runText.setFill(Color.RED);
		});
		
		runButton.setOnMouseExited(event -> {
			runText.setFill(Color.BLACK);
		});
		
		runButton.setOnMouseClicked(event -> {
			if (cc.getMultiplayer()) {  
			stage.setScene(multiplayerScene);
	        stage.show();
			} else {
				 stage.setScene(mainMenuScene);
			     stage.show();
			}
		});

		// Adding the selection boxes and text to their corresponding stack panes
		fightStack.getChildren().addAll(fightButton, fightText);
		bagStack.getChildren().addAll(bagButton, bagText);
		switchStack.getChildren().addAll(switchButton, switchText);
		runStack.getChildren().addAll(runButton, runText);

		// Putting the StackPanes in the correct locations
		actionGrid.add(fightStack, 0, 0);
		actionGrid.add(bagStack, 1, 0);
		actionGrid.add(switchStack, 0, 1);
		actionGrid.add(runStack, 1, 1);
		actionGrid.setAlignment(Pos.CENTER);

		playerAttackPane();
		playerMonSelectionPane();

		moveStack.getChildren().addAll(moveBackground, moveBackgroundPurple, moveBackgroundWhite, actionGrid,
				attackPane, monSelect);
		return moveStack;
	}

	/**
	 * playerAttackPane builds the pane responsible for showing the current Mons moves, the amount of PP, and attack type.
	 * Selecting an attack then sends it to the controller.
	 */
	private void playerAttackPane() {
		Rectangle attackBackground = new Rectangle(0, 0, 480, 100);
		attackBackground.setFill(Color.BLACK);
		// attackBackground.setTranslateX(-120);
		HBox movesAndInfo = new HBox();

		Text pp = new Text("");
		pp.setFont(new Font(20));
		Text type = new Text("");
		type.setFont(new Font(20));

		// 350px
		StackPane attackMovesStack = new StackPane();
		Rectangle attackBackgroundPurple = new Rectangle(5, 5, 350, 95);
		attackBackgroundPurple.setFill(pokePurple);
		attackBackgroundPurple.setArcHeight(8);
		attackBackgroundPurple.setArcWidth(8);
		Rectangle attackBackgroundWhite = new Rectangle(10, 10, 340, 85);
		attackBackgroundWhite.setFill(pokeWhite);
		attackBackgroundWhite.setArcHeight(8);
		attackBackgroundWhite.setArcWidth(8);

		StackPane move1Stack = new StackPane();
		Rectangle move1Button = new Rectangle(0, 0, 170, 40);
		move1Button.setFill(pokeWhite);
		Text move1 = new Text(p1.getActiveMon().getMove(0).getName());
		move1.setFont(new Font(20));
		move1.setMouseTransparent(true);
		move1Stack.getChildren().addAll(move1Button, move1);
		playerMoves.add(move1);

		StackPane move2Stack = new StackPane();
		Rectangle move2Button = new Rectangle(0, 0, 170, 40);
		move2Button.setFill(pokeWhite);
		Text move2 = new Text(p1.getActiveMon().getMove(1).getName());
		move2.setMouseTransparent(true);
		move2.setFont(new Font(20));
		move2Stack.getChildren().addAll(move2Button, move2);
		playerMoves.add(move2);

		StackPane move3Stack = new StackPane();
		Rectangle move3Button = new Rectangle(0, 0, 170, 40);
		move3Button.setFill(pokeWhite);
		Text move3 = new Text(p1.getActiveMon().getMove(2).getName());
		move3.setMouseTransparent(true);
		move3.setFont(new Font(20));
		move3Stack.getChildren().addAll(move3Button, move3);
		playerMoves.add(move3);

		StackPane move4Stack = new StackPane();
		Rectangle move4Button = new Rectangle(0, 0, 170, 40);
		move4Button.setFill(pokeWhite);
		Text move4 = new Text(p1.getActiveMon().getMove(3).getName());
		move4.setMouseTransparent(true);
		move4.setFont(new Font(20));
		move4Stack.getChildren().addAll(move4Button, move4);
		playerMoves.add(move4);

		movePane.add(move1Stack, 0, 0);
		movePane.add(move2Stack, 0, 1);
		movePane.add(move3Stack, 1, 0);
		movePane.add(move4Stack, 1, 1);

		movePane.setTranslateX(5);
		movePane.setTranslateY(10);

		// Controls for the 1st player pokemon move
		move1Button.setOnMouseEntered(event -> {
			move1.setFill(Color.RED);
			if (cc.getPlayerTurn()) {
				pp.setText("PP: " + p1.getActiveMon().getMove(0).getPP() + "/" + p1.getActiveMon().getMove(0).getMaxPP());
				type.setText(p1.getActiveMon().getMove(0).getType().name());
			} else if (!cc.getPlayerTurn()) {
				pp.setText("PP: " + p2.getActiveMon().getMove(0).getPP() + "/" + p2.getActiveMon().getMove(0).getMaxPP());
				type.setText(p2.getActiveMon().getMove(0).getType().name());
			}
			
		});

		move1Button.setOnMouseExited(event -> {
			move1.setFill(Color.BLACK);
			pp.setText("");
			type.setText("");
		});

		move1Button.setOnMouseClicked(event -> {
			if (!p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && !cc.getMultiplayer()) {
				cc.makeMove(1, 0);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
				cc.makeComputerMove();
			} else if (cc.getPlayerTurn() && !p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && cc.getMultiplayer()) {
				cc.makeMove(1, 0);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
			} else if (!cc.getPlayerTurn() && !p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && cc.getMultiplayer()) {
				cc.makeMove(2, 0);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
			}
			else {
				attackPane.setVisible(false);
			}
			if (cc.getNetworkMultiplayer()) { cc.updateThread(); }
		});

		// Controls for the 2nd player pokemon move
		move2Button.setOnMouseEntered(event -> {
			move2.setFill(Color.RED);
			if (cc.getPlayerTurn()) {
				pp.setText("PP: " + p1.getActiveMon().getMove(1).getPP() + "/" + p1.getActiveMon().getMove(1).getMaxPP());
				type.setText(p1.getActiveMon().getMove(1).getType().name());
			} else if (!cc.getPlayerTurn()) {
				pp.setText("PP: " + p2.getActiveMon().getMove(1).getPP() + "/" + p2.getActiveMon().getMove(1).getMaxPP());
				type.setText(p2.getActiveMon().getMove(1).getType().name());
			}
		});

		move2Button.setOnMouseExited(event -> {
			move2.setFill(Color.BLACK);
			pp.setText("");
			type.setText("");
		});

		move2Button.setOnMouseClicked(event -> {
			if (!p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && !cc.getMultiplayer()) {
				cc.makeMove(1, 1);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
				cc.makeComputerMove();
			} else if (!p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && cc.getMultiplayer() && cc.getPlayerTurn()) {
				cc.makeMove(1, 1);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
			} else if (!p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && cc.getMultiplayer() && !cc.getPlayerTurn()) {
				cc.makeMove(2, 1);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
			}
			else {
				attackPane.setVisible(false);
			}
			if (cc.getNetworkMultiplayer()) { cc.updateThread();}
			

		});

		// Controls for the 3rd player pokemon move
		move3Button.setOnMouseEntered(event -> {
			move3.setFill(Color.RED);
			pp.setText("PP: " + p1.getActiveMon().getMove(2).getPP() + "/" + p1.getActiveMon().getMove(2).getMaxPP());
			type.setText(p1.getActiveMon().getMove(2).getType().name());
		});

		move3Button.setOnMouseExited(event -> {
			move3.setFill(Color.BLACK);
			pp.setText("");
			type.setText("");
		});

		move3Button.setOnMouseClicked(event -> {
			if (!cc.getMultiplayer() && !p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted()) {
				cc.makeMove(1, 2);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
				cc.makeComputerMove();
			} else if (cc.getMultiplayer() && cc.getPlayerTurn() && !p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted()) {
				cc.makeMove(1, 2);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
			} else if (cc.getMultiplayer() && !cc.getPlayerTurn() && !p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted()) {
				cc.makeMove(2, 2);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
			}
			else {
				attackPane.setVisible(false);
			}
			if (cc.getNetworkMultiplayer()) { 
				cc.updateThread();
			}
			

		});

		// Controls for the the 4th player pokemon move
		move4Button.setOnMouseEntered(event -> {
			move4.setFill(Color.RED);
			pp.setText("PP: " + p1.getActiveMon().getMove(3).getPP() + "/" + p1.getActiveMon().getMove(3).getMaxPP());
			type.setText(p1.getActiveMon().getMove(3).getType().name());
		});

		move4Button.setOnMouseExited(event -> {
			move4.setFill(Color.BLACK);
			pp.setText("");
			type.setText("");
		});

		move4Button.setOnMouseClicked(event -> {
			if (!p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && !cc.getMultiplayer()) {
				cc.makeMove(1, 3);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
				cc.makeComputerMove();
			} else if (!p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && cc.getMultiplayer() && cc.getPlayerTurn()) {
				cc.makeMove(1, 3);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
			} else if (!p1.getActiveMon().hasFainted() && !p2.getActiveMon().hasFainted() && cc.getMultiplayer() && !cc.getPlayerTurn()) {
				cc.makeMove(2, 3);
				double hpBar = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
				battleNPCHealthBar.setProgress(hpBar);
				battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
				attackPane.setVisible(false);
			}
			else {
				attackPane.setVisible(false);
			}
			if (cc.getNetworkMultiplayer()) { cc.updateThread();}
			
		});

		attackMovesStack.getChildren().addAll(attackBackgroundPurple, attackBackgroundWhite, movePane);

		// 120px
		StackPane infoStack = new StackPane();
		Rectangle infoBackgroundPurple = new Rectangle(5, 5, 120, 95);
		infoBackgroundPurple.setFill(pokePurple);
		infoBackgroundPurple.setArcHeight(8);
		infoBackgroundPurple.setArcWidth(8);

		Rectangle infoBackgroundWhite = new Rectangle(10, 10, 110, 85);
		infoBackgroundWhite.setFill(pokeWhite);
		infoBackgroundWhite.setArcHeight(8);
		infoBackgroundWhite.setArcWidth(8);

		VBox ppType = new VBox();

		ppType.setTranslateX(10);
		ppType.setTranslateY(20);

		ppType.getChildren().addAll(pp, type);

		infoStack.setTranslateX(10);
		infoStack.getChildren().addAll(infoBackgroundPurple, infoBackgroundWhite, ppType);

		movesAndInfo.getChildren().addAll(attackMovesStack, infoStack);
		attackPane.getChildren().addAll(attackBackground, movesAndInfo);
		attackPane.setTranslateX(-120);
		attackPane.setVisible(false);
	}
	
	/**
	 * playerMonSelectionPane builds the pane for selecting a mon either when you want to switch it out or when the current
	 * one faints.
	 */
	private void playerMonSelectionPane() {
		Rectangle pokeSlotBackground = new Rectangle(0, 0, 303, 80);
		pokeSlotBackground.setFill(pokePurple);
		pokeSlotBackground.setStroke(Color.BLACK);
		pokeSlotBackground.setStrokeWidth(3);
		pokeSlotBackground.setArcHeight(8);
		pokeSlotBackground.setArcWidth(8);
		HBox monSelectBox = new HBox();
		
		StackPane pokeSlot1 = new StackPane();
		Rectangle pokeSlot1Button = new Rectangle(0, 0, 100, 70);
		pokeSlot1Button.setFill(pokeWhite);
		pokeSlot1Name.setFont(new Font(15));
		pokeSlot1Name.setMouseTransparent(true);
		pokeSlot1.getChildren().addAll(pokeSlot1Button, pokeSlot1Name);
		
		
		StackPane pokeSlot2 = new StackPane();
		Rectangle pokeSlot2Button = new Rectangle(0, 0, 100, 70);
		pokeSlot2Button.setFill(pokeWhite);
		pokeSlot2Name.setFont(new Font(15));
		pokeSlot2Name.setMouseTransparent(true);
		pokeSlot2.getChildren().addAll(pokeSlot2Button, pokeSlot2Name);
		
		StackPane pokeSlot3 = new StackPane();
		Rectangle pokeSlot3Button = new Rectangle(0, 0, 100, 70);
		pokeSlot3Button.setFill(pokeWhite);
		pokeSlot3Name.setFont(new Font(15));
		
		pokeSlot3Name.setMouseTransparent(true);
		pokeSlot3.getChildren().addAll(pokeSlot3Button, pokeSlot3Name);
		
		if (p1.teamSize() > 1) { pokeSlot1Name.setText(p1.getMon(1).getName()); }
		if (p1.teamSize() > 2) { pokeSlot2Name.setText(p1.getMon(2).getName()); }
		if (p1.teamSize() > 3) { pokeSlot3Name.setText(p1.getMon(3).getName()); }
		
		//Pokemon Switch
		pokeSlot1Button.setOnMouseEntered(event -> {
			pokeSlot1Name.setFill(Color.RED);
			if (p2.teamSize() > 1 && p2.getMon(1).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
				pokeSlot1Name.setFill(Color.GREY); 
				pokeSlot1Name.setStrikethrough(true);
			} else if (p2.teamSize() > 1 && p2.getMon(1).hasFainted() && cc.getNetworkMultiplayer() && cc.getPlayerID() == 2) {
				pokeSlot2Name.setFill(Color.GREY); 
				pokeSlot2Name.setStrikethrough(true);
			} else if (p1.teamSize() > 1 && p1.getMon(1).hasFainted()) { 
				pokeSlot1Name.setFill(Color.GREY); 
				pokeSlot1Name.setStrikethrough(true);
			}
			
		});
		
		pokeSlot1Button.setOnMouseExited(event -> {
			pokeSlot1Name.setFill(Color.BLACK);
			if (p2.teamSize() > 1 && p2.getMon(1).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
				pokeSlot1Name.setFill(Color.GREY); 
				pokeSlot1Name.setStrikethrough(true);
			} else if (p2.teamSize() > 1 && p2.getMon(1).hasFainted() && cc.getNetworkMultiplayer() && cc.getPlayerID() == 2) {
				pokeSlot2Name.setFill(Color.GREY); 
				pokeSlot2Name.setStrikethrough(true);
			} else if (p1.teamSize() > 1 && p1.getMon(1).hasFainted()) { 
				pokeSlot1Name.setFill(Color.GREY); 
				pokeSlot1Name.setStrikethrough(true);
			}
		});
		
		pokeSlot1Button.setOnMouseClicked(event -> {
			if (cc.getNetworkMultiplayer() && (p1.teamSize() > 1 && !p1.getMon(1).hasFainted() && cc.getPlayerID() == 1)) {
				cc.switchMon(1, 1); 
				monSelect.setVisible(false);
				//cc.setPlayerTurn(false);
			} else if (cc.getNetworkMultiplayer() && (p2.teamSize() > 1 && !p2.getMon(1).hasFainted() && cc.getPlayerID() == 2)) {
				cc.switchMon(2, 1); 
				monSelect.setVisible(false);
				//cc.setPlayerTurn(true);
			} else if (cc.getPlayerTurn() && p1.teamSize() > 1 && !p1.getMon(1).hasFainted() && !cc.getNetworkMultiplayer() && cc.getMultiplayer()) { 
				cc.switchMon(1, 1);
				monSelect.setVisible(false);
				cc.setPlayerTurn(false);
				if (!cc.getMultiplayer()) {
					cc.makeComputerMove();
					cc.setPlayerTurn(true);
				}
			} else if (p2.teamSize() > 1 && !p2.getMon(1).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
				cc.switchMon(2, 1); 
				monSelect.setVisible(false);
				cc.setPlayerTurn(true);
			} else if (!cc.getMultiplayer() && p1.teamSize() > 1 && !p1.getMon(1).hasFainted() && !cc.getNetworkMultiplayer()) {
				cc.switchMon(1, 1);
				monSelect.setVisible(false);
				cc.setPlayerTurn(true);
			}
		});
		
		pokeSlot2Button.setOnMouseEntered(event -> {
			pokeSlot2Name.setFill(Color.RED);
			 if (p2.teamSize() > 2 && p2.getMon(2).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
				pokeSlot2Name.setFill(Color.GREY); 
				pokeSlot2Name.setStrikethrough(true);
			} else if (p2.teamSize() > 2 && p2.getMon(2).hasFainted() && cc.getNetworkMultiplayer() && cc.getPlayerID() == 2) {
				pokeSlot2Name.setFill(Color.GREY); 
				pokeSlot2Name.setStrikethrough(true);
			} else if (p1.teamSize() > 2 && p1.getMon(2).hasFainted()) { 
				pokeSlot2Name.setFill(Color.GREY); 
				pokeSlot2Name.setStrikethrough(true);
			}
		});
		
		pokeSlot2Button.setOnMouseExited(event -> {
			pokeSlot2Name.setFill(Color.BLACK);
			 if (p2.teamSize() > 2 && p2.getMon(2).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
					pokeSlot2Name.setFill(Color.GREY); 
					pokeSlot2Name.setStrikethrough(true);
				} else if (p2.teamSize() > 2 && p2.getMon(2).hasFainted() && cc.getNetworkMultiplayer() && cc.getPlayerID() == 2) {
					pokeSlot2Name.setFill(Color.GREY); 
					pokeSlot2Name.setStrikethrough(true);
				} else if (p1.teamSize() > 2 && p1.getMon(2).hasFainted()) { 
					pokeSlot2Name.setFill(Color.GREY); 
					pokeSlot2Name.setStrikethrough(true);
				}
		});
		
		pokeSlot2Button.setOnMouseClicked(event -> {
			if (cc.getNetworkMultiplayer() && (p1.teamSize() > 2 && !p1.getMon(2).hasFainted() && cc.getPlayerID() == 1)) {
				cc.switchMon(1, 2); 
				monSelect.setVisible(false);
				//cc.setPlayerTurn(false);
			} else if (cc.getNetworkMultiplayer() && (p2.teamSize() > 2 && !p2.getMon(2).hasFainted() && cc.getPlayerID() == 2)) {
				cc.switchMon(2, 2); 
				monSelect.setVisible(false);
				//cc.setPlayerTurn(true);
			} else if (cc.getPlayerTurn() && p1.teamSize() > 2 && !p1.getMon(2).hasFainted() && !cc.getNetworkMultiplayer() && cc.getMultiplayer()) { 
				cc.switchMon(1, 2); 
				monSelect.setVisible(false);
				cc.setPlayerTurn(false);
				if (!cc.getMultiplayer()) {
					cc.makeComputerMove();
					cc.setPlayerTurn(true);
				}
			} else if (p2.teamSize() > 2 && !p2.getMon(2).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
				cc.switchMon(2, 2); 
				monSelect.setVisible(false);
				cc.setPlayerTurn(true);
			} else if (!cc.getMultiplayer() && p1.teamSize() > 2 && !p1.getMon(2).hasFainted() && !cc.getNetworkMultiplayer()) {
				cc.switchMon(1, 2);
				monSelect.setVisible(false);
				cc.setPlayerTurn(true);
			}

		});
		
		pokeSlot3Button.setOnMouseEntered(event -> {
			pokeSlot3Name.setFill(Color.RED);
			if (p2.teamSize() > 3 && p2.getMon(3).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
				pokeSlot3Name.setFill(Color.GREY); 
				pokeSlot3Name.setStrikethrough(true);
			} else if (p2.teamSize() > 3 && p2.getMon(3).hasFainted() && cc.getNetworkMultiplayer() && cc.getPlayerID() == 2) {
				pokeSlot2Name.setFill(Color.GREY); 
				pokeSlot2Name.setStrikethrough(true);
			} else if (p1.teamSize() > 3 && p1.getMon(3).hasFainted()) { 
				pokeSlot3Name.setFill(Color.GREY);
				pokeSlot3Name.setStrikethrough(true);
			}
		});
		
		pokeSlot3Button.setOnMouseExited(event -> {
			pokeSlot3Name.setFill(Color.BLACK);
			if (p2.teamSize() > 3 && p2.getMon(3).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer()) {
				pokeSlot3Name.setFill(Color.GREY); 
				pokeSlot3Name.setStrikethrough(true);
			} else if (p2.teamSize() > 3 && p2.getMon(3).hasFainted() && cc.getNetworkMultiplayer() && cc.getPlayerID() == 2) {
				pokeSlot2Name.setFill(Color.GREY); 
				pokeSlot2Name.setStrikethrough(true);
			} else if (p1.teamSize() > 3 && p1.getMon(3).hasFainted()) { 
				pokeSlot3Name.setFill(Color.GREY);
				pokeSlot3Name.setStrikethrough(true);
			}
		});
		
		pokeSlot3Button.setOnMouseClicked(event -> {
			if (cc.getNetworkMultiplayer() && (p1.teamSize() > 3 && !p1.getMon(3).hasFainted() && cc.getPlayerID() == 1)) {
				cc.switchMon(1, 3); 
				monSelect.setVisible(false);
				//cc.setPlayerTurn(false);
			} else if (cc.getNetworkMultiplayer() && (p2.teamSize() > 3 && !p2.getMon(3).hasFainted() && cc.getPlayerID() == 2)) {
				cc.switchMon(2, 3); 
				monSelect.setVisible(false);
				//cc.setPlayerTurn(true);
			} else if (cc.getPlayerTurn() && p1.teamSize() > 3 && !p1.getMon(3).hasFainted() && !cc.getNetworkMultiplayer() && cc.getMultiplayer()) { 
				cc.switchMon(1, 3); 
				monSelect.setVisible(false);
				cc.setPlayerTurn(false);
				if (!cc.getMultiplayer()) {
					cc.makeComputerMove();
					cc.setPlayerTurn(true);
				}
			} else if (p2.teamSize() > 3 && !p2.getMon(3).hasFainted() && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
				cc.switchMon(2, 3); 
				monSelect.setVisible(false);
				cc.setPlayerTurn(true);
			} else if (!cc.getMultiplayer() && p1.teamSize() > 1 && !p1.getMon(3).hasFainted() && !cc.getNetworkMultiplayer()) {
				cc.switchMon(1, 3);
				monSelect.setVisible(false);
				cc.setPlayerTurn(true);
			}
		});
		
		monSelectBox.getChildren().addAll(pokeSlot1, pokeSlot2, pokeSlot3);
		monSelectBox.setTranslateX(91);
	
		monSelect.getChildren().addAll(pokeSlotBackground, monSelectBox);
		monSelect.setTranslateX(-100);
		monSelect.setTranslateY(-100);
		monSelect.setVisible(false);
	}
	
	/**
	 * Update the battle UI based on information from the two player objects. Updates include: Team pokemon, health, PP, Moves and move type,
	 * pokemon status, and checks for game over states.
	 */
	@Override
	public void update(Observable o, Object arg) {
		CombatController updater = (CombatController) o;
		ArrayList<Player> updateList = (ArrayList<Player>) arg;
		this.p1 = updateList.get(0);
		this.p2 = updateList.get(1);
		
		//Update Player 1 and 2 Health elements
		battlePlayerPokeName.setText(p1.getActiveMon().getName());
		double hpBar = (double) p1.getActiveMon().getCurrHp() / (double) p1.getActiveMon().getMaxHp();
		battlePlayerHealthBar.setProgress(hpBar);													
		battlePlayerHP.setText(" HP: " + p1.getActiveMon().getCurrHp());;
		battleNPCPokeName.setText(p2.getActiveMon().getName());
		double hpBar2 = (double) p2.getActiveMon().getCurrHp() / (double) p2.getActiveMon().getMaxHp();
		battleNPCHealthBar.setProgress(hpBar2);
		battleNPCHealthText.setText("HP: " + p2.getActiveMon().getCurrHp());
		
		// Game over screens
		if (p1.hasLost() && !cc.getMultiplayer()) {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Message");
			a.setContentText("Message");
			a.setHeaderText("You lost.");
			a.showAndWait();
			levelScene = levelsUI();
            stage.setScene(levelScene);
		} else if (p1.hasLost() && cc.getMultiplayer() && !toggledEndGameScreen) {
			toggledEndGameScreen = true;
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Message");
			a.setContentText("Message");
			a.setHeaderText("Player 2 Wins!");
			a.showAndWait();
			if (cc.getNetworkMultiplayer()) {
				stage.setScene(multiplayerScene);
			} else {
				model.beatLevel(selectedChallenge);
				levelScene = levelsUI();
	            stage.setScene(levelScene);
			}
		} else if (p2.hasLost() && !cc.getMultiplayer()) {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Message");
			a.setContentText("Message");
			a.setHeaderText("You won!");
			a.showAndWait();
			model.beatLevel(selectedChallenge);
			levelScene = levelsUI();
            stage.setScene(levelScene);
		} else if (p2.hasLost() && cc.getMultiplayer()  && !toggledEndGameScreen) {
			toggledEndGameScreen = true;
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Message");
			a.setContentText("Message");
			a.setHeaderText("Player 1 Wins!");
			a.showAndWait();
			if (cc.getNetworkMultiplayer()) {
				stage.setScene(multiplayerScene);
			} else {
				model.beatLevel(selectedChallenge);
				levelScene = levelsUI();
	            stage.setScene(levelScene);
			}
		}
		
		// If player mon faints, allowed for selection of new one
		if (p1.getActiveMon().hasFainted() && p1.livingMonCount() >= 1 && cc.getPlayerID() == 1 && cc.getNetworkMultiplayer()) {
			monSelect.setVisible(true);
		} else if (p2.getActiveMon().hasFainted() && p2.livingMonCount() >= 1 && cc.getPlayerID() == 2 && cc.getNetworkMultiplayer()) {
			monSelect.setVisible(true);
		} else if (p1.getActiveMon().hasFainted() && p1.livingMonCount() >= 1 && cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
			monSelect.setVisible(true);
		} else if (p2.getActiveMon().hasFainted() && p2.livingMonCount() >= 1 && !cc.getPlayerTurn() && cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
			monSelect.setVisible(true);
		} else if (p1.getActiveMon().hasFainted() && p1.livingMonCount() >= 1 && !cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
			monSelect.setVisible(true);
		}
		
		//Update notification bar
		if (cc.getNetworkMultiplayer()) {
			if (cc.getPlayerID() == 1 && updater.getMPTurn()) {
				notificationText.setText("What will\n" + p1.getActiveMon().getName() + " do?");
			} else if (cc.getPlayerID() == 2 && updater.getMPTurn()){
				notificationText.setText("What will\n" + p2.getActiveMon().getName() + " do?");
			} else if (cc.getPlayerID() == 1 && !updater.getMPTurn()) {
				notificationText.setText("What will\n" + p2.getActiveMon().getName() + " do?");
			} else if (cc.getPlayerID() == 2 && !updater.getMPTurn()) {
				notificationText.setText("What will\n" + p1.getActiveMon().getName() + " do?");
			}
		} else if (cc.getMultiplayer() && !cc.getNetworkMultiplayer()) {
			if (cc.getPlayerTurn()) {
				notificationText.setText("What will\n" + p1.getActiveMon().getName() + " do?");
			} else {
				notificationText.setText("What will\n" + p2.getActiveMon().getName() + " do?");
			}
		} else {
			notificationText.setText("What will\n" + p1.getActiveMon().getName() + " do?");
		}
	
		//Update Player 1 and 2 Pokemon sprites
		Image playerPokemon = new Image(getClass().getResourceAsStream(p1.getActiveMon().getPlayerSprite()), 100, 100, false, false);
		playerSpriteView.setImage(playerPokemon);

		Image player2Pokemon = new Image(getClass().getResourceAsStream(p2.getActiveMon().getOppSprite()), 100, 100, false, false);
		player2SpriteView.setImage(player2Pokemon);
		
		// Update mons for current player
		if (cc.getNetworkMultiplayer() && cc.getPlayerID() == 1) {
			if (p1.teamSize() > 1) { pokeSlot1Name.setText(p1.getMon(1).getName()); 
			} else {
				pokeSlot1Name.setText("-");
			}
			if (p1.teamSize() > 2) { pokeSlot2Name.setText(p1.getMon(2).getName()); 
			} else {
				pokeSlot2Name.setText("-");
			}
			if (p1.teamSize() > 3) { pokeSlot3Name.setText(p1.getMon(3).getName()); 
			} else {
				pokeSlot3Name.setText("-");
			}
		} else if (cc.getNetworkMultiplayer() && cc.getPlayerID() == 2) { 
			if (p2.teamSize() > 1) { pokeSlot1Name.setText(p2.getMon(1).getName()); 
			} else {
				pokeSlot1Name.setText("-");
			}
			if (p2.teamSize() > 2) { pokeSlot2Name.setText(p2.getMon(2).getName()); 
			} else {
				pokeSlot2Name.setText("-");
			}
			if (p2.teamSize() > 3) { pokeSlot3Name.setText(p2.getMon(3).getName()); 
			} else {
				pokeSlot3Name.setText("-");
			}
		} else if (!cc.getPlayerTurn() && cc.getMultiplayer()) {
			if (p2.teamSize() > 1) { pokeSlot1Name.setText(p2.getMon(1).getName()); 
			} else {
				pokeSlot1Name.setText("-");
			}
			if (p2.teamSize() > 2) { pokeSlot2Name.setText(p2.getMon(2).getName()); 
			} else {
				pokeSlot2Name.setText("-");
			}
			if (p2.teamSize() > 3) { pokeSlot3Name.setText(p2.getMon(3).getName()); 
			} else {
				pokeSlot3Name.setText("-");
			}
		} else if (cc.getMultiplayer() && cc.getPlayerTurn()) {
			if (p1.teamSize() > 1) { pokeSlot1Name.setText(p1.getMon(1).getName()); 
			} else {
				pokeSlot1Name.setText("-");
			}
			if (p1.teamSize() > 2) { pokeSlot2Name.setText(p1.getMon(2).getName()); 
			} else {
				pokeSlot2Name.setText("-");
			}
			if (p1.teamSize() > 3) { pokeSlot3Name.setText(p1.getMon(3).getName()); 
			} else {
				pokeSlot3Name.setText("-");
			}
		} else if (!cc.getMultiplayer()) {
			if (p1.teamSize() > 1) { pokeSlot1Name.setText(p1.getMon(1).getName()); 
			} else {
				pokeSlot1Name.setText("-");
			}
			if (p1.teamSize() > 2) { pokeSlot2Name.setText(p1.getMon(2).getName()); 
			} else {
				pokeSlot2Name.setText("-");
			}
			if (p1.teamSize() > 3) { pokeSlot3Name.setText(p1.getMon(3).getName()); 
			} else {
				pokeSlot3Name.setText("-");
			}
		}
		
		//Update Moves
		if (!cc.getNetworkMultiplayer() && playerMoves.size() > 0) {
			if ((cc.getPlayerTurn() && cc.getMultiplayer() ||!cc.getMultiplayer())) {
				moves = p1.getActiveMon().getAllMoves();
				for (int i = 0; i < moves.length; i++) {
					playerMoves.get(i).setText(moves[i].getName());
				}
			} else if (!cc.getPlayerTurn() && cc.getMultiplayer()) {
				moves = p2.getActiveMon().getAllMoves();
				for (int i = 0; i < moves.length; i++) {
					playerMoves.get(i).setText(moves[i].getName());
				}
			}
		} else if (playerMoves.size() > 0) {
			if (cc.getNetworkMultiplayer() && cc.getPlayerID() == 1) {
				moves = p1.getActiveMon().getAllMoves();
				for (int i = 0; i < moves.length; i++) {
					playerMoves.get(i).setText(moves[i].getName());
				}
			} else if (cc.getNetworkMultiplayer() && cc.getPlayerID() == 2){
				moves = p2.getActiveMon().getAllMoves();
				for (int i = 0; i < moves.length; i++) {
					playerMoves.get(i).setText(moves[i].getName());
				}
			}
		}
			
	}
}