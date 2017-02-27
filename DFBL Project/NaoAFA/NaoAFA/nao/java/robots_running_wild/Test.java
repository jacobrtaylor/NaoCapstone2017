import java.util.*;

import com.aldebaran.qi.*;
import com.aldebaran.qi.helper.*;
import com.aldebaran.qi.helper.proxies.*;

public class Test {
  private static final int    DEFAULT_ROBOT_PORT = 9559;
  private static final String DEFAULT_ROBOT_ADDRESS = "rufus";

  private final ALAutonomousLife    AUTONOMOUS_LIFE_SERVICE;
  private final ALAutonomousMoves   AUTONOMOUS_MOVES_SERVICE;
  private final ALMemory            MEMORY_SERVICE;
  private final ALMotion            MOTION_SERVICE;
  private final ALRobotPosture      POSTURE_SERVICE;
  private final ALSpeechRecognition SPEECH_RECOGNITION_SERVICE;
  private final ALTextToSpeech      TEXT_TO_SPEECH_SERVICE;
  private final Application         APPLICATION;

  private boolean m_bListening = false;
  private long    m_lTouchedSubscriptionId;
  private long    m_lWordRecognizedSubscriptionId;

  public Test(String sRobotUrl) throws Exception {
    ArrayList<String>   alCommands = new ArrayList<>();
    Session             session;

    // Create a new Application.
    APPLICATION = new Application(new String[0],
                                  sRobotUrl);

    // Start the application, which creates a session and connects it to the
    // robot.
    APPLICATION.start();

    // Get the created session.
    session = APPLICATION.session();

    // Create required services.
    AUTONOMOUS_MOVES_SERVICE   = new ALAutonomousMoves(session);
    AUTONOMOUS_LIFE_SERVICE    = new ALAutonomousLife(session);
    MEMORY_SERVICE             = new ALMemory(session);
    MOTION_SERVICE             = new ALMotion(session);
    SPEECH_RECOGNITION_SERVICE = new ALSpeechRecognition(session);
    TEXT_TO_SPEECH_SERVICE     = new ALTextToSpeech(session);
    POSTURE_SERVICE            = new ALRobotPosture(session);

    System.out.println("Available postures:");
    for (String sPosture : POSTURE_SERVICE.getPostureList()) {
      System.out.println("  " + sPosture);
    }

    POSTURE_SERVICE.goToPosture("Stand",
                                0.5f);

    Thread.sleep(3000);

    POSTURE_SERVICE.goToPosture("StandInit",
                                0.5f);

    Thread.sleep(3000);

    POSTURE_SERVICE.goToPosture("Stand",
                                0.5f);

    MOTION_SERVICE.setBreathEnabled("Body",
                                    new Boolean(false));
    MOTION_SERVICE.setBreathEnabled("Head",
                                    new Boolean(false));

    // Specify the words that the robot will recognize.
    alCommands.add("forward");
    alCommands.add("backward");
    alCommands.add("left");
    alCommands.add("right");
    alCommands.add("stop");
    alCommands.add("faster");
    alCommands.add("slower");
    alCommands.add("exit");
    SPEECH_RECOGNITION_SERVICE.setVocabulary(alCommands,
                                             false);

    // Disable expressive listening to prevent the robot from falling down
    // due to autonomous moves while we are manually commanding the robot to
    // move.
    AUTONOMOUS_MOVES_SERVICE.setExpressiveListeningEnabled(false);


System.out.println("AutonomousLife state: " + AUTONOMOUS_LIFE_SERVICE.getState());

    // Enable listening.
    enableListening(true);

    subscribeToTouchedEvent();

    // Keep the application running until the stop() method is called.
    APPLICATION.run();

    // Clean up when done.
    enableListening(false);
    MEMORY_SERVICE.unsubscribeToEvent(m_lWordRecognizedSubscriptionId);
    MEMORY_SERVICE.unsubscribeToEvent(m_lTouchedSubscriptionId);
  }

  public void enableListening(final boolean ENABLE) throws Exception {
    if (ENABLE) {
      // The string "VoiceCommand" is the name used to identify our
      // subscription.
      TEXT_TO_SPEECH_SERVICE.say("I am listening now");
      SPEECH_RECOGNITION_SERVICE.subscribe("VoiceCommand");
      subscribeToWordRecognizedEvent();

      m_bListening = true;
    } else {
      SPEECH_RECOGNITION_SERVICE.unsubscribe("VoiceCommand");
      MEMORY_SERVICE.unsubscribeToEvent(m_lWordRecognizedSubscriptionId);
      TEXT_TO_SPEECH_SERVICE.say("I am no longer listening.");

      m_bListening = false;

      handleCommand("stop");
    }
  }

  // In Java 8, this could be replaced with a function that uses lambda
  // expressions instead.
  private void subscribeToWordRecognizedEvent() throws Exception {
    EventCallback<List<Object>> callback;
    long                        lId;

    callback = new EventCallback<List<Object>>() {
      @Override
      public void onEvent(List<Object> lstInput) throws CallError,
                                                        InterruptedException {
        System.out.println("WordRecognizedEvent:");
        for (Object o : lstInput) {
          System.out.println("  " + o);
        }

        if (   m_bListening
            && ((Float) lstInput.get(1)) > 0.5f) {
          // Do something with the recognized word.
          handleCommand((String) lstInput.get(0));
        }
      }
    };

    lId = MEMORY_SERVICE.subscribeToEvent("WordRecognized",
                                          callback);

    m_lWordRecognizedSubscriptionId = lId;
  }

//  // Lambda expressions are not allowed before Java 8.
//  @SuppressWarnings("unchecked")
//  private void subscribeToWordRecognizedEvent() throws Exception {
//    long lId;
//
//    lId = MEMORY_SERVICE.subscribeToEvent(
//        "WordRecognized",
//        (lstInput)-> {
//          if (m_bListening) {
//            // Do something with the recognized word.
//            handleCommand((String) ((List<Object>) lstInput).get(0));
//          }
//        });
//
//    m_lWordRecognizedSubscriptionId = lId;
//  }

  // In Java 8, this could be replaced with a function that uses lambda
  // expressions instead.
  private void subscribeToTouchedEvent() throws Exception {
    EventCallback<Float> callback;

    callback = new EventCallback<Float>() {
      @Override
      public void onEvent(Float touched) throws CallError,
                                                InterruptedException {
        if (touched > 0f) {
          System.out.println("Touched");

          try {
            enableListening(!m_bListening);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    };

    m_lTouchedSubscriptionId = MEMORY_SERVICE.subscribeToEvent(
        "MiddleTactilTouched",
        callback);
  }

//  // Lambda expressions are not allowed before Java 8.
//  private void subscribeToTouchedEvent() throws Exception {
//    m_lTouchedSubscriptionId = MEMORY_SERVICE.subscribeToEvent(
//        "MiddleTactilTouched",
//        (touched)-> {
//          if ((float) touched > 0f) {
//            System.out.println("Touched");
//
//            try {
//              enableListening(!m_bListening);
//            } catch (Exception e) {
//              e.printStackTrace();
//            }
//          }
//        }
//    );
//  }

  private void handleCommand(String sCommand) throws CallError,
                                                     InterruptedException {
    float fTheta = 0.0f;
    float fX     = 0.0f;
    float fY     = 0.0f;

    if ("exit".equals(sCommand)) {
      APPLICATION.stop();

      return;
    }

    if ("forward".equals(sCommand)) {
      fX = 0.8f;
    } else if ("backward".equals(sCommand)) {
      fX = -0.8f;
    } else if ("left".equals(sCommand)) {
      fTheta = 0.4f;
    } else if ("right".equals(sCommand)) {
      fTheta = -0.4f;
    } else if ("stop".equals(sCommand)) {
      fX = 0.0f;
      fY = 0.0f;
      fTheta = 0.0f;
    } else if ("faster".equals(sCommand)) {
      fX += 0.2f * Math.signum(fX);
    } else if ("slower".equals(sCommand)) {
      fX -= 0.2f * Math.signum(fX);
    }

    MOTION_SERVICE.moveToward(fX,
                              fY,
                              fTheta);

    if ("stop".equals(sCommand)) {
      POSTURE_SERVICE.goToPosture("Stand",
                                  0.5f);
    }
  }

  public static void main(String... args) throws Exception {
    int    nPort;
    String sAddress;
    String sUrl;
    Test   test;

    sAddress = (args.length > 0)
                ? args[0]
                : DEFAULT_ROBOT_ADDRESS;
    nPort = (args.length > 1)
                ? Integer.parseInt(args[1])
                : DEFAULT_ROBOT_PORT;

    sUrl = "tcp://" + sAddress + ":" + nPort;

    test = new Test(sUrl);
  }
}
