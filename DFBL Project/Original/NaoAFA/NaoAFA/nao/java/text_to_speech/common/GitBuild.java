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

import java.io.*;

/****************************************************************************
 * This class provides a constant to uniquely identify a specific build of
 * the appliation using the hash of the GIT commit at the time of the build.
 *
 * @author Craig Cox (Craig.Cox.1@us.af.mil, c.afrl.rhxs@reacomp.com)
 ****************************************************************************/
public class GitBuild {
  /**
   * The hash of the Git commit used for this build. If there are modified
   * files that are not part of the commit, an asterisk follows the hash.
   */
  public static final String GIT_HASH;
  static {
    BufferedReader br;
    InputStream    is;
    String         sHash;

    try {
      // The hash (and possible trailing asterisk) are located in a file named
      // "BUILD_INFO" in the top directory of the JAR file.
      is = ClassLoader.getSystemResourceAsStream("BUILD_INFO");

      // Wrap the resource in a buffered reader and read the Git hash.
      br = new BufferedReader(new InputStreamReader(is));
      sHash = br.readLine();

      // Close the resource.
      is.close();
    } catch (Exception e) {

      // If the Git hash cannot be found for any reason, the hash is
      // "unknown."
      sHash = "unknown";
    }

    // Set the final hash value. 
    GIT_HASH = sHash;
  }
}
