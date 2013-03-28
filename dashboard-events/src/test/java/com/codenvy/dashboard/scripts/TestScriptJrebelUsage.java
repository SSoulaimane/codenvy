/*
 *    Copyright (C) 2013 eXo Platform SAS.
 *
 *    This is free software; you can redistribute it and/or modify it
 *    under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation; either version 2.1 of
 *    the License, or (at your option) any later version.
 *
 *    This software is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this software; if not, write to the Free
 *    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *    02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.dashboard.scripts;

import com.codenvy.dashboard.scripts.util.Event;
import com.codenvy.dashboard.scripts.util.LogGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a>
 */
public class TestScriptJrebelUsage extends BasePigTest
{
   @Test
   public void testJRebelEligible() throws Exception
   {
      List<Event> events = new ArrayList<Event>();
      events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project2", "type", false).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project3", "type", false).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project4", "type", false).withDate("2010-10-01").build());

      File log = LogGenerator.generateLog(events);

      executePigScript(ScriptType.EVENT_COUNT_JREBEL_USAGE, log, new String[][]{{Constants.FROM_DATE, "20101001"},
         {Constants.TO_DATE, "20101001"}});

      FileObject fileObject = ScriptType.EVENT_COUNT_JREBEL_USAGE.createFileObject(BASE_DIR, 20101001, 20101001);

      Long value = (Long)fileObject.getValue();
      Assert.assertEquals(value, Long.valueOf(4));
   }

   @Test
   public void testJRebelEligibleDistinction() throws Exception
   {
      List<Event> events = new ArrayList<Event>();
      events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project3", "type", false).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user2", "ws1", "session", "project1", "type", false).withDate("2010-10-01").build());

      File log = LogGenerator.generateLog(events);

      executePigScript(ScriptType.EVENT_COUNT_JREBEL_USAGE, log, new String[][]{{Constants.FROM_DATE, "20101001"},
         {Constants.TO_DATE, "20101001"}});

      FileObject fileObject = ScriptType.EVENT_COUNT_JREBEL_USAGE.createFileObject(BASE_DIR, 20101001, 20101001);

      Long value = (Long)fileObject.getValue();
      Assert.assertEquals(value, Long.valueOf(3));
   }

   @Test
   public void testDetailedJRebelUsageDistinction() throws Exception
   {
      List<Event> events = new ArrayList<Event>();
      events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", true).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project1", "type", true).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project4", "type", true).withDate("2010-10-01").build());

      File log = LogGenerator.generateLog(events);

      executePigScript(ScriptType.DETAILS_JREBEL_USAGE, log, new String[][]{{Constants.FROM_DATE, "20101001"},
         {Constants.TO_DATE, "20101001"}});

      FileObject fileObject = ScriptType.DETAILS_JREBEL_USAGE.createFileObject(BASE_DIR, 20101001, 20101001);

      Properties props = (Properties)fileObject.getValue();
      Assert.assertEquals(props.getProperty("true"), "2");
      Assert.assertEquals(props.getProperty("false"), "1");
   }

   @Test
   public void testDetailedJRebelUsage() throws Exception
   {
      List<Event> events = new ArrayList<Event>();
      events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project1", "type", false).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user1", "ws", "session", "project2", "type", true).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project3", "type", true).withDate("2010-10-01").build());
      events.add(Event.Builder.createJRebelUsageEvent("user2", "ws", "session", "project4", "type", true).withDate("2010-10-01").build());

      File log = LogGenerator.generateLog(events);

      executePigScript(ScriptType.DETAILS_JREBEL_USAGE, log, new String[][]{{Constants.FROM_DATE, "20101001"},
         {Constants.TO_DATE, "20101001"}});

      FileObject fileObject = ScriptType.DETAILS_JREBEL_USAGE.createFileObject(BASE_DIR, 20101001, 20101001);

      Properties props = (Properties)fileObject.getValue();
      Assert.assertEquals(props.getProperty("true"), "3");
      Assert.assertEquals(props.getProperty("false"), "1");
   }
}
