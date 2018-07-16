# CNC_Plotter
Software for creation of images using arduino controller and stepper motors

So since it was pretty difficult to find a working instructable for a cnc plotter, with software and compatible Gcode images,
here I give it all for you. This works for a project with arduino board and an L293D servo controller plug board.

Hadware-wise, arduino-uno pins PCINT12 and PCINT13 are used as a pull down motor home position switches I think. Look at the source
in branch Arduino-Program.

This repository is composed of two main parts:
1. Software for the arduino controller board
  This program is editable in regular Arduino IDE. It is operational. You just need to put it there and compile to the board.
2. Software for the computer
  So this is a thing I played with a little. It is a clone of one program which was edited in some weird portable Java IDE.
  It was so awfull and it did not work correctly, so I decided to port it to plain Java. You just edit the code and compile
  the jar file which you can use as you wish. Also, there is a simple bat file provided to automate the need of running the file
  with permissions to successfully connect to native libraries. From IDE it worked without permissions, when running through jar,
  for no reason it needs elevation. Oh, how awfull programing is.
  
------
TO MAKE IT JUST WORK:
1. Download release up there:
2. Download official arduino IDE: https://www.arduino.cc/en/Main/Software
3. Choose the correct com port
4. Open the project file (.ino file)
5. Compile to the board with the button and wait.
6. Close the IDE (COM for the arduino must be free)
7. Run the bat from the root of release folder, or run the jar file with administrator privileges
   (Natives loading error.. Stupid stuff.)
8. Choose your CNC COM port and play with buttons, or load an available image.

TO MAKE YOUR OWN IMAGE:
Use InkScape program and export the result as G-Code.
Video here (Sorry, I'm lazy now): https://www.youtube.com/watch?v=bbe56S_O-uI
If InkScape reports weird error, use older version of this program. You can search in which release it was not a case and use that.

Multiple instances can control more CNC plotters from one computer.

Repository contains IntelliJ Idea project, with settings ready to build. But if you want to use Eclipse or netbeans,
you can just use the src folder. Jar should contain the compile output of this folder with files of the 'native' folder
extracted in the root of the jar.

Tested on x64 system. Should work on other platforms but not tested.
Some features are not finished because I don't have a plotter yet. Feel free to fork and change whatever you want.

When running G-code streamer, it will extract a directory called "nativesPlotter". This folder will not be deleted
after the program finishes so please delete it when you stop using the program. It just serves the purpose of having
portable application with native files.
