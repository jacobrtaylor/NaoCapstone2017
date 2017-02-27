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

/****************************************************************************
 * This panel allows the user to input text to be sent to a listener (which
 * typically will make Rufus speak the text).
 *
 * @author Craig Cox (Craig.Cox.1@us.af.mil, c.afrl.rhxs@reacomp.com)
 ****************************************************************************/
public class TextPanel extends JPanel {
  private final ArrayList<Listener> LISTENERS = new ArrayList<>();
  private JTextField m_tfText;

  public TextPanel() {
    buildGui();
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

    p1 = new JPanel(new BorderLayout());
    add(p1,
        BorderLayout.CENTER);

    p1.add(new JLabel("Text: ",
                      JLabel.RIGHT),
           BorderLayout.WEST);

    m_tfText = new JTextField("Hello, my name is Rufus",
                              40);
    p1.add(m_tfText,
           BorderLayout.CENTER);

    p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
    add(p1,
        BorderLayout.SOUTH);

    btn = new JButton("Send");
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        for (Listener l : LISTENERS) {
          l.hearText(m_tfText.getText());
        }
      }
    });
    p1.add(btn);
  }

  public interface Listener {
    public void hearText(String sText);
  }
}
