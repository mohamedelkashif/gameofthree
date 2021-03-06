package com.gameofthree;

import com.gameofthree.client.Client;
import com.gameofthree.server.sockets.SocketIOHandler;
import com.gameofthree.utils.PropertiesConfigLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.JVM)
public class AcceptanceTests {

    static {
        try {
            PropertiesConfigLoader.initialize("application.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final String SERVER_IP = PropertiesConfigLoader.getProperties().getProperty("ip");
    private static final String SERVER_PORT = PropertiesConfigLoader.getProperties().getProperty("port");
    private static final int TEST_TIMEOUT = 3000;



    private ExecutorService executorService;

    private Client testClient1;
    private Client testClient2;
    private SocketIOHandler socketPlayer1;
    private SocketIOHandler socketPlayer2;


    @Before
    public void startServer() throws Exception {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> GameOfThreeServer.main(new String[]{}));


        //wait for server under test to start external sockets connections.
        Thread.sleep(200);

        //start test testClient1
        testClient1 = new Client();
        socketPlayer1 = testClient1.start(SERVER_IP, Integer.parseInt(SERVER_PORT));

        Thread.sleep(100);

        //start test testClient2
        testClient2 = new Client();
        socketPlayer2 = testClient2.start(SERVER_IP, Integer.parseInt(SERVER_PORT));
        Thread.sleep(100);
    }

    @After
    public void tearDown() throws Exception {
        socketPlayer1.sendWithDelay("EXIT");
        socketPlayer2.sendWithDelay("EXIT");

        executorService.shutdown();
        executorService.awaitTermination(500, MILLISECONDS); // waiting max time for server under test to shutdown
        executorService.shutdownNow();

        testClient1.stop();
        testClient2.stop();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReceiveConnectedWhenNewConnectionIsMade() {
        assertEquals("Player 1 should receive connected first.", "connected", socketPlayer1.readNextLineSync());
        assertEquals("Player 2 should receive connected first.", "connected", socketPlayer2.readNextLineSync());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReceiveUnknownCommandWhenNotRecognisingCommand() {
        //given connected is read
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("some unknown command");

        //then
        assertTrue("Player 1 should receive unknown command.", socketPlayer1.readNextLineSync().startsWith("unknown command"));
        assertTrue("Player 2 should not receive anything.", socketPlayer2.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAcceptHumanPlayers() {
        //given connected is read
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        //then
        assertEquals("Player 1 should receive added player1.", "Added player player1 to game.", socketPlayer1.readNextLineSync());
        assertEquals("Player 2 should receive added player1.", "Added player player1 to game.", socketPlayer2.readNextLineSync());

        //and when
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        //then
        assertEquals("Player 1 should receive added player2.", "Added player player2 to game.", socketPlayer1.readNextLineSync());
        assertEquals("Player 2 should receive added player2.", "Added player player2 to game.", socketPlayer2.readNextLineSync());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAcceptMachinePlayers() {
        //given connected is read
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("ADD_MACHINE");

        //then
        assertThat("Player1 should receive added machine1.", socketPlayer1.readNextLineSync(),
                matchesPattern("Added computer player Computer[0-9]+ to game."));
        assertThat("Player2 should receive added machine1.", socketPlayer2.readNextLineSync(),
                matchesPattern("Added computer player Computer[0-9]+ to game."));

        //and when
        socketPlayer2.sendWithDelay("ADD_MACHINE");

        //then
        assertThat("Player1 should receive added machine1.", socketPlayer1.readNextLineSync(),
                matchesPattern("Added computer player Computer[0-9]+ to game."));
        assertThat("Player2 should receive added machine1.", socketPlayer2.readNextLineSync(),
                matchesPattern("Added computer player Computer[0-9]+ to game."));
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAcceptOnlyOnePlayerPerInputStream() {
        //given connected is read
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");

        //then
        assertEquals("Player 1 should receive error: can not add another player.",
                "ERROR: can not add another player", socketPlayer1.readNextLineSync());
        assertTrue("Player 2 should not receive anything.", socketPlayer2.inputIsEmpty());

        //and when
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");

        //then
        assertEquals("Player 1 should receive added player2.", "Added player player2 to game.", socketPlayer1.readNextLineSync());
        assertEquals("Player 2 should receive added player2.", "Added player player2 to game.", socketPlayer2.readNextLineSync());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotAcceptMorePlayersThanConfigured() {
        //given connected is read
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer1.sendWithDelay("ADD_MACHINE");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer2.sendWithDelay("ADD_PLAYER:player1");

        //then
        assertThat("Player 2 should receive error: can not add another player.", socketPlayer2.readNextLineSync(),
                matchesPattern("ERROR: limit reached, can not add more players: \\[player player1, computer Computer[0-9]+\\] and player 1 has next turn."));
        assertTrue("Player 1 should not receive anything.", socketPlayer1.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldStartGameIfHumansPlayers() {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("START");

        //then
        assertThat("Player 1 should receive game started with number and next player.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is player player1."));
        assertThat("Player 2 should receive starting game by player1 with output number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is player player1."));
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotBeAbleToStartGameWithInvalidPlayers() throws InterruptedException {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("START");

        //then
        assertEquals("Player 1 should not be able to start game with invalid players.",
                "ERROR: Game initialization failed due to invalid players.", socketPlayer1.readNextLineSync());
        assertTrue("Player 2 should not receive anything.", socketPlayer2.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotBeAbleToPlayNotStartedGameWithInvalidPlayers() {
        //given
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("PLAY:1");

        //then
        assertEquals("Should not be able to play game that was not started and has invalid players.",
                "ERROR: can not play game when players: [] and player 1 has next turn.", socketPlayer1.readNextLineSync());
        assertTrue("Player 2 should not receive anything.", socketPlayer2.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotBeAbleToPlayGoodNumberIfNotItsTurn() throws InterruptedException {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer2.sendWithDelay("PLAY:0");

        //then
        assertTrue("Player 1 should not receive anything.", socketPlayer1.inputIsEmpty());
        assertThat("Player 2 should not be able to play when not it's turn.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("ERROR: It is not your turn to play."));

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotBeAbleToPlayInvalidNumberIfNotItsTurn() throws InterruptedException {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer2.sendWithDelay("PLAY:999");

        //then
        assertTrue("Player 1 should not receive anything.", socketPlayer1.inputIsEmpty());
        assertThat("Player 2 should not be able to play when not it's turn.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("ERROR: It is not your turn to play."));

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotPlayInvalidNumberIfItsTurn(){
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("PLAY:999");

        //then
        assertEquals("Player1 should not play with incorrect input number.",
                socketPlayer1.readNextLineSync(), "ERROR: can not play game because 999 is not within [-1, 0, 1]");
        assertTrue("Player2 should not receive anything.", socketPlayer2.inputIsEmpty());

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldPlayValidNumber(){
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("PLAY:-1");

        //then
        assertThat("Player1 should play 1 and receive success message.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("player player1 played number -1. The result is Round result:outputNumber 21, winner false."));
        assertThat("Player2 should receive that player1 played successfully.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("player player1 played number -1. The result is Round result:outputNumber 21, winner false."));
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotPlayWrongAdditionInput(){
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("PLAY:0");

        //then
        assertEquals("Player1 should not be able to play wrong addition input.",
                "ERROR: could not play this round because of invalid input last round output was 64 and your input was 0",
                socketPlayer1.readNextLineSync());
        assertTrue("Player 2 should not receive anything.", socketPlayer2.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotPlayBadInputType(){
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("PLAY:not_number");

        //then
        assertEquals("Should not play bad input data types.",
                "ERROR: For input string: \"not_number\"",
                socketPlayer1.readNextLineSync());
        assertTrue("Player 2 should not receive anything.", socketPlayer2.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldBeAbleToRestartAnAlreadyInitializedGame(){
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.sendWithDelay("PLAY:0");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("START");

        //then
        assertThat("Player1 should be able to restart an already started game.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is player player1."));
        assertThat("Player2 should receive same message when player1 is able to restart an already started game.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is player player1."));
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldBeAbleToGetCurrentStateOfNewGame(){
        //given
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("STATE");

        //then
        assertEquals("Player1 should be able to get correct current state of new game.",
                "Currently playing players: [] and player 1 has next turn. Last Round result:outputNumber null, winner false.",
                socketPlayer1.readNextLineSync());
        assertTrue("Player2 should not receive anything.", socketPlayer2.inputIsEmpty());

        //and when
        socketPlayer2.sendWithDelay("STATE");

        //then
        assertTrue("Player1 should not receive anything.", socketPlayer1.inputIsEmpty());
        assertEquals("Player2 should be able to get correct current state of new game.",
                "Currently playing players: [] and player 1 has next turn. Last Round result:outputNumber null, winner false.", socketPlayer2.readNextLineSync());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldBeAbleToGetCurrentStateOfStartedGame() throws InterruptedException {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("STATE");

        //then
        assertEquals("Player1 should be able to get correct current state of game.",
                socketPlayer1.readNextLineSync(),
                "Currently playing players: [player player1, player player2] and player 1 has next turn. Last Round result:outputNumber 64, winner false.");
        assertTrue("Player2 should not receive anything.", socketPlayer2.inputIsEmpty());

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldBeAbleToGetCurrentStateOfPlayingGameAfterSuccessfulAttempt(){
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("STATE");

        //then
        assertEquals("Player1 should be able to get correct current state of game after a failed attempt.",
                socketPlayer1.readNextLineSync(),
                "Currently playing players: [player player1, player player2] and player 2 has next turn. Last Round result:outputNumber 21, winner false.");
        assertTrue("Player2 should not receive anything.", socketPlayer2.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldBeAbleToGetCurrentStateOfPlayingGameAfterFailedAttempt(){
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:AAA");
        socketPlayer2.sendWithDelay("ADD_PLAYER:BBB");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer1.sendWithDelay("PLAY:999");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("STATE");

        //then
        assertEquals("Player1 should be able to get correct current state of game after a failed attempt.",
                socketPlayer1.readNextLineSync(),
                "Currently playing players: [player AAA, player BBB] and player 2 has next turn. Last Round result:outputNumber 21, winner false.");
        assertTrue("Player2 should not receive anything.", socketPlayer2.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldBeAbleToPlayUntilWinning() {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer2.sendWithDelay("PLAY:0");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer2.sendWithDelay("PLAY:1");

        //then
        assertThat("Player2 should be able to play until winning.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("player player2 played number 1. The result is Round result:outputNumber 1, winner true."));
        assertThat("Player1 should receive same message when checking if able to play until winning.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("player player2 played number 1. The result is Round result:outputNumber 1, winner true."));
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotBeAbleToPlayAfterWinning() {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer2.sendWithDelay("PLAY:0");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer2.sendWithDelay("PLAY:1");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("PLAY:1");

        //then
        assertThat("Player1 should not be able to play after winning.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("ERROR: can not play game when Round result:outputNumber 1, winner true."));
        assertTrue("Player2 should not receive anything.", socketPlayer2.inputIsEmpty());
    }


    @Test(timeout = TEST_TIMEOUT)
    public void shouldBeAbleToStartNewGameAfterWinning() {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer2.sendWithDelay("PLAY:0");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer2.sendWithDelay("PLAY:1");
        socketPlayer1.sendWithDelay("PLAY:1");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer2.sendWithDelay("START");

        //then
        assertThat("Player1 should receive new game started.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is player player1."));
        assertThat("Player2 should receive new game started.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is player player1."));
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldPlayGameWithMachineWhenPlayerIsFirst() {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer1.sendWithDelay("ADD_MACHINE");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("START");

        //then
        assertThat("Player1 should receive starting game by player1 with output number.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is player player1."));
        assertThat("Player2 should receive starting game by player1 with output number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is player player1."));

        //and when
        socketPlayer1.sendWithDelay("PLAY:-1");

        //and then
        assertThat("Player1 should receive that Player1 1 played correct input number.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("player player1 played number -1. The result is Round result:outputNumber 21, winner false."));
        assertThat("Player2 should receive that Player 1 played correct input number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("player player1 played number -1. The result is Round result:outputNumber 21, winner false."));

        //and then
        assertThat("Player1 should receive that Machine1 played correct input number.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number 0. The result is Round result:outputNumber 7, winner false."));
        assertThat("Player2 should receive that Machine 1 played correct input number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number 0. The result is Round result:outputNumber 7, winner false."));
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldStartAndPlayGameWhenMachineAndHuman() throws InterruptedException {
        //given
        socketPlayer1.sendWithDelay("ADD_MACHINE");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("START");

        //then
        assertThat("Player1 should receive game started with number and next player.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is computer Computer[0-9]+."));
        assertThat("Player2 should receive starting game by player1 with output number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is computer Computer[0-9]+."));

        //and then
        assertThat("Player1 should receive that Machine 1 played correct input number.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number -1. The result is Round result:outputNumber 21, winner false."));
        assertThat("Player2 should receive that Machine 1 played correct input number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number -1. The result is Round result:outputNumber 21, winner false."));
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldStartAndPlayGameWhenMachineAndMachine() {
        //given
        socketPlayer1.sendWithDelay("ADD_MACHINE");
        socketPlayer2.sendWithDelay("ADD_MACHINE");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("START");

        //then
        assertThat("Player1 should receive game started with number and next player.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is computer Computer[0-9]+."));
        assertThat("Player2 should receive starting game by player1 with output number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("Game started. The starting number is 64. Next to play is computer Computer[0-9]+."));

        //and then
        assertThat("Player1 should receive that Machine 1 played correct input number.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number -1. The result is Round result:outputNumber 21, winner false."));
        assertThat("Player2 should receive that Machine 1 played correct input number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number -1. The result is Round result:outputNumber 21, winner false."));

        //and then
        assertThat("Player1 should receive that Machine 1 played correct input number.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number 0. The result is Round result:outputNumber 7, winner false."));
        assertThat("Player2 should receive that Machine 1 played correct input number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number 0. The result is Round result:outputNumber 7, winner false."));

        //and then
        assertThat("Player1 should receive that Machine 1 played correct input number.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number -1. The result is Round result:outputNumber 2, winner false."));
        assertThat("Player2 should receive that Machine 1 played correct input number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number -1. The result is Round result:outputNumber 2, winner false."));

        //and then
        assertThat("Player1 should receive that Machine 1 played correct input number.",
                socketPlayer1.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number 1. The result is Round result:outputNumber 1, winner true."));
        assertThat("Player2 should receive that Machine 1 played correct input number.",
                socketPlayer2.readNextLineSync(),
                matchesPattern("computer Computer[0-9]+ played number 1. The result is Round result:outputNumber 1, winner true."));
    }


    @Test(timeout = TEST_TIMEOUT)
    public void whenExitPlayerShouldReceivePlayerExited() {
        //given
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer2.sendWithDelay("EXIT");

        //then
        assertEquals("Player 2 should receive goodbye.",
                "Quitting", socketPlayer2.readNextLineSync());
        assertTrue("Player 1 should not receive anything.", socketPlayer1.inputIsEmpty());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void whenExitPlayerShouldRemoveFromAggregate() {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer2.sendWithDelay("EXIT");
        socketPlayer1.clearInput();

        //when
        socketPlayer1.sendWithDelay("STATE");

        //then
        assertEquals("Player2 should be unregistred.",
                "Currently playing players: [player player1] and player 2 has next turn. Last Round result:outputNumber null, winner false.",
                socketPlayer1.readNextLineSync());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotBeAbleToPlayAloneWhenOtherPlayerExited() {
        //given
        socketPlayer1.sendWithDelay("ADD_PLAYER:player1");
        socketPlayer2.sendWithDelay("ADD_PLAYER:player2");
        socketPlayer1.sendWithDelay("START");
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer2.sendWithDelay("PLAY:0");
        socketPlayer2.sendWithDelay("EXIT");

        socketPlayer1.clearInput();
        socketPlayer2.clearInput();

        //when
        socketPlayer1.sendWithDelay("PLAY:-1");
        socketPlayer1.sendWithDelay("PLAY:1");
        socketPlayer1.sendWithDelay("PLAY:1");

        //then
        assertEquals("Player1 should not be able to play when left alone.",
                socketPlayer1.readNextLineSync(),
                "ERROR: can not play game when players: [player player1] and player 1 has next turn.");
    }

}
