import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

import javafx.stage.FileChooser;

public class main {
    static BufferedWriter out = null;
    static JFrame frame = new JFrame("Gcode Streamer");

    static CMD cmd = new CMD();
    public static void consoleWrite(String s){
            cmd.log(s);
            //out.write(s + '\n');
    }
    static File nativesDestination = null;
    static int motX = 0; //0 = nothing, 1 = left, 2 = right
    static int motY = 0;
    public static void main(String[] args){
        cmd.setVisible(true);
        //Load variables:
        nativesDestination = new File(System.getenv("JAVA_HOME"), "nativesPlotter/");
        if(!nativesDestination.exists()){
            cmd.log("Extracting natives files. Please wait. Program will start automatically.");
            nativesDestination.mkdir();
            //Extract there
            extractFromMe("jSSC-2.8.dll", nativesDestination);
            extractFromMe("libjSSC-2.8.so", nativesDestination);
        }
        cmd.clearLog();
        if(System.getProperty("os.name").startsWith("Win"))
            System.load(nativesDestination.getAbsolutePath() + File.separatorChar + "jSSC-2.8.dll");
        else
            System.load(nativesDestination.getAbsolutePath() + File.separatorChar +  "libjSSC-2.8.so");
        Canvas canvas = new Canvas(){

            @Override
            public void paint(Graphics g) {
                Font f = new Font("Dialog", Font.PLAIN, 12);
                g.setFont(f);
                g.setColor(Color.WHITE);
                int y = 24, dy = 12;
                g.drawString("Available INSTRUCTIONS:", 12, y); y += dy;
                if(port == null)
                g.setColor(Color.RED);
                g.drawString("p: Choose serial port", 12, y); y += dy;
                g.setColor(Color.WHITE);
                //g.drawString("1: set speed to 0.001 inches (1 mil) per jog", 12, y); y += dy;
                //g.drawString("2: set speed to 0.010 inches (10 mil) per jog", 12, y); y += dy;
                //g.drawString("3: set speed to 0.100 inches (100 mil) per jog", 12, y); y += dy;
                g.drawString("Arrows: Move on x-y axes", 12, y); y += dy;
                g.drawString("page up & page down: Pen up, pen down", 12, y); y += dy;
                g.drawString("$: Show grbl settings", 12, y); y+= dy;
                g.drawString("h: Motors to the home position", 12, y); y += dy;
                g.drawString("c: Calibrate motors", 12, y); y += dy;
                g.drawString("r: Stop motors", 12, y); y+= dy;
                //g.drawString("0: zero machine (set home to the current location)", 12, y); y += dy;
                g.drawString("g: Run g-code instruction stream", 12, y); y += dy;
                g.drawString("x: Stop g–code stream", 12, y); y += dy;
                y = 250 - dy;// Height = 250
                g.drawString("© Patrik Staron", this.getWidth()-120, y);
                //g.drawString("current jog speed: " + speed + " inches per step", 12, y); y -= dy;
                g.drawString("Chosen serial port: " + portname, 12, y); y -= dy;
            }
        };
        canvas.setSize(500, 250);
        frame.getContentPane().add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

                nativesDestination.delete();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        frame.getContentPane().setBackground(Color.BLACK);
        canvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                if (key == '1') speed = 0.001f;
                if (key == '2') speed = 0.01f;
                if (key == '3') speed = 0.1f;

                if (!streaming) {
                    if (key == 'h'){port.write("U\n"); port.write("H\n");}
                    if (key == 'c') port.write("C\n");
                    //if (key == 'v') port.write("$0=100\n$1=74\n$2=75\n");
                    if (key == 's') port.write("$3=10\n");
                    if (key == 'e') port.write("$16=1\n");
                    if (key == 'd') port.write("$16=0\n");
                    if (key == 'w'){
                        Integer intt = speedDialog();
                        if(intt != null)
                            port.write("P" + intt + '\n');}
                    if (key == '0') openSerialPort();
                    if (key == 'p') selectSerialPort();
                    if (key == '$') port.write("$$\n");
                    if (key == 'r') port.write("S\n");
                }

                if (!streaming && key == 'g') {
                    gcode = null; i = 0;
                    File file = null;
                    consoleWrite("Loading file...");
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open file with Gcode instructions");
                    fileChooser.setInitialDirectory(
                            new File(System.getProperty("user.home"))
                    );
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("Gcode", "*.gcode"),
                            new FileChooser.ExtensionFilter("All", "*.*")
                    );
                    java.awt.FileDialog fd = new java.awt.FileDialog((java.awt.Frame) null);
                    fd.setVisible(true);
                    fileSelected(new File(fd.getDirectory() + fd.getFile()));
                    //file = fileChooser.showOpenDialog(new Stage());
                }
                frame.getContentPane().getComponent(0).repaint();
                if (key == 'x') streaming = false;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!streaming) {

                    if (e.getKeyCode() == KeyEvent.VK_LEFT) motX = 1;
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) motX = 2;
                    if (e.getKeyCode() == KeyEvent.VK_UP) motY = 1;
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) motY = 2;
                    if(motX == 1){
                        port.write("\""+ '\n');
                    }else if(motX == 2){
                        port.write("_"+ '\n');
                    }
                    if(motY == 1){
                        port.write("#\n");
                    }else if(motY == 2){
                        port.write("[\n");
                    }
                    //Old one
                    /*if (e.getKeyCode() == KeyEvent.VK_LEFT) port.write("B\n");
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) port.write("A\n");
                    if (e.getKeyCode() == KeyEvent.VK_UP) port.write("E\n");
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) port.write("F\n");*/
                    if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) port.write("U" + speed + "\n");
                    if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) port.write("D" + speed + "\n");
                }
                /*else{
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) stream();
                }*/
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!streaming) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT
                    || e.getKeyCode() == KeyEvent.VK_RIGHT) motX = 0;
                    if (e.getKeyCode() == KeyEvent.VK_UP
                    || e.getKeyCode() == KeyEvent.VK_DOWN) motY = 0;
                    if(motX == 0 && motY == 0){
                        if(port != null)
                            port.write("S\n");
                    }
                    //Old one
                    /*if (e.getKeyCode() == KeyEvent.VK_LEFT
                    || e.getKeyCode() == KeyEvent.VK_RIGHT
                    || e.getKeyCode() == KeyEvent.VK_UP
                    || e.getKeyCode() == KeyEvent.VK_DOWN){
                        consoleWrite("fock");
                        port.write("S\n");}*/
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
        //frame.repaint();
    }
    static Serial port = null;

    // select and modify the appropriate line for your operating system
// leave as null to use interactive port (press 'p' in the program)
    static String portname = null;
//String portname = Serial.list()[0]; // Mac OS X
//String portname = "/dev/ttyUSB0"; // Linux
//String portname = "COM6"; // Windows

    static boolean streaming = false;
    static float speed = 0.001f;
    static String[] gcode;
    static int i = 0;

    static void openSerialPort()
    {
        if (portname == null) return;
        if (port != null) port.stop();

        port = new Serial(portname, 9600);

        port.bufferUntil('\n');
    }

    static Integer speedDialog(){
        JFrame frame = new JFrame("Select speed");
        frame.getContentPane().setSize(500, 250);
        String result = (String) JOptionPane.showInputDialog(
                "Set speed.",
                "Choose serial port");
        return result.isEmpty()?null : Integer.parseInt(result);

    }

    static void selectSerialPort()
    {
  /*String result = (String) JOptionPane.showInputDialog(this,
    "Select the serial port that corresponds to your Arduino board.",
    "Select serial port",
    JOptionPane.PLAIN_MESSAGE,
    null,
    Serial.list(),
    0);*/
        JFrame frame = new JFrame("InputDialog Example #1");
        frame.getContentPane().setSize(500, 250);
        String result = (String) JOptionPane.showInputDialog(frame,
                "Choose the serial port corresponding to your board.",
                "Choice of serial port",
                JOptionPane.PLAIN_MESSAGE,
                null,
                Serial.list(),
                0);

        if (result != null) {
            portname = result;
            openSerialPort();
            try{
                Thread.sleep(3000);}
            catch(InterruptedException e){
            }
            port.write("H\n");
        }
    }

    void setup()
    {
        openSerialPort();
    }

    static void fileSelected(File selection) {
        if (selection == null) {
            consoleWrite("Window was closed or the user hit cancel.");
        } else {
            consoleWrite("User selected " + selection.getAbsolutePath());
            gcode = readFile(new File(selection.getAbsolutePath()), 250, 250).toArray(new String[0]);
            if (gcode == null) return;
            streaming = true;
            stream();
        }
    }

    static void stream()
    {
        if (!streaming) return;
        while (true) {
            if (i == gcode.length) {
                streaming = false;
                port.write("H\n");
                return;
            }
            if (gcode[i].trim().length() == 0) i++;
            else break;
        }
        consoleWrite(gcode[i]);
        port.write(gcode[i] + '\n');
        i++;
    }

    static void serialEvent(Serial p)
    {
        String s = p.readStringUntil('\n');
        consoleWrite(s.trim());
        if (s.trim().startsWith("ok")) stream();
        if (s.trim().startsWith("error")) stream(); // XXX: really?
    }


    private static void extractFromMe(String file, File destination){
        File jarLoc = null;
        try {
            jarLoc = new File(main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        java.util.jar.JarFile jar = null;
        try {
            jar = new java.util.jar.JarFile(jarLoc);
            java.io.File dest = new java.io.File(destination + java.io.File.separator + file);

            java.io.InputStream is = jar.getInputStream(new ZipEntry(file)); // get the input stream
            java.io.FileOutputStream fos = new java.io.FileOutputStream(dest);
            while (is.available() > 0) {  // write contents of 'is' to 'fos'
                fos.write(is.read());
            }
            fos.close();
            is.close();
            /*java.util.Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {

            java.util.jar.JarEntry innerFile = (java.util.jar.JarEntry) enumEntries.nextElement();
            java.io.File f = new java.io.File(destination + java.io.File.separator + innerFile.getName());
            if (innerFile.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            }
            java.io.InputStream is = jar.getInputStream(innerFile); // get the input stream
            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
            while (is.available() > 0) {  // write contents of 'is' to 'fos'
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }*/
        jar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the file and returns a list of lines.
     * Automatically detects line breaks. Supports: "\n","\r","\r\n"
     * @param buffersize Size of chunks the file is read at. Best if multiple of innerbuffersize.
     * @param innerbufsize Size of the stored string chunks, Best if as big as the biggest string.
     * @return List of lines.
     * */
    private static java.util.List<String> readFile(File file, int buffersize, int innerbufsize){
        List<String> lines = new ArrayList<String>();
        try {
            InputStreamReader fr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            String s = "";
            boolean rBreakPresent = false; //This tells the next internal for iteration to remove the r break in n break detection
            while(fr.ready()){
                char[] buffer = new char[buffersize]; //External buffer
                int q = fr.read(buffer); //Index of read characters
                char[] innerBuffer = new char[innerbufsize];
                int smallest = q > innerbufsize? innerbufsize : q;
                int fromInner = 0; //If one iteration covers more strings, this stores the splitter index.
                for(int i = 0; i < smallest; i++){
                    char ch = buffer[i];
                    if(rBreakPresent || ch == '\n'){//Always create new line.
                        //Two things can happen. character terminator is in the middle of two buffers,
                        //end of line is smaller than the inner buffer.
                        int terminatorLength = (rBreakPresent? (ch == '\n'? 1:0) : 0);//NOT TESTED WITH BARE l AND n TERMINATORS
                        int numOfCharsToTake = i-fromInner-terminatorLength;
                        if(numOfCharsToTake > 0){ //Protect from Either in terminator split in two arrays or other unexpected outcome.
                            s = s + new String(innerBuffer, fromInner, numOfCharsToTake); //Stores by break, without break.
                            lines.add(s); //Line ended by terminator, after probable completion above, it is stored.
                            s = ""; //Reset, in need of continuing with the next string in this loop
                        }else if(!s.isEmpty()){
                            lines.add(s); s = ""; //If the line was in previous buffer and terminator is in the new one.
                        }
                        fromInner=(i + terminatorLength); //In the case that this is the last thing in the buffer, it is excluded after loop finish.
                        rBreakPresent = false; //Everything is reset right.
                    }else
                    if(ch == '\r'){
                        //Now, one piss can happen, direct break in the thing. So we need to remember the state and evaluate
                        //it later. HOWEVER No later may come, since there may not be any comming one. Piss|
                        //Check if r terminator present, or close line.
                        rBreakPresent = true;
                    }else{
                        innerBuffer[i] = buffer[i];
                    }
                }
                if(fromInner != smallest) //If last iteration already finished string, don't allow the executionion.
                    //String was not terminated, so put from the left start of the string to the buffer.
                    s = s + new String(innerBuffer, fromInner, innerbufsize-fromInner - (rBreakPresent? 1 : 0));
            }
            //If line is not terminated by terminator, but by the end of stream, line is added to the list.
            if(!s.isEmpty()) lines.add(s);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
