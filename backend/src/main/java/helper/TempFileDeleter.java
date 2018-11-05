package helper;

import java.io.File;
import java.io.FilenameFilter;

public class TempFileDeleter {

  public static void deleteTempFiles() {
    final File folder = new File(System.getProperty("user.dir"));
    final File[] files = folder.listFiles( new FilenameFilter() {
      @Override
      public boolean accept( final File dir,
                             final String name ) {
        return name.matches( ".*\\.pdf" );
      }
    } );
    for ( final File file : files ) {
      if ( !file.delete() ) {
        System.err.println( "Can't remove " + file.getAbsolutePath() );
      }
    }
  }

}
