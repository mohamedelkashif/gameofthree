package com.gameofthree.controller;

import com.gameofthree.controller.commands.AddComputerCommand;
import com.gameofthree.controller.commands.AddHumanCommand;
import com.gameofthree.controller.commands.ExitCommand;
import com.gameofthree.controller.commands.PlayCommand;
import com.gameofthree.controller.commands.PlayComputerCommand;
import com.gameofthree.controller.commands.StartCommand;
import com.gameofthree.controller.commands.StateCommand;
import com.gameofthree.controller.commands.UnknownCommand;
import com.gameofthree.controller.mapper.UserInputDeserializer;
import com.gameofthree.enums.CommandType;
import com.gameofthree.game.dtos.UserInputDTO;
import com.gameofthree.game.service.IGameService;
import com.gameofthree.game.service.computer.ComputerGameTurnService;
import com.gameofthree.server.sockets.ISocketIOHandler;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class GameCommandController implements Consumer<String> {

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;
    private UserInputDeserializer userInputDeserializer;


    public GameCommandController(IGameService gameService,
                                 ISocketIOHandler socketIOHandler,
                                 UserInputDeserializer userInputDeserializer) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
        this.userInputDeserializer = userInputDeserializer;

    }

    @Override
    public void accept(String input) {
        Stream.of(input)
                .map(userInputDeserializer)
                .filter(UserInputDTO::isValid)
                .forEach(this::runCommand);
    }

    private void runCommand(UserInputDTO userInputDto) {
        CommandType commandType = CommandType.valueOfCommandString(userInputDto.getCommand());

        switch (commandType) {
            case ADD_PLAYER:
                new AddHumanCommand(gameService, socketIOHandler).execute(userInputDto.getData());
                break;
            case ADD_MACHINE:
                new AddComputerCommand(gameService, socketIOHandler).execute(userInputDto.getData());
                break;
            case START:
                StartCommand start = new StartCommand(gameService, socketIOHandler);
                PlayComputerCommand playMachine = new PlayComputerCommand(gameService, socketIOHandler, new ComputerGameTurnService());
                start.setSuccessor(playMachine);
                start.execute(userInputDto.getData());
                break;
            case PLAY:
                PlayCommand playCommand = new PlayCommand(gameService, socketIOHandler);
                playCommand.setSuccessor(new PlayComputerCommand(gameService, socketIOHandler, new ComputerGameTurnService()));
                playCommand.execute(userInputDto.getData());
                break;
            case STATE:
                new StateCommand(gameService, socketIOHandler).execute(userInputDto.getData());
                break;
            case EXIT:
                new ExitCommand(gameService, socketIOHandler).execute(userInputDto.getData());
                break;
            default:
                new UnknownCommand(socketIOHandler).execute(userInputDto.getData());
        }
    }
}
