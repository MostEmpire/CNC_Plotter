import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b){
        // redirects data to the text area
        if(textArea.getText().length() > 2048)
        textArea.replaceRange(null, 0, 100);
        textArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}

public class CMD extends JFrame {
    /**
     * The text area which is used for displaying logging information.
     */
    private JTextArea textArea;

    private PrintStream standardOut;

    public CMD() {
        super("Plotter output - LOG");

        textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        standardOut = new PrintStream(new CustomOutputStream(textArea));
        // keeps reference of standard output stream

        // re-assigns standard output stream and error output stream
        System.setErr(standardOut);
        // creates the GUI
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(new JScrollPane(textArea), constraints);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);    // centers on screen
    }

    /**
     * Prints log statements for testing in a thread
     */
    public void log(String data) {

        try {
            standardOut.write(data.getBytes());
            standardOut.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearLog(){
        try {
            textArea.getDocument().remove(0,
                    textArea.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}