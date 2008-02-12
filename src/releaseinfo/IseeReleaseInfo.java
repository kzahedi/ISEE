/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Tue Feb 12 00:16:12 CET 2008 */
package releaseinfo;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class IseeReleaseInfo {


   /** buildDate (set during build process to 1202771772028L). */
   private static Date buildDate = new Date(1202771772028L);

   /**
    * Get buildDate (set during build process to Tue Feb 12 00:16:12 CET 2008).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /**
    * Get buildNumber (set during build process to 1299).
    * @return int buildNumber
    */
   public static final int getBuildNumber() { return 1299; }


   /** compiledWith (set during build process to "java                                             1.6"). */
   private static String compiledWith = "java                                             1.6";

   /**
    * Get compiledWith (set during build process to "java                                             1.6").
    * @return String compiledWith
    */
   public static final String getCompiledWith() { return compiledWith; }


   /** compiledBy (set during build process to "zahedi"). */
   private static String compiledBy = "zahedi";

   /**
    * Get compiledBy (set during build process to "zahedi").
    * @return String compiledBy
    */
   public static final String getCompiledBy() { return compiledBy; }


   /** project (set during build process to "ISEE"). */
   private static String project = "ISEE";

   /**
    * Get project (set during build process to "ISEE").
    * @return String project
    */
   public static final String getProject() { return project; }


   /** revisionNum (set during build process to -1). */
   private static Integer revisionNum = new Integer(-1);

   /**
    * Get revisionNum (set during build process to -1).
    * @return Integer revisionNum
    */
   public static final Integer getRevisionNum() { return revisionNum; }


   /** version (set during build process to "1.3"). */
   private static String version = "1.3";

   /**
    * Get version (set during build process to "1.3").
    * @return String version
    */
   public static final String getVersion() { return version; }

}
