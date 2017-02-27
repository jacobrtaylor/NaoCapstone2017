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
import java.io.*;
import java.util.*;

import javax.swing.*;

import com.aldebaran.qi.*;
import com.aldebaran.qi.helper.*;
import com.aldebaran.qi.helper.proxies.*;

import common.*;

/****************************************************************************
 * This class sends text to Rufus for him to speak.
 *
 * @author Craig Cox (Craig.Cox.1@us.af.mil, c.afrl.rhxs@reacomp.com)
 ****************************************************************************/
public class RufusTextToSpeech extends JFrame
    implements ExceptionHandler,
               TextPanel.Listener {
  public static final String VERSION = "0.1";

  private static final int    DEFAULT_ROBOT_PORT = 9559;
  private static final String DEFAULT_ROBOT_ADDRESS = "rufus";

  private final ArrayList<String> QUEUE = new ArrayList<>();

  private ALAutonomousMoves m_srvcAutonomousMoves;
  private ALRobotPosture    m_srvcPosture;
  private ALTextToSpeech    m_srvcTextToSpeech;
  private Application       m_application;
  private JTextArea         m_taStatus;
  private Posture           m_posture;
  private TextPanel         m_pnlText;

  public RufusTextToSpeech(String... args) {
    super("Rufus Text to Speech");

    buildGui();

    initialize(args);
  }

  /**
   * Required by the TextPanel.Listener interface.
   */
  @Override
  public void hearText(String sText) {
    send(sText);
  }

  /**
   * Initialize communications with Rufus.
   */
  private void initialize(String... args) {
    Session session;
    String  sUrl;

    try {
      // Create a new Application.
      sUrl = "tcp://" + DEFAULT_ROBOT_ADDRESS
             + ":" + DEFAULT_ROBOT_PORT;
      m_application = new Application(args,
                                      sUrl);

      // Start the application, which creates a session and connects it to the
      // robot.
      m_application.start();

      // Get the created session.
      session = m_application.session();

      // Create required services.
      m_srvcAutonomousMoves = new ALAutonomousMoves(session);
      m_srvcPosture         = new ALRobotPosture(session);
      m_srvcTextToSpeech    = new ALTextToSpeech(session);

      // Set the autonomous move background strategy to none to prevent Rufus
      // from moving back to some default posture after a new posture is
      // commanded.
      m_srvcAutonomousMoves.setBackgroundStrategy("none");

      // Make Rufus stand normally and then speak the current text.
      m_posture = Posture.RELAXING;
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            m_srvcPosture.goToPosture("Stand", 0.25f);

            send(m_pnlText.getText());
          } catch (Exception e) {
            handleException(e);
          }
        }
      }).start();

      // Start a thread to send text to Rufus.
      startSpeechThread();
    } catch (Exception e) {
      handleException(e);

      m_application      = null;
      m_srvcPosture      = null;
      m_srvcTextToSpeech = null;
    }
  }

  public void send(String sText) {
    synchronized (QUEUE) {
      QUEUE.add(sText);

      QUEUE.notifyAll();
    }
  }

  // Required by the ExceptionHandler interface.
  public void handleException(Exception e)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    e.printStackTrace(new PrintStream(baos));

    m_taStatus.append(baos.toString());
    m_taStatus.setCaretPosition(m_taStatus.getText().length());
  }

  private void buildGui()
  {
    Container   cp;
    JScrollPane jsp;

    createMenu();

    cp = getContentPane();
    cp.setLayout(new BorderLayout());

    m_pnlText = new TextPanel();
    m_pnlText.addListener(this);
    cp.add(m_pnlText,
           BorderLayout.NORTH);

    m_taStatus = new JTextArea(8, 60);
    jsp = new JScrollPane(m_taStatus,
                          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jsp.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createLineBorder(Color.black)));
    cp.add(jsp, BorderLayout.CENTER);

    pack();

    GraphicsUtil.center(this);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private void createMenu()
  {
    ButtonGroup group;
    JMenu       menu;
    JMenuBar    menuBar;
    JMenuItem   menuItem;
    String      sVersion;

    setJMenuBar(menuBar = new JMenuBar());

    menuBar.add(menu = new JMenu("Options"));

    menu.add(menu = new JMenu("Postures"));

    group = new ButtonGroup();

    menuItem = new JRadioButtonMenuItem("Relaxing",
                                        true);
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        if (Posture.RELAXING != m_posture) {
          m_posture = Posture.RELAXING;

          posture("Stand",
                  0.5f,
                  2000,
                  "I am relaxing");
        }
      }
    });
    group.add(menuItem);
    menu.add(menuItem);

    menuItem = new JRadioButtonMenuItem("Ready");
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        if (Posture.READY != m_posture) {
          m_posture = Posture.READY;

          posture("StandInit",
                  0.5f,
                  1500,
                  "I am ready for anything now!");
        }
      }
    });
    group.add(menuItem);
    menu.add(menuItem);

    menuItem = new JRadioButtonMenuItem("Rest");
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        if (Posture.CROUCH != m_posture) {
          m_posture = Posture.CROUCH;

          posture("Crouch",
                  0.5f,
                  1500,
                  "I am exhausted and must sit down");
        }
      }
    });
    group.add(menuItem);
    menu.add(menuItem);

    menuItem = new JRadioButtonMenuItem("Robot");
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        if (Posture.ROBOT != m_posture) {
          m_posture = Posture.ROBOT;

          posture("StandZero",
                  0.5f,
                  2500,
                  "I am a robot! Danger, Will Robinson!");
        }
      }
    });
    group.add(menuItem);
    menu.add(menuItem);

    menuBar.add(Box.createHorizontalGlue());

    menuBar.add(menu = new JMenu("Help"));

    sVersion = VERSION + " (git hash " + GitBuild.GIT_HASH + ")";

    menu.add(GraphicsUtil.createAboutInfoMenuItem("About " + getTitle(),
                                                  this,
                                                  sVersion,
                                                  null,
                                                  GraphicsUtil.CONTACTS));
  }

  private void posture(final String POSTURE_NAME,
                       final float  SPEED,
                       final int    DELAY_MILLIS,
                       final String SPEECH) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          m_srvcPosture.goToPosture(POSTURE_NAME,
                                    SPEED);
        } catch (Exception e) {
          handleException(e);
        }
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(DELAY_MILLIS);

          send(SPEECH);
        } catch (Exception e) {
          handleException(e);
        }
      }
    }).start();
  }

  private void setStatus(String sText) {
    m_taStatus.setText(sText);
  }

  private void startSpeechThread() {
    Thread thread;

    thread = new Thread(new Runnable() {
      @Override
      public void run() {
        String sText = null;

        while (true) {
          synchronized (QUEUE) {
            sText = null;

            if (!QUEUE.isEmpty()) {
              sText = QUEUE.remove(0);
            } else {
              try {
                QUEUE.wait();
              } catch (InterruptedException e) {
              }
            }
          }

          if (null != sText) {
            try {
              m_srvcTextToSpeech.say(sText);
            } catch (Exception e) {
              handleException(e);
            }
          }
        }
      }
    }, "TextToSpeech");
    thread.setDaemon(true);

    thread.start();
  }

  private enum Posture {
    RELAXING,
    READY,
    CROUCH,
    ROBOT;
  }

  public static void main(String[] args) throws Exception
  {
    RufusTextToSpeech frame;

    frame = new RufusTextToSpeech();
    frame.setVisible(true);
  }
}
