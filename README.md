# Orange-Iguanas
<b>Author</b>  : Ee En Goh    - 17202691<br/>
<b>Author</b>  : Ferdia Fagan - 16372803<br/>
<b>Version</b> : 2.0 


## How to open the JAR file :
* We can't open our program by directly clicking on our created JAR file, which could due to one of the known bugs for running JavaFX runnable JAR files through clicking on it (as there are a lot of users complain on online too)<br>
* It could be caused by the different version of Java, JRE or SE<br>
* However we are able to execute it by using command prompt with the following command :<br>
* Command : <b>java -jar file_location/BackGammonSprint4.jar</b> <br>
* file_location : The full path name of where this JAR file stored-in<br>
* <b>PS : Our program is executable and playable in Eclipse<br/>


### How to run the program in Eclipse : (It works all the time when we test it)
1. Open eclipse and create a new project
2. Right click the newly created src folder
3. Click import
4. Click General -> File System -> Next
5. Click Browse
6. Browse to the folder that contains all the content of the repository
7. Select that folder
8. Tick all the files 
9. Click finish
10. Open and run Backgammon.java


## Commands to play in this backgammon program :

Commands  | Argument(s) | Description
----------|-------------|----------------------------------------------------------------------------------------------
double    | null        | Command to activate doubling and re-doubling dice play
cheat     | null        | Move all the checkers to defined pips (All the checkers to their corresponding home except 2 checkers on their ace points for each player)
quit      | null        | Quit the game

[ Our Game Hierarchy : Program > Games > Matches > Turns ]


### Sprint 4 Requirements Done :
- [x] Ask the players how many points they want to play to (set game point) <br>
- [x] Display the doubling cube, the match score and the match length on the board panel<br>
- [x] Allow players to enter 'double' as a command before they enter a play, if doubling is illegal at that time , display an error message<br>
- [x] Allow the player receiving the cube to accept or reject the double. If the double is accepted, update the doubling cube showing which player owns the cube. If the double is rejected, end the game and allocate the points to the winner<br>
- [x] When the game is over, update the match score on the board and report the new match score on the information panel. Ask the players to press any key for starting the next game in the match<br>
- [x] When the match is over, announce the winner and ask if the players want to play again (yes/no). If the user response is invalid, an error message should be displayed and the question should be asked again. If they choose to play again, the program should ask for the player names and game score again<br>
- [x] Modify the 'cheat' command so that the board goes to a position where all of the checkers have been bore off except that both players should have two checkers on their own ace points
- [x] The players should be able to enter 'quit' as a command at any time to quit the program
- [x] When the game is over, announce the winning player on the info panel (in current version)<br>

