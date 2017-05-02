//////////////////////////////////////////////////////////////////////////////
//                                                                          //
// This software is a work of the U.S. Government. It is not subject to     //
// copyright protection and is in the public domain. It may be used as-is   //
// or modified and re-used. The author and the Air Force Research           //
// Laboratory would appreciate credit if this software or parts of it are   //
// used or modified for re-use.                                             //
//                                                                          //
//////////////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import common.*;
// CHANGES: Added imports to support timestamp
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/****************************************************************************
 * This panel allows the user to input text to be sent to a listener (which
 * typically will make Rufus speak the text).
 *
 * @author Craig Cox (Craig.Cox.1@us.af.mil, c.afrl.rhxs@reacomp.com)
 ****************************************************************************/
public class TextPanel extends JPanel implements ActionListener {
  private final ArrayList<Listener> LISTENERS = new ArrayList<>();
  private JTextField m_tfText;
  
  // CHANGES: Added reference to JFrame in constructor, and date formatter
  private static final SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

  private NaoMarks parent;
  public TextPanel(NaoMarks f) {
	parent = f;
    buildGui();
  }

  /**
   * Required by the ActionListener interface.
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    for (Listener l : LISTENERS) {
      l.hearText(m_tfText.getText());
    }
    
    // CHANGES: Set text to blank after input, and log text input to the text area.
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    String text = df.format(ts) + ": " + m_tfText.getText() + "\n";
    parent.getStatusArea().append(text);
    parent.getStatusArea().setCaretPosition(parent.getStatusArea().getDocument().getLength());

    m_tfText.setText("");
  }

  public void addListener(Listener listener) {
    LISTENERS.add(listener);
  }

  public String getText() {
    return m_tfText.getText();
  }

  private void buildGui() {
    JButton btn;
    JPanel  p1;

    setLayout(new BorderLayout());

    p1 = new JPanel(new BorderLayout(5,
                                     0));
    add(p1,
        BorderLayout.CENTER);

    p1.add(new JLabel("Text:",
                      JLabel.RIGHT),
           BorderLayout.WEST);

    m_tfText = new JTextField(getInitialText(),
                              40);
    m_tfText.addActionListener(this);
    p1.add(m_tfText,
           BorderLayout.CENTER);

    btn = new JButton("Send");
    GraphicsUtil.makeSkinny(btn);
    btn.addActionListener(this);
    p1.add(btn,
           BorderLayout.EAST);
  }

  private String getInitialText() {
    return "Connected.";
  }

  public interface Listener {
    public void hearText(String sText);
  }
}
