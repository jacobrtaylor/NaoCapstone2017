//////////////////////////////////////////////////////////////////////////////
//                                                                          //
// This software is a work of the U.S. Government. It is not subject to     //
// copyright protection and is in the public domain. It may be used as-is   //
// or modified and re-used. The author and the Air Force Research           //
// Laboratory would appreciate credit if this software or parts of it are   //
// used or modified for re-use.                                             //
//                                                                          //
//////////////////////////////////////////////////////////////////////////////

/****************************************************************************
 * This interface provides a means to get and set Rufus head (yaw/pitch)
 * angles.
 *
 * @author Craig Cox (Craig.Cox.1@us.af.mil, c.afrl.rhxs@reacomp.com)
 ****************************************************************************/
public interface HeadControl {
  public void setHeadAngles(float[] fDesiredHeadAngles);
  public float[] getHeadAngles();
}
