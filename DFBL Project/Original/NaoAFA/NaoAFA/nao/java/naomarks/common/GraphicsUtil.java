//////////////////////////////////////////////////////////////////////////////
//                                                                          //
// This software is a work of the U.S. Government. It is not subject to     //
// copyright protection and is in the public domain. It may be used as-is   //
// or modified and re-used. The author and the Air Force Research           //
// Laboratory would appreciate credit if this software or parts of it are   //
// used or modified for re-use.                                             //
//                                                                          //
//////////////////////////////////////////////////////////////////////////////

package common;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import javax.swing.text.*;

/****************************************************************************
 * Provides a number of helper functions and classes that are useful when
 * building graphical user interfaces.
 *
 * @author Craig Cox (Craig.Cox.1@us.af.mil, c.afrl.rqva@reacomp.com)
 *
 * $Rev:: 273                   $: Revision of last commit
 * $Author:: mavlab             $: Author of last commit
 * $Date:: 2011-03-28 17:12:48 #$: Date of last commit
 ****************************************************************************/
public class GraphicsUtil
{
  public static final String[] CONTACTS
      = new String[] { "Craig.Cox.1@us.af.mil",
                       "c.afrl.rhxs@reacomp.com" };

  public static void center(Component child)
  {
    center(child, null);
  }

  public static void center(Component child, Component parent)
  {
    Dimension dimChildSize;
    Dimension dimParentSize;
    Point     ptParentLocation;

    dimChildSize = child.getSize();

    if (parent != null)
    {
      ptParentLocation = parent.getLocationOnScreen();
      dimParentSize = parent.getSize();
    }
    else
    {
      ptParentLocation = new Point(0, 0);

      try
      {
        dimParentSize = Toolkit.getDefaultToolkit().getScreenSize();
      }
      catch (Exception e)
      {
        dimParentSize = new Dimension(800, 600);
      }
    }

    child.setLocation(new Point(ptParentLocation.x
        + (dimParentSize.width - dimChildSize.width) / 2,
        ptParentLocation.y
        + (dimParentSize.height - dimChildSize.height) / 2));
  }

  public static void moveRelative(Component comp,
                                  int       nDeltaX,
                                  int       nDeltaY)
  {
    Point pt;

    pt = comp.getLocation();

    pt.x += nDeltaX;
    pt.y += nDeltaY;

    comp.setLocation(pt);
  }

  public static void makeSkinny(JButton btn)
  {
    btn.setBorder(new javax.swing.plaf.metal.MetalBorders.ButtonBorder()
    {
      private Insets m_insets = new Insets(2, 5, 2, 5);

      public Insets getBorderInsets(Component c)
      {
        return (Insets) m_insets.clone();
      }

      public Insets getBorderInsets(Component c, Insets newInsets)
      {
        newInsets.top = m_insets.top;
        newInsets.left = m_insets.left;
        newInsets.bottom = m_insets.bottom;
        newInsets.right = m_insets.right;

        return newInsets;
      }
    });
  }

  public static JMenuItem createAboutInfoMenuItem(
      final String     DIALOG_TITLE,
      final Component  PARENT,
            String     sVersionInfo,
            String[][] sInfoPairs,
            String     sContactInfo)
  {
    String[] sContacts;

    sContacts = (null != sContactInfo)
                ? new String[] { sContactInfo }
                : null;

    return createAboutInfoMenuItem(DIALOG_TITLE,
                                   PARENT,
                                   sVersionInfo,
                                   sInfoPairs,
                                   sContacts);
  }

  public static JMenuItem createAboutInfoMenuItem(
      final String     DIALOG_TITLE,
      final Component  PARENT,
            String     sVersionInfo,
            String[][] sInfoPairs,
            String[]   sContacts)
  {
    boolean            bContactLabelAdded = false;
    GridBagConstraints gbc = new GridBagConstraints();
    JMenuItem          menuItem;
    String             sKey;
    String             sValue;

    final JPanel pnl;

    pnl = new JPanel(new GridBagLayout());

    gbc.gridy = 0;
    gbc.gridx = 0;

    if (null != sVersionInfo)
    {
      gbc.anchor = GridBagConstraints.EAST;
      pnl.add(new JLabel("Software version: ", JLabel.RIGHT), gbc);
      gbc.gridx++;

      gbc.anchor = GridBagConstraints.WEST;
      pnl.add(new JLabel(sVersionInfo.trim()), gbc);
      gbc.gridy++;
      gbc.gridx = 0;
    }

    if (0 != gbc.gridy)
    {
      pnl.add(Box.createVerticalStrut(5), gbc);
      gbc.gridy++;
      gbc.gridx = 0;
    }

    gbc.anchor = GridBagConstraints.EAST;
    pnl.add(new JLabel("JRE version: ", JLabel.RIGHT), gbc);
    gbc.gridx++;

    gbc.anchor = GridBagConstraints.WEST;
    pnl.add(new JLabel(System.getProperty("java.version")), gbc);
    gbc.gridy++;
    gbc.gridx = 0;

    if (null != sInfoPairs)
    {
      for (int i = 0; i < sInfoPairs.length; i++)
      {
        if (2 == sInfoPairs[i].length)
        {
          sKey = (null != sInfoPairs[i][0])
                 ? sInfoPairs[i][0].trim()
                 : "";

          if (sKey.length() > 0 && !sKey.endsWith(":"))
          {
            sKey = sKey + ":";
          }

          sKey += " ";

          sValue = (null != sInfoPairs[i][1])
                   ? sInfoPairs[i][1].trim()
                   : "";

          if (0 != gbc.gridy)
          {
            pnl.add(Box.createVerticalStrut(5), gbc);
            gbc.gridy++;
            gbc.gridx = 0;
          }

          gbc.anchor = GridBagConstraints.EAST;
          pnl.add(new JLabel(sKey, JLabel.RIGHT), gbc);
          gbc.gridx++;

          gbc.anchor = GridBagConstraints.WEST;
          pnl.add(new JLabel(sValue), gbc);
          gbc.gridy++;
          gbc.gridx = 0;
        }
      }
    }

    if (null != sContacts
        && sContacts.length > 0)
    {
      for (String sContact : sContacts)
      {
        if (null != sContact)
        {
          if (0 != gbc.gridy)
          {
            pnl.add(Box.createVerticalStrut(5), gbc);
            gbc.gridy++;
            gbc.gridx = 0;
          }

          if (!bContactLabelAdded)
          {
            gbc.anchor = GridBagConstraints.EAST;
            pnl.add(new JLabel("Contact: ", JLabel.RIGHT), gbc);
            bContactLabelAdded = true;
          }

          gbc.gridx = 1;
          gbc.anchor = GridBagConstraints.WEST;
          pnl.add(new JLabel(sContact.trim()), gbc);
          gbc.gridy++;
        }
      }
    }

    if (0 != gbc.gridy)
    {
      menuItem = new JMenuItem(new AbstractAction("About")
      {
        public void actionPerformed(ActionEvent event)
        {
          JOptionPane.showMessageDialog(PARENT,
                                        pnl,
                                        DIALOG_TITLE,
                                        JOptionPane.INFORMATION_MESSAGE);
        }
      });
    }
    else
    {
      menuItem = null;
    }

    return menuItem;
  }

  public static BufferedImage makeTranslucent(BufferedImage imageIn,
                                              double        dAlpha)
  {
    BufferedImage imageOut;
    Graphics2D    g2;

    imageOut = new BufferedImage(imageIn.getWidth(),
                                 imageIn.getHeight(),
                                 BufferedImage.TYPE_INT_ARGB_PRE);

    g2 = imageOut.createGraphics();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                               (float) dAlpha));
    g2.drawImage(imageIn, null, 0, 0);
    g2.dispose();

    return imageOut;
  }

  public static Color blend(Color  color1,
                            double dColor1Weight,
                            Color  color2) {
    int nBlue;
    int nGreen;
    int nRed;

    nRed = (int) Math.round(color1.getRed() * dColor1Weight
                            + color2.getRed() * (1.0 - dColor1Weight));
    nGreen = (int) Math.round(color1.getGreen() * dColor1Weight
                            + color2.getGreen() * (1.0 - dColor1Weight));
    nBlue = (int) Math.round(color1.getBlue() * dColor1Weight
                            + color2.getBlue() * (1.0 - dColor1Weight));

    return new Color(Math.min(255,
                              Math.max(0,
                                       nRed)),
                     Math.min(255,
                              Math.max(0,
                                       nGreen)),
                     Math.min(255,
                              Math.max(0,
                                       nBlue)));
  }

//  public static JTextArea addStatusPanel(Container cont)
//  {
//    JScrollPane jsp;
//    JTextArea   ta;
//
//    ta = new JTextArea(10, 40);
//    jsp = new JScrollPane(ta,
//                          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//                          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//    jsp.setBorder(BorderFactory.createCompoundBorder(
//        BorderFactory.createEmptyBorder(5, 5, 5, 5),
//        BorderFactory.createLineBorder(Color.black)));
//    cont.add(jsp, BorderLayout.SOUTH);
//
//    return ta;
//  }

  public static class FileSelector extends JPanel
  {
    private boolean      m_bDisplayFullPath = true;
    private Component    m_parent;
    private Cursor       m_crsrTextFieldDefault;
    private JButton      m_btn;
    private JFileChooser m_chooser;
    private JTextField   m_tf;

    public FileSelector(Component parent)
    {
      this(parent, null);
    }

    public FileSelector(Component parent, String sLabel)
    {
      m_parent = parent;

      buildGui(sLabel);
    }

    public void setDisplayFullPath(boolean bValue)
    {
      m_bDisplayFullPath = bValue;
    }

    public String getFilename()
    {
      return (m_bDisplayFullPath || m_tf.getText().startsWith("/"))
             ? m_tf.getText()
             : m_chooser.getCurrentDirectory() + "/" + m_tf.getText();
    }

    public boolean doesFileExist()
    {
      if (0 == getFilename().length())
      {
        return false;
      }

      return new File(getFilename()).exists();
    }

    public void setCursor(Cursor cursor)
    {
      m_tf.setCursor(cursor);
    }

    public void resetCursor()
    {
      m_tf.setCursor(m_crsrTextFieldDefault);
    }

    public void setEnabled(boolean bEnabled)
    {
      m_tf.setEditable(bEnabled);
      m_btn.setEnabled(bEnabled);
    }

    public void setCurrentDirectory(File dir)
    {
      getFileChooser().setCurrentDirectory(dir);

      try {
        m_tf.setText(dir.getCanonicalPath());
      } catch (IOException e) {
        m_tf.setText(dir.getName());
      }
    }

    public JFileChooser getFileChooser()
    {
      return m_chooser;
    }

    public JButton getButton()
    {
      return m_btn;
    }

    public JTextField getTextField()
    {
      return m_tf;
    }

    public void addDocumentListener(DocumentListener listener)
    {
      m_tf.getDocument().addDocumentListener(listener);
    }

    private void buildGui(String sLabel)
    {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

      setOpaque(false);

      if (sLabel != null)
      {
        add(new JLabel(sLabel, JLabel.RIGHT));
        add(Box.createHorizontalStrut(5));
      }

      m_tf = new JTextField("", 15);
      m_crsrTextFieldDefault = m_tf.getCursor();
      add(m_tf);

      add(Box.createHorizontalStrut(5));

      m_btn = new JButton("...");
      makeSkinny(m_btn);
      m_btn.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
        {
          select();
        }
      });
      add(m_btn);

      m_chooser = new JFileChooser();
    }

    private void select()
    {
      int nReturn;

      nReturn = m_chooser.showOpenDialog(m_parent);

      if (JFileChooser.APPROVE_OPTION == nReturn)
      {
        if (m_bDisplayFullPath)
        {
          m_tf.setText(m_chooser.getSelectedFile().getAbsolutePath());
        }
        else
        {
          m_tf.setText(m_chooser.getSelectedFile().getName());
        }
      }
    }
  }

  public static class SliderCombo extends JPanel
  {
    public final double MAX_VALUE;
    public final double MIN_VALUE;

    private final double  DISPLAY_FACTOR;
    private final double  FACTOR;
    private final JSlider SLIDER;

    protected final JTextField READOUT;

    public SliderCombo()
    {
      this(-1.0, 1.0, 0.0);
    }

    public SliderCombo(double dMin,
                       double dMax,
                       double dInitial)
    {
      this(dMin, dMax, dInitial, 1000);
    }

    public SliderCombo(double dMin,
                       double dMax,
                       double dInitial,
                       int    nSliderCount)
    {
      GridBagConstraints         gbc = new GridBagConstraints();
      Hashtable<Integer, JLabel> ht = new Hashtable<Integer, JLabel>();

      MIN_VALUE = dMin;
      MAX_VALUE = dMax;
      FACTOR = nSliderCount / (dMax - dMin);

      DISPLAY_FACTOR = Math.pow(10, 4 - (int) Math.log10(dMax - dMin));

      setLayout(new GridBagLayout());

      gbc.gridy = 0;
      gbc.gridx = -1;
      gbc.fill  = GridBagConstraints.HORIZONTAL;

      SLIDER = new JSlider(0,
                           nSliderCount,
                           (int) Math.round((dInitial - dMin) * FACTOR));

      ht.put(new Integer(0),
             new JLabel("" + dMin));
      ht.put(new Integer(nSliderCount / 2),
             new JLabel("" + ((dMax + dMin) / 2.0)));
      ht.put(new Integer(nSliderCount),
             new JLabel("" + dMax));
      SLIDER.setLabelTable(ht);

      SLIDER.setMajorTickSpacing(nSliderCount / 2);
      SLIDER.setPaintLabels(true);
      SLIDER.setPaintTicks(true);
      SLIDER.setPaintTrack(true);
      SLIDER.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent event)
        {
          setReadoutText();
        }
      });
      gbc.gridx++;
      gbc.weightx = 1.0;
      add(SLIDER, gbc);
      gbc.weightx = 0.0;

      gbc.gridx++;
      add(Box.createHorizontalStrut(10), gbc);

      READOUT = new JTextField("", 6);
      setReadoutText();
      READOUT.setHorizontalAlignment(JTextField.CENTER);
      READOUT.setEditable(false);
      gbc.gridx++;
      add(READOUT, gbc);
    }

    public JSlider getSlider()
    {
      return SLIDER;
    }

    public void setEnabled(boolean bEnabled)
    {
      super.setEnabled(bEnabled);

      SLIDER.setEnabled(bEnabled);
//      READOUT.setEnabled(bEnabled);
    }

    public JTextField getReadout()
    {
      return READOUT;
    }

    public double getValue()
    {
      return SLIDER.getValue() / FACTOR + MIN_VALUE;
    }

    public void setValue(double dValue)
    {
       SLIDER.setValue((int) Math.round((dValue - MIN_VALUE) * FACTOR));
    }

    protected void setReadoutText()
    {
      READOUT.setText("" + (Math.round(getValue() * DISPLAY_FACTOR)
                           / DISPLAY_FACTOR));
    }
  }

  public static class Log10SliderCombo extends SliderCombo
  {
    private double m_dDisplayFactor;

    public Log10SliderCombo(double dMin,
                            double dMax,
                            double dInitial)
    {
      super(dMin, dMax, dInitial);

      m_dDisplayFactor = Math.pow(10, 4 - (int) dMax);

      setReadoutText();
    }

    protected void setReadoutText()
    {
      double dValue;

      dValue = Math.pow(10.0, getValue());

      READOUT.setText("" + (Math.round(dValue * m_dDisplayFactor)
                            / m_dDisplayFactor));
    }
  }

  public static class RotatedLabel extends JLabel
  {
    public static final int SOUTH     = 0;
    public static final int SOUTHWEST = 1;
    public static final int SOUTHEAST = 2;
    public static final int NORTH     = 3;
    public static final int NORTHWEST = 4;
    public static final int NORTHEAST = 5;
    public static final int WEST      = 6;
    public static final int CENTER    = 7;
    public static final int EAST      = 8;

    private final Dimension MAX_SIZE;
    private final Dimension MIN_SIZE;
    private final Dimension PREF_SIZE;

    private boolean m_bRotationIsReversed = false;
    private int     m_nAnchor;
    private String  m_sText;

    public RotatedLabel(String sText)
    {
      this(sText, SOUTH);
    }

    public RotatedLabel(String sText,
                        int    nAnchor)
    {
      super();

      Dimension dim;
      JLabel    lbl;

      m_sText = sText;
      m_nAnchor = nAnchor;

      lbl = new JLabel(sText);

      dim = lbl.getPreferredSize();
      PREF_SIZE = new Dimension(dim.height, dim.width);

      dim = lbl.getMinimumSize();
      MIN_SIZE = new Dimension(dim.height, dim.width);

      dim = lbl.getMaximumSize();
      MAX_SIZE = new Dimension(dim.height, dim.width);
    }

    public void reverseRotation() {
      m_bRotationIsReversed ^= true;
    }

    public void paint(Graphics g)
    {
      super.paint(g);

      double      dRotationAngle;
      int         nTranslateX;
      int         nTranslateY;
      FontMetrics fm;
      Rectangle   bounds;

      g.setColor(getForeground());

      fm = g.getFontMetrics();
      bounds = g.getClipBounds();

      switch (m_nAnchor)
      {
        case SOUTH:
        case SOUTHWEST:
        case SOUTHEAST:
          nTranslateX = (m_bRotationIsReversed)
                        ? bounds.height - (fm.stringWidth(m_sText))
                        : -bounds.height;

          break;

        case NORTH:
        case NORTHWEST:
        case NORTHEAST:
          nTranslateX = (m_bRotationIsReversed)
                        ? 0
                        : -fm.stringWidth(m_sText);

          break;

        case WEST:
        case CENTER:
        case EAST:
          nTranslateX = (m_bRotationIsReversed)
                        ? (bounds.height - fm.stringWidth(m_sText)) / 2
                        : -(bounds.height + fm.stringWidth(m_sText)) / 2;

          break;

        default:
          throw new Error("Invalid anchor argument: " + m_nAnchor);
      }

      switch (m_nAnchor) {
        case SOUTH:
        case CENTER:
        case NORTH:
          nTranslateY = (m_bRotationIsReversed)
                        ? (fm.getHeight() - bounds.width) / 2
                          - fm.getDescent()
                        : (bounds.width + fm.getHeight()) / 2
                          - fm.getDescent();

          break;

        case SOUTHWEST:
        case NORTHWEST:
        case WEST:
          nTranslateY = (m_bRotationIsReversed)
                        ? -fm.getDescent()
                        : fm.getHeight() - fm.getDescent();

          break;

        case SOUTHEAST:
        case NORTHEAST:
        case EAST:
          nTranslateY = (m_bRotationIsReversed)
                        ? -bounds.width + fm.getHeight() - fm.getDescent()
                        : bounds.width - fm.getDescent();

          break;

        default:
          throw new Error("Invalid anchor argument: " + m_nAnchor);
      }

      dRotationAngle = (m_bRotationIsReversed)
                       ? Math.PI / 2.0
                       : -Math.PI / 2.0;

      ((Graphics2D) g).rotate(dRotationAngle);
      ((Graphics2D) g).translate(nTranslateX, nTranslateY);

      g.drawString(m_sText, 0, 0);
    }

    public Dimension getPreferredSize()
    {
      return PREF_SIZE;
    }

    public Dimension getMinimumSize()
    {
      return MIN_SIZE;
    }

    public Dimension getMaximumSize()
    {
      return MAX_SIZE;
    }
  }

  public static class IntField extends JTextField
    implements DocumentListener
  {
    public static final Color INVALID_COLOR   = new Color(1.0f, 0.5f, 0.5f);
    public static final Color VALID_COLOR     = new Color(1.0f, 1.0f, 1.0f);

    private final int      MIN_VALUE;
    private final int      MAX_VALUE;
    private final Listener LISTENER;

    private Integer m_value;

    public IntField(int      nMinValue,
                    int      nMaxValue,
                    int      nValue,
                    Listener listener)
    {
      super(4);

      MIN_VALUE = nMinValue;
      MAX_VALUE = nMaxValue;
      LISTENER = listener;

      setHorizontalAlignment(CENTER);

      startColorThread();

      // In order for the document listener to get an event when the desired
      // text is set in the text field, a fake value is set now, before the
      // document listener is added. That way, when the desired value is set
      // after the listener is added, the new text is guaranteed to be
      // different and trigger an event.
      setText("A" + nValue);

      getDocument().addDocumentListener(this);

      setText("" + nValue);
      setCaretPosition(getText().length());
    }

    public Integer getValue()
    {
      return m_value;
    }

    // Required by the DocumentListener interface.
    public void changedUpdate(DocumentEvent event)
    {
      synchronized (this)
      {
        this.notifyAll();
      }
    }

    // Required by the DocumentListener interface.
    public void insertUpdate(DocumentEvent event)
    {
      synchronized (this)
      {
        this.notifyAll();
      }
    }

    // Required by the DocumentListener interface.
    public void removeUpdate(DocumentEvent event)
    {
      synchronized (this)
      {
        this.notifyAll();
      }
    }

    private void startColorThread()
    {
      final boolean[] WAITING = new boolean[1];

      Thread thread;

      WAITING[0] = false;

      thread = new Thread(new Runnable()
      {
        public void run()
        {
          String sProcessedText = null;
          String sText;

          while (true)
          {
            synchronized (IntField.this)
            {
              try
              {
                WAITING[0] = true;

                IntField.this.wait();
              }
              catch (InterruptedException e)
              {
              }
            }

            sText = getText();

            while (!sText.equals(sProcessedText))
            {
              processInput(sText);

              sProcessedText = sText;
              sText = getText();
            }
          }
        }
      }, "IntField.Color");

      thread.setDaemon(true);

      thread.start();

      // Don't return from this method until the new thread has a lock and is
      // waiting to be notified by another thread.
      while (!WAITING[0])
      {
        try
        {
          Thread.sleep(100);
        }
        catch (Exception e)
        {
        }
      }
    }

    private void processInput(String sInput)
    {
      Integer originalValue;

      originalValue = m_value;

      try
      {
        setValue(Integer.parseInt(sInput));
      }
      catch (Exception e)
      {
        m_value = null;
      }

      if (m_value != originalValue
          && null != LISTENER)
      {
        LISTENER.valueChanged(m_value, this);
      }

      setBackground((null == m_value)
                    ? INVALID_COLOR
                    : VALID_COLOR);
    }

    public interface Listener
    {
      public void valueChanged(Integer  value,
                               IntField field);
    }

    private void setValue(int nValue)
    {
      if (nValue >= MIN_VALUE
          && nValue <= MAX_VALUE)
      {
        m_value = new Integer(nValue);
      }
      else
      {
        m_value = null;
      }
    }
  }

  public static abstract class DocumentAdapter implements DocumentListener
  {
    public void insertUpdate(DocumentEvent event)
    {
      handleUserInput(event.getDocument());
    }

    public void removeUpdate(DocumentEvent event)
    {
      handleUserInput(event.getDocument());
    }

    public void changedUpdate(DocumentEvent event)
    {
      handleUserInput(event.getDocument());
    }

    public abstract void handleUserInput(Document doc);
  }

  ////////////////////////////////////////////////////////////////////////////
  //                                                                        //
  // THE FOLLOWING METHODS ARE PROVIDED FOR TESTING PURPOSES ONLY.          //
  //                                                                        //
  ////////////////////////////////////////////////////////////////////////////

  public static void main(String[] args) throws Exception
  {
    final FileSelector selector;

    Container    cp;
    JButton      btn;
    JFileChooser chooser;
    JFrame       frame;
    JMenu        menu;
    JMenuBar     menuBar;
    JMenuItem    menuItem;
    String[][]   sInfoPairs;

    frame = new JFrame("Test");

    frame.setJMenuBar(menuBar = new JMenuBar());

    menuBar.add(Box.createHorizontalGlue());

    menuBar.add(menu = new JMenu("Help"));

    sInfoPairs = new String[][] { { "First value", "1" },
                                  { "Second value: ", "2" },
                                  { ": ", "2" },
                                  { "Fourth value:", "" } };

    menu.add(createAboutInfoMenuItem("About " + frame.getTitle(),
                                     frame,
                                     "1.0",
                                     sInfoPairs,
                                     CONTACTS));

    cp = frame.getContentPane();
    cp.setLayout(new GridLayout(2, 1));

    selector = new FileSelector(frame);
    chooser = selector.getFileChooser();
    selector.getTextField().setColumns(25);
    chooser.setFileFilter(new FileNameExtensionFilter("ZIP files", "zip"));
    chooser.setCurrentDirectory(new File("/home/coxc/etc/afrl"));
    frame.add(selector);

    btn = new JButton("Check");
    btn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        System.out.println("File exists: " + selector.doesFileExist());
        System.out.println("Filename: " + selector.getFilename());
      }
    });
    frame.add(btn);

    frame.setSize(new Dimension(640, 480));
    center(frame);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.setVisible(true);
  }
}
