TWEditor - Version 2.2
----------------------

Overview
========

TWEditor allows you to modify save games created by The Witcher.  You can modify the attributes and abilities of the player character (Geralt). You can also unpack all of the files in the save, manually modify one or more of the files, and then repack the save.  Note that you can not add files to the save or delete files from the save.

The 'Stats' tab allows you to modify selected fields in the save game such as experience, orens and talents.  The modified values will be written when the file is saved.  Whether or not the changes are accepted when the save is loaded depends on the game engine.

The 'Attributes' tab allows you to modify Strength, Dexterity, Stamina and Intelligence selections.

The 'Signs' tab allows you to modify Aard, Igni, Quen, Axii and Yrden selections.

The 'Styles' tab allows you to modify Steel Sword and Silver Sword selections.

The 'Equipment' tab allows you to modify Geralt's equipped items and trophy.

The 'Inventory' tab allows you to modify Geralt's inventory.

The 'Quests' tab shows the game quests (Started, Completed, Failed and Not Started).  The 'Examine' button will display a description of the current quest stage (if the stage has a description).

The 'Difficulty' tab allows you to modify difficulty level.


Installation
============

This version of the save game editor assumes you have installed the Enhanced Edition of The Witcher.  Using this version of the editor with the original version of The Witcher can result in inventory errors.

To install this utility, place the TWEditor.jar file into a directory of your choice.  To run the utility, create a program shortcut and specify 

  `javaw -Xmx256m -jar TWEditor.jar`

as the program to run.  Set the Start Directory to the directory where you extracted the jar file.  A sample program shortcut is included.  The `-Xmx256m` argument specifies the maximum heap size in megabytes (the example specifies a heap of 256Mb).  You can increase the size if you run out of space processing very large saves.  Note that Windows will start swapping if the Java heap size exceeds the amount of available storage and this will significantly impact performance.  The java virtual machine will fail to start if the requested heap size is too large.

The Sun Java 1.8 runtime is required.  You can download JRE 1.8 from http://java.com/download/index.jsp.  If you are unsure what version of Java is installed on your system, open a command prompt window and enter `java -version`.

The game install directory is located by scanning the Windows registry.  If this scan fails or if the game files are located in a different directory, you can specify the game install directory when starting the editor.  This is done by specifying -DTW.install.path="<path>" on the java command line where <path> is the directory containing dialog.tlk.  For example, if the game files are located in C:\Games\The Witcher and the editor is installed in C:\Games, the shortcut would look like this:

  `javaw -DTW.install.path="C:\Games\The Witcher" -jar TWEditor.jar`

Don't forget to put double quotes around the path name.

The language identifier is determined by scanning the windows registry.  If this scan fails or you want to use a different language, you can specify the language identifier when starting the editor.  This is done by specifying -DTW.language=n on the java command line where 'n' is the language identifier for the associated .tlk file.  For example, US English would be specified as:

  `javaw -DTW.language=3 -jar TWEditor.jar`

The game data directory is assumed to be `The Witcher` in the user documents folder (*My Documents* on an English-language system).  If the save games are located in another directory, you can specify the game data directory when starting the editor.  This is done by specifying `-DTW.data.path="<path>"` on the java command line where <path> is directory containing the game data.  For example, if the user login is `Ronald Hoffman`, the normal game data directory would be `C:\Documents and Settings\Ronald Hoffman\My Documents\The Witcher`.

The Java runtime will sometimes throws a null pointer exception when adding the shell folders to the file chooser dialog (JFileChooser).  If this happens, you can disable the shell folders by specifying `-DUseShellFolder=0` on the java command line.

ScripterRon - Ronald.Hoffman6@gmail.com

--------------------------------------------------

Version 1.0:
============
Initial release.


Version 1.1:
============
Add inventory support (add/remove/examine)


Version 1.2:
============
An open input stream was causing the save to intermittently fail.

The game mnemonic for the Axii sign is 'Axi' and not 'Axii'.  This caused failures when editing the Axii sign.


Version 1.3:
============
Open saves created on a Russian system.

Add 'Quests' tab.


Version 1.4:
============
Use the maximum stack size when adding an item to the inventory.


Version 1.5:
============
Fix null pointer exception when modifying a sign and no signs have been learned yet.


Version 2.0:
============
Support multiple installed languages.

Add the ability to repack a save file.

Support for the expanded inventory management scheme implemented in the Enhanced Edition.


Version 2.1:
============
Support equipped items.

Version 2.2:
============
Difficulty support.
