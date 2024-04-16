# Reversi
[![Coverage Status](https://coveralls.io/repos/github/marc1107/Reversi/badge.svg?branch=main&service=github)](https://coveralls.io/github/marc1107/Reversi?branch=main)
[![codecov](https://codecov.io/gh/marc1107/Reversi/graph/badge.svg?token=ZC922JC7QS)](https://codecov.io/gh/marc1107/Reversi)

## How to Play

This is the game board:

![alt text](https://github.com/marc1107/Reversi/blob/main/Reversi.jpeg?raw=true)

Each piece played must be laid adjacent to an opponent's piece so that the opponent's piece or a row of opponent's
pieces is flanked by the new piece and another piece of the player's colour. All of the opponent's pieces between these
two pieces are 'captured' and turned over to match the player's colour.

It can happen that a piece is played so that pieces or rows of pieces in more than one direction are trapped between the
new piece played and other pieces of the same colour. In this case, all the pieces in all viable directions are turned
over.

The game is over when neither player has a legal move (i.e. a move that captures at least one opposing piece) or when
the board is full.