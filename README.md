# CNC_Plotter
Software for creation of images using arduino controller and stepper motors

So since it was pretty difficult to find a working instructable for a cnc plotter, with software and compatible Gcode images,
here I give it all for you.

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
6. Close the compiler (COM for the arduino must be free)
7. Run the bat from "Program to PC" folder
8. Choose your CNC COM port and play with buttons.

Multiple instances can control more CNC plotters from one computer.
