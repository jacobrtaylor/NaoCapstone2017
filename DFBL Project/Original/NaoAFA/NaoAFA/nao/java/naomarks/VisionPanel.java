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
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

import common.*;

/****************************************************************************
 * This panel shows what Rufus is seeing with his camera, and allows the
 * user to indicate where Rufus should point his head to look.
 *
 * @author Craig Cox (Craig.Cox.1@us.af.mil, c.afrl.rhxs@reacomp.com)
 ****************************************************************************/
public class VisionPanel extends JPanel implements ChangeListener {
  private static final float  PITCH_CENTER_ANGLE = (0.25f - 0.5f) / 2.0f;
  private static final float  PITCH_ANGLE_RANGE = 0.25f - (-0.5f);
  private static final float  YAW_CENTER_ANGLE = 0.0f;
  private static final float  YAW_ANGLE_RANGE = (float) (Math.PI * 2.0 / 3.0);
  private static final Cursor CROSSHAIRS =
      Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

  private final HeadControl HEAD;
  private final Object      DISPLAY_LOCK;

  private boolean       m_bImageAnglesSet = false;
  private boolean       m_bScan = false;
  private float[]       m_fImageAngles = new float[4];
  private BufferedImage m_image;
  private CameraPanel   m_pnlCamera;
  private JSlider       m_sldrPitch;
  private JSlider       m_sldrYaw;

  public VisionPanel(HeadControl head,
                     Object      oDisplayLock) {
    super();

    HEAD         = head;
    DISPLAY_LOCK = oDisplayLock;

    buildGui();
  }

  public void setScan(boolean bScan) {
    m_bScan = bScan;

    m_sldrYaw.setEnabled(!m_bScan);
    m_sldrPitch.setEnabled(!m_bScan);

    m_pnlCamera.setCursor((m_bScan)
                          ? Cursor.getDefaultCursor()
                          : CROSSHAIRS);
  }

  public boolean isScanning() {
    return m_bScan;
  }

  public void updateLandmarkData(float[][] fLandmarkData) {
    m_pnlCamera.updateLandmarkData(fLandmarkData);
  }

  public int[] getSliderValues() {
    return new int[] { m_sldrYaw.getValue(),
                       m_sldrPitch.getValue() };
  }

  public void setSliderValues(int[] nDesiredValues) {
    m_sldrYaw.setValue(nDesiredValues[0]);
    m_sldrPitch.setValue(nDesiredValues[1]);
  }

  public void enableSliders(boolean bEnabled) {
    m_sldrYaw.setEnabled(bEnabled);
    m_sldrPitch.setEnabled(bEnabled);
  }

  public void updateImageData(int    nWidth,
                              int    nHeight,
                              byte[] byPixels,
                              float  fLeftAngle,
                              float  fTopAngle,
                              float  fRightAngle,
                              float  fBottomAngle) {
    synchronized (DISPLAY_LOCK) {
      if (null == m_image
          || m_image.getWidth() != nWidth
          || m_image.getHeight() != nHeight) {
        m_image = new BufferedImage(nWidth,
                                    nHeight,
                                    BufferedImage.TYPE_3BYTE_BGR);
      }

      m_image.getRaster().setDataElements(0,
                                          0,
                                          nWidth,
                                          nHeight,
                                          byPixels);

      m_fImageAngles[0] = fLeftAngle;
      m_fImageAngles[1] = fTopAngle;
      m_fImageAngles[2] = fRightAngle;
      m_fImageAngles[3] = fBottomAngle;

      m_bImageAnglesSet = true;

      repaint();
    }
  }

  private void buildGui() {
    GridBagConstraints gbc = new GridBagConstraints();

    setLayout(new GridBagLayout());

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.insets.right = 5;
    gbc.insets.bottom = 5;

    m_pnlCamera = new CameraPanel();
    m_pnlCamera.setCursor(
        Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    add(m_pnlCamera,
        gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 0.0;
    gbc.weighty = 1.0;
    gbc.insets.right = 0;
    gbc.insets.bottom = 0;

    m_sldrPitch = new JSlider(JSlider.VERTICAL);
    m_sldrPitch.setValue(56);
    m_sldrPitch.addChangeListener(this);
    add(m_sldrPitch,
        gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;

    m_sldrYaw = new JSlider(JSlider.HORIZONTAL);
    m_sldrYaw.setValue(50);
    m_sldrYaw.addChangeListener(this);
    add(m_sldrYaw,
        gbc);
  }

  @Override
  public void stateChanged(ChangeEvent event) {
    float[] fDesiredHeadAngles = new float[2];

    // Get desired head angles using the slider values.
    fDesiredHeadAngles[0] = (0.5f - (m_sldrYaw.getValue() / 100.0f))
                            * YAW_ANGLE_RANGE
                            + YAW_CENTER_ANGLE;
    fDesiredHeadAngles[1] = (0.5f - (m_sldrPitch.getValue() / 100.0f))
                            * PITCH_ANGLE_RANGE
                            + PITCH_CENTER_ANGLE;

    // Set the desired angles.
    HEAD.setHeadAngles(fDesiredHeadAngles);
  }

  private class CameraPanel extends JPanel {
    private final Dimension PREFERRED_SIZE = new Dimension(614, 480);

    private float[]       m_fCameraFov = new float[2];
    private float[]       m_fImageFactors = new float[2];
    private float[][]     m_fLandmarkData = new float[0][3];
    private int[]         m_nOffsets = new int[2];
    private SubImageInfo  m_subImageInfo;

    public CameraPanel() {
      super();

      setFont(new Font(getFont().getName(),
                       Font.BOLD,
                       (int) Math.round(getFont().getSize() * 1.3)));

      addMouseListener(createMouseListener());
    }

    @Override
    public Dimension getPreferredSize() {
      return PREFERRED_SIZE;
    }

    @Override
    public void paint(Graphics g) {
      synchronized (DISPLAY_LOCK) {
        float           fAvailablePixelsPerRad;
        float           fXFactor;
        float           fYFactor;
        int[]           nLoc;
        AffineTransform xform;
        Dimension       dim;
        Graphics2D      g2;
        String          sLandmark;

        super.paint(g);

        if (null == m_image
            || !m_bImageAnglesSet) {
          return;
        }

        g2 = (Graphics2D) g;

        dim = getSize();

        m_fCameraFov[0] = m_fImageAngles[0] - m_fImageAngles[2];
        m_fCameraFov[1] = m_fImageAngles[1] - m_fImageAngles[3];

        fAvailablePixelsPerRad = Math.min(dim.width / m_fCameraFov[0],
                                          dim.height / m_fCameraFov[1]);

        m_fImageFactors[0] = fAvailablePixelsPerRad
                             * m_fCameraFov[0]
                             / m_image.getWidth();
        m_fImageFactors[1] = fAvailablePixelsPerRad
                             * m_fCameraFov[1]
                             / m_image.getHeight();

        m_nOffsets[0] =
            (int) Math.round((dim.width
                              - m_fImageFactors[0] * m_image.getWidth())
                             / 2.0);
        m_nOffsets[1] =
            (int) Math.round((dim.height
                              - m_fImageFactors[1] * m_image.getHeight())
                             / 2.0);

        g2.translate(m_nOffsets[0],
                     m_nOffsets[1]);

        xform = g2.getTransform();

        g2.scale(m_fImageFactors[0],
                 m_fImageFactors[1]);

        g.drawImage(m_image,
                    0,
                    0,
                    null);

        if (null != m_subImageInfo
            && null != m_subImageInfo.IMAGE
            && m_subImageInfo.m_nTimer > 0) {
          // Draw the subimage at double size.
          g2.setTransform(xform);
          g2.scale(2.0f, 2.0f);

          g.drawImage(m_subImageInfo.IMAGE,
                      0,
                      0,
                      null);

          g.setColor(Color.red);
          g.fillRect(m_subImageInfo.CENTER_X,
                     m_subImageInfo.CENTER_Y,
                     1,
                     1);

          m_subImageInfo.m_nTimer--;
        }

        g2.setTransform(xform);

        fXFactor = dim.width / m_fCameraFov[0];
        fYFactor = dim.height / m_fCameraFov[1];

        g.setColor(Color.red);

        for (float[] fLandmark : m_fLandmarkData) {
          nLoc = angle2Pixel(-fLandmark[0],
                             fLandmark[1]);

          // Landmark data from the camera seems to be a little off. A fudge
          // of two pixels down and two pixels right seems to fix it for this
          // particular robot.
          nLoc[0] += 2;
          nLoc[1] += 2;

          g2.setStroke(new BasicStroke(2.0f));

          g.drawArc(nLoc[0] - 25,
                    nLoc[1] - 25,
                    50,
                    50,
                    0,
                    360);

          sLandmark = "" + (int) fLandmark[2];


          nLoc[0] -= g.getFontMetrics().stringWidth(sLandmark) / 2;
          nLoc[1] += g.getFontMetrics().getAscent() / 2;

          g.drawString(sLandmark,
                       nLoc[0],
                       nLoc[1]);
        }

        g.setColor(Color.black);

        g2.setStroke(new BasicStroke(1.0f));

        drawCross(g,
                  0.0f,
                  0.0f);
      }
    }

    private void debug(String sName,
                       String sData) {
      String sOutput;

      sOutput = String.format("%s%s: %s",
                              "                   ".substring(sName.length()),
                              sName,
                              sData);
      System.out.println(sOutput);
    }

    private void drawCross(Graphics g,
                           float    fX,
                           float    fY) {
      int[] nLoc;

      nLoc = angle2Pixel(fX,
                         fY);

      g.drawLine(nLoc[0],
                 nLoc[1] - 5,
                 nLoc[0],
                 nLoc[1] + 5);
      g.drawLine(nLoc[0] - 5,
                 nLoc[1],
                 nLoc[0] + 5,
                 nLoc[1]);
    }

    private int[] angle2Pixel(float   fXRad,
                              float   fYRad) {
      int [] nPixels = new int[2];

      nPixels[0] = (int) ((fXRad - m_fImageAngles[2])
                          / m_fCameraFov[0]
                          * m_image.getWidth()
                          * m_fImageFactors[0]
                          + 0.5);
      nPixels[1] = (int) ((fYRad - m_fImageAngles[3])
                          / m_fCameraFov[1]
                          * m_image.getHeight()
                          * m_fImageFactors[1]
                          + 0.5);

      return nPixels;
    }

    private float[] pixel2Angle(int     nXPixel,
                                int     nYPixel) {
      float [] fAngles = new float[2];

      fAngles[0] = (0.5f - nXPixel + m_nOffsets[0] + 2) 
                   * m_fCameraFov[0]
                   / m_image.getWidth()
                   / m_fImageFactors[0]
                   - m_fImageAngles[2];
      fAngles[1] = (nYPixel - m_nOffsets[1] - 0.5f - 2) 
                   * m_fCameraFov[1]
                   / m_image.getHeight()
                   / m_fImageFactors[1]
                   + m_fImageAngles[3];

      return fAngles;
    }

    public void updateLandmarkData(float[][] fLandmarkData) {
      synchronized (DISPLAY_LOCK) {
        if (null == fLandmarkData) {
          m_fLandmarkData = new float[0][3];
        } else {
          m_fLandmarkData = new float[fLandmarkData.length][3];

          for (int i = 0; i < m_fLandmarkData.length; i++) {
            for (int j = 0; j < 3; j++) {
              m_fLandmarkData[i][j] = fLandmarkData[i][j];
            }
          }
        }

        repaint();
      }
    }

    private MouseListener createMouseListener() {
      return new MouseAdapter() {
        @Override
  	public void mouseClicked(MouseEvent event) {
          float[]       fClickAngles;
          float[]       fDesiredAngles = new float[2];
          float[]       fHeadAngles;
          int[]         nClickLocation = new int[2];
          int[]         nDesiredSliderValues = new int[2];
          int[]         nRGB;
          int[]         nSubImageLimits = new int[4];
          BufferedImage subImage = null;

          // Return without doing anything if scanning.
          if (isScanning()) {
            return;
          }

          fClickAngles = pixel2Angle(event.getPoint().x,
                                     event.getPoint().y);

          synchronized (DISPLAY_LOCK) {
            if (null != m_image) {
              nClickLocation[0] = (int) ((m_fImageAngles[0] - fClickAngles[0])
                                         * m_image.getWidth()
                                         / m_fCameraFov[0]);
              nClickLocation[1] = (int) ((fClickAngles[1] - m_fImageAngles[3])
                                         * m_image.getHeight()
                                         / m_fCameraFov[1]);

              nSubImageLimits[0] = Math.max(0,
                                            nClickLocation[0] - 30);
              nSubImageLimits[1] = Math.max(0,
                                            nClickLocation[1] - 30);
              nSubImageLimits[2] = Math.min(m_image.getWidth() - 1,
                                            nClickLocation[0] + 30);
              nSubImageLimits[3] = Math.min(m_image.getHeight() - 1,
                                            nClickLocation[1] + 30);

              subImage = new BufferedImage(
                  nSubImageLimits[2] - nSubImageLimits[0] + 1,
                  nSubImageLimits[3] - nSubImageLimits[1] + 1,
                  BufferedImage.TYPE_INT_ARGB);

              nRGB = m_image.getRGB(
                  nSubImageLimits[0],
                  nSubImageLimits[1],
                  nSubImageLimits[2] - nSubImageLimits[0] + 1,
                  nSubImageLimits[3] - nSubImageLimits[1] + 1,
                  null,
                  0,
                  nSubImageLimits[2] - nSubImageLimits[0] + 1);

              subImage.setRGB(0,
                              0,
                              subImage.getWidth(),
                              subImage.getHeight(),
                              nRGB,
                              0,
                              subImage.getWidth());

              m_subImageInfo = new SubImageInfo(subImage,
                                                nClickLocation,
                                                nSubImageLimits);
            } else {
              m_subImageInfo = null;
            }
          }

          fHeadAngles = HEAD.getHeadAngles();

          fDesiredAngles[0] = fHeadAngles[0] + fClickAngles[0];
          fDesiredAngles[1] = fHeadAngles[1] + fClickAngles[1];

          m_sldrYaw.setValue(
              (int) ((0.5f - (fDesiredAngles[0] - YAW_CENTER_ANGLE)
                             / YAW_ANGLE_RANGE)
                     * 100.0f + 0.5f));
          m_sldrPitch.setValue(
              (int) ((0.5f - (fDesiredAngles[1] - PITCH_CENTER_ANGLE)
                             / PITCH_ANGLE_RANGE)
                     * 100.0f + 0.5f));
        }
      };
    }

    private class SubImageInfo {
      final int           CENTER_X;
      final int           CENTER_Y;
      final BufferedImage IMAGE;

      int m_nTimer = 20;

      private SubImageInfo(BufferedImage image,
                           int[]         nClickLocation,
                           int[]         nImageLimits) {
        IMAGE = image;

        CENTER_X = nClickLocation[0] - nImageLimits[0] + 1;
        CENTER_Y = nClickLocation[1] - nImageLimits[1] + 1;
      }
    }
  }
}
