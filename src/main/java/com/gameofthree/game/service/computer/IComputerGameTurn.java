package com.gameofthree.game.service.computer;

import com.gameofthree.game.entities.InputNumber;
import com.gameofthree.game.entities.OutputNumber;

public interface IComputerGameTurn {


    InputNumber calculateNextInputNumberFor(OutputNumber outputNumber);

}
