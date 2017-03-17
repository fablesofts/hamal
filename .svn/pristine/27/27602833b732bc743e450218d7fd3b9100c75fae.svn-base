package com.fable.hamal.log.event;


import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fable.hamal.shuttle.communication.event.Event;
import com.fable.hamal.shuttle.communication.event.EventHandler;

public class ConifgInnerLogEventHandler implements EventHandler {
   
    public static enum LevelEnum {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
	
	public Object handle(Event event) {
	    
		if(event.getType().equals(TrackerEventTypes.SETINNERLOGLEVEL)){
		    Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            LevelEnum levelEnum = LevelEnum.valueOf(event.getData().toString());
            switch (levelEnum)
            {
                case TRACE:
                    root.setLevel(Level.TRACE);
                    break;
                case DEBUG:
                    root.setLevel(Level.DEBUG);
                    break;
                case INFO:
                    root.setLevel(Level.INFO);
                    break;
                case WARN:
                    root.setLevel(Level.WARN);
                    break;
                case ERROR:
                    root.setLevel(Level.ERROR);
                    break;
            }
		}else if(event.getType().equals(TrackerEventTypes.GETINNERLOGLEVEL)){
	        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	        return root.getLevel().toString();
		}
		return null;
	}

}
