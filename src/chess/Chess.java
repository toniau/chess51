package chess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * <p>A non-graphical character-based chess game.
 * The "main" function is defined in this class.
 *
 * <p>
 * To run this chess program:
 * <p>
 * &nbsp;&nbsp;&nbsp; java chess.Chess &nbsp;  &lt;optional playbackFileName&gt;
 * <p>
 *
 *
 * @author Toni Au, Sean Wu
 * @version 1.0
 *
 * @see  java.io.File
 * @see  java.io.BufferedReader
 * @see  java.util.ArrayList
 * @see  java.util.Scanner
 *
 */
public class Chess {

	/**
	 *
	 * Enumerated states of a chess game
	 *
	 */
	public static enum gameState {
		/**
		 *   White's turn to move
		 */
		whiteMove,

		/**
		 *   Black's turn to move
		 */
		blackMove,

		/**
		 *   White resigned from the game
		 */
		whiteResign,

		/**
		 *  Black resigned from the game
		 */
		blackResign,

		/**
		 *   Accepted a draw proposal offered by opponent during the previous turn
		 */
		drawAccepted,

		/**
		 *   The current side who is due to make a move is in checkmate.
		 *   Therefore, the opposing side has won this chess game.
		 */
		checkMate,

		/**
		 *   There is no legal move in this turn for the current side.  However,
		 *   the current side is not under a check either.  Therefore, this
		 *   created a "stalemate" in the chess game.
		 */
		staleMate};


	/**
	 *  Current state of this chess game.  White always move first.
	 */
	public static gameState gGameState = gameState.whiteMove;


	/**
	 *  Enumerated pawn promotion choices
	 *
	 */
	public static enum pawnPromotionChoices {
		/**
		 *  Promote a pawn to a rook
		 */
		rook,

		/**
		 *  Promote a pawn to a knight
		 */
		knight,

		/**
		 *  Promote a pawn to a bishop
		 */
		bishop,

		/**
		 *  Promote a pawn to a queen
		 */
		queen };


	/**
	 *   A pawn is promoted to another piece when it reaches the last rank.
	 *   There are four promotion choices: rook, knight, bishop, or queen.
	 *   By default, a pawn is promoted to a new queen.
	 *
	 */
	public static pawnPromotionChoices  pawnPromotionChoice = pawnPromotionChoices.queen;

	/**
	 *  Set to "true" if white is in check
	 */
	public static boolean bWhiteInCheck = false;

	/**
	 *  Set to "true" if black is in check
	 */
	public static boolean bBlackInCheck = false;

	/**
	 *  Set to "true" if one side offered a "draw" to his opponent
	 */
	public static boolean bDrawOffered = false;

	private static ArrayList<String> movements = new ArrayList<String>();



	/**
	 * @param args
	 *
	 * An optional input file name used to auto playback
	 * a previous chess moves.
	 *
	 */
	public static void main(String[] args) {

		if ( args.length >= 1 ) {
			// We are given an input file
			readInputFile(args[0].trim());
		}

		// Initialize the chess game board
		Board  board = new Board();
		board.show();
		gGameState = gameState.whiteMove;  // White moves first

		Scanner keyboardInput = new Scanner(System.in);

		while ( ! isGameOver() ) {
			// Read a move or a command from the user
			String token1 = "", token2 = "", token3 = "";
			String userInput = getNextMove(keyboardInput);

			if ( ! userInput.isEmpty() ) {
				StringTokenizer st = new StringTokenizer(userInput, " ");
				if (st.hasMoreTokens()) token1 = st.nextToken().trim().toLowerCase();
				if (st.hasMoreTokens()) token2 = st.nextToken().trim().toLowerCase();
				if (st.hasMoreTokens()) token3 = st.nextToken().trim().toLowerCase();
			}

			// Execute the given "move" or command
			switch (token1) {
				case "resign":
					if ( gGameState == gameState.whiteMove) {
						gGameState = gameState.whiteResign;
					}
					else {
						gGameState = gameState.blackResign;
					}
					break;

				case "draw":
					if ( bDrawOffered ) {
						gGameState = gameState.drawAccepted;
					}
					else {
						// illegal command.  You cannot accept a draw
						// that has not been offered by your opponent.
						System.out.println("Illegal move, try again");
					}
					break;

				default:
					// Did not accept opponent's request for a draw
					bDrawOffered = false;

					// Decode the third optional command token
					switch (token3) {
						case "r":
							pawnPromotionChoice = pawnPromotionChoices.rook;
							break;

						case "n":
							pawnPromotionChoice = pawnPromotionChoices.knight;
							break;

						case "b":
							pawnPromotionChoice = pawnPromotionChoices.bishop;
							break;

						case "q":
						default:
							pawnPromotionChoice = pawnPromotionChoices.queen;
					}

					// Offer a draw request to my opponent
					if ( token3.equals("draw?") ) bDrawOffered = true;

					// move a game piece
					if ( board.move(token1, token2) ) {
						// valid move
						if (gGameState != gameState.checkMate) {
							//
							// This game will continue to alternate turns
							// if it is not ended in "checkmate".
							//
							if ( gGameState == gameState.whiteMove ) {
								gGameState = gameState.blackMove;
							}
							else if ( gGameState == gameState.blackMove ) {
								gGameState = gameState.whiteMove;
							}
						}

					}
					else {
						// illegal move
						System.out.println("Illegal move, try again");
					}
			} //end of switch case statement

		}  // end of while loop

		keyboardInput.close();
	}



	private static boolean isGameOver()
	{
		boolean bGameOver = false;

		switch ( gGameState ) {
			case whiteMove:
				System.out.print("White's move: ");
				break;

			case blackMove:
				System.out.print("Black's move: ");
				break;

			case whiteResign:
				System.out.print("Black wins");
				bGameOver = true;
				break;

			case blackResign:
				System.out.print("White wins");
				bGameOver = true;
				break;

			case drawAccepted:
				bGameOver = true;
				break;

			case checkMate:
				if (bWhiteInCheck) {
					System.out.print("Black wins");
				}
				else {
					System.out.print("White wins");
				}
				bGameOver = true;
				break;

			case staleMate:
				bGameOver = true;
				break;

			default:
		}

		return bGameOver;
	}



	private static String getNextMove(Scanner keyboardInput)
	{
		String nextMove = "";

		if ( ! movements.isEmpty() ) {
			nextMove = movements.remove(0);
			System.out.println(nextMove);
		}
		else {
			nextMove = keyboardInput.nextLine();
		}

		System.out.println();
		return nextMove;
	}



	/*
	 * Read each line from the input file and store the line
	 * in an ArrayList of type String.
	 */
	private static void readInputFile(final String fileName)
	{
		movements.clear();
		if ( fileName.isEmpty() ) return;

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = in.readLine()) != null) {
				if ( ! line.isEmpty() ) movements.add(line);
			}
			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}



}
