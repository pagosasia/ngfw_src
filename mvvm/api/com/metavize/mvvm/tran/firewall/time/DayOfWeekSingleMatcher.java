/*
 * Copyright (c) 2003-2006 Untangle Networks, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Metavize Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information.
 *
 * $Id$
 */

package com.metavize.mvvm.tran.firewall.time;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.metavize.mvvm.tran.ParseException;
import com.metavize.mvvm.tran.firewall.Parser;
import com.metavize.mvvm.tran.firewall.ParsingConstants;

public final class DayOfWeekSingleMatcher extends DayOfWeekDBMatcher
{
    private static final long serialVersionUID = 8513192448149629420L;

    private final String dayOfWeek;

    private DayOfWeekSingleMatcher( String dayOfWeek )
    {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isMatch( Date when )
    {
        return ( matchCanonicalDayString(dayOfWeek, when) );
    }

    public List<String> toDatabaseList()
    {
        return Collections.nCopies( 1, toString());
    }
        
    public String toString()
    {
        return this.dayOfWeek;
    }

    public static DayOfWeekDBMatcher makeInstance( String dayOfWeek )
    {
        return new DayOfWeekSingleMatcher( dayOfWeek );
    }

    /* This is just for matching a single day */
    static final Parser<DayOfWeekDBMatcher> PARSER = new Parser<DayOfWeekDBMatcher>() 
    {
        public int priority()
        {
            return 10;
        }
        
        public boolean isParseable( String value )
        {
            return !value.contains( ParsingConstants.MARKER_SEPERATOR );
        }
        
        public DayOfWeekDBMatcher parse( String value ) throws ParseException
        {
            if ( !isParseable( value )) {
                throw new ParseException( "Invalid dayOfWeek single matcher '" + value + "'" );
            }
            String val = DayOfWeekDBMatcher.canonicalizeDayName(value.trim());
            if (val == null) {
                throw new ParseException( "Invalid dayOfWeek single matcher '" + value + "'" );
            }
            
            return makeInstance( val );
        }
    };
}

