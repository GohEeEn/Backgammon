# Orange-Iguanas
<b>Author</b>  : Ee En Goh    - 17202691<br/>
<b>Author</b>  : Ferdia Fagan - 16372803<br/>
<b>Version</b> : 2.0 


## How to open the JAR file :
* We can't open our program by directly clicking on our created JAR file, which could due to one of the known bugs for running JavaFX runnable JAR files through clicking on it (as there are a lot of users complain on online too)<br>
* It could be caused by the different version of Java, JRE or SE<br>
* However we are able to execute it by using command prompt with the following command :<br>
* Command : <b>java -jar file_location/BackGammonSprint3.jar</b> <br>
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
8. Tick all the files (packages - game, gui, data_structures)
9. Click finish
10. Open and run main.java

## Commands to play in this backgammon program :

Commands  | Argument(s) | Description
----------|-------------|----------------------------------------------------------------------------------------------
double    | null        | Command to activate doubling dice play
cheat     | null        | Move all the checkers to defined pips (All the checkers to their corresponding home except 2 checkers on their ace points for each player)
quit      | null        | Quit the game

### Sprint 3 Requirements Done :
- [x] List the legal plays available to the current player as a menu (hits, bear offs and doubles)<br>
- [x] Legal move selection by typing in a letter corresponding to the play (Typing a referencing <b>integer</b> instead) <br>
- [ ] Display all plays on board (display Bear Off play case didn't been done on time)<br>
- [x] Command 'cheat' to make the checkers to move to the following position<br>
- [x] Notification to inform the user if no play is possible<br>
- [x] When the user enters "quit", the program should terminate<br>
- [x] When the game is over, announce the winning player<br>

