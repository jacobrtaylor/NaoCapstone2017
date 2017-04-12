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

/****************************************************************************
 * The interface for handling exceptions. A class that implements this
 * interface must be able to handle a passed exception in some manner. Use
 * of this interface simplifies the coding of methods that cannot throw an
 * exception, such as the run() method of a new Runnable object created in
 * multi-threaded applications.
 *
 * @author Craig Cox (Craig.Cox.1@us.af.mil, c.afrl.rqva@reacomp.com)
 *
 * $Rev:: 733                   $: Revision of last commit
 * $Author:: mavlab             $: Author of last commit
 * $Date:: 2012-08-29 15:14:49 #$: Date of last commit
 ****************************************************************************/
public interface ExceptionHandler
{
  /**
   * Handle the passed exception in some manner. For non-GUI applications this
   * can be as simple as printing out a stack trace or ignoring the
   * exception (not recommended). For GUI applications the message of the
   * exception could be displayed in a label, text field, or text area.
   *
   * @param e The exception to be output (or ignored).
   */
  public void handleException(Exception e);
}
