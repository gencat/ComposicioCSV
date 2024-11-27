package cat.gencat.ctti.std.csv.utils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.IOException;

public class TraceUtils {
    
    public static void writeTrace(String file, String text) {

		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date());
		String timeStampName = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String path = "/data/STD/logs/" + timeStampName + "_" + file + ".log";
		try{
			ArrayList<String> content = new ArrayList<String>();
			content.add(timeStamp + "  " + text);
			Path out = Paths.get(path);
			Files.write(out,content,StandardOpenOption.CREATE,StandardOpenOption.WRITE, StandardOpenOption.APPEND);
		}  
		catch( IOException e ){
			// File writing/opening failed at some stage.
		}
	  }
}