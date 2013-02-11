package org.yaz4j;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.exception.*;
import java.util.List;

public class ConnectionTest {

  @Test
  public void testConnection() {
    Connection con = new Connection("z3950.indexdata.dk:210/gils", 0);
    assertNotNull(con);
    try {
      con.setSyntax("sutrs");
      System.out.println("Open connection to z3950.indexdata.dk:210/gils...");
      con.connect();
      ResultSet s = con.search("@attr 1=4 utah",
        Connection.QueryType.PrefixQuery);
      System.out.println("Search for 'utah'...");
      assertNotNull(s);
      assertEquals(s.getHitCount(), 9);
      Record rec = s.getRecord(0);
      assertNotNull(rec);
      byte[] content = rec.getContent();
      // first SUTRS record
      assertEquals(content.length, 1940);
      assertEquals(content[0], 103);
      assertEquals(rec.getSyntax(), "SUTRS");
      assertEquals(rec.getDatabase(), "gils");
      System.out.println("Read all records..");
      // read all records
      int i = 0;
      for (Record r : s) {
        assertNotNull(r);
        System.out.println("Got "+i+" record of type "+r.getSyntax());
        i++;
      }
      System.out.println("Try sorting them...");
      s.sort("yaz", "1=4 >i 1=21 >s");
      System.out.println("Try fetching them all at once...");
      i = 0;
      List<Record> all = s.getRecords(0, (int) s.getHitCount());
      for (Record r : all) {
        System.out.println("getRecords, rec '"+i+"'"+r.getSyntax());
        i++;
      }
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } finally {
      con.close();
    }
  }


  @Test
  public void unsupportedSyntax() {
    System.out.println("Open connection to z3950.loc.gov:7090/voyager...");
    Connection con = new Connection("z3950.loc.gov:7090/voyager", 0);
    try {
      System.out.println("Set syntax to 'rusmarc'");
      con.setSyntax("rusmarc");
      con.connect();
      System.out.println("Search for something that exists...");
      ResultSet set = con.search("@attr 1=7 0253333490",
        Connection.QueryType.PrefixQuery);
      System.out.println("Result set size: " + set.getHitCount());
      System.out.println("Get the first record...");
      Record rec = set.getRecord(0);
      if (rec == null) {
        System.out.println("Record is null");
      } else {
        System.out.print(rec.render());
      }
    } catch (ZoomException ze) {
      //fail(ze.getMessage());
    } finally {
      con.close();
    }
  }

  @Test
  /**
   * This only works with local ztest
   */
  public void recordError() {
    Connection con = new Connection("localhost:9999", 0);
    assertNotNull(con);
    try {
      con.setSyntax("postscript");
      System.out.println("Open connection to localhost:9999...");
      con.connect();
      ResultSet s = con.search("100", Connection.QueryType.PrefixQuery);
      assertNotNull(s);
      assertEquals(s.getHitCount(), 100);
      Record rec = s.getRecord(0);
      fail("We should never get here and get ZoomeException instead");
    } catch (ZoomException ze) {
      // we need more specific exceptions here
      System.out.println(ze.getMessage());
    } finally {
      con.close();
    }
  }

  @Test
  public void testScan() {
    System.out.println("Open connection to z3950cat.bl.uk:9909/BLAC");
    Connection con = new Connection("z3950cat.bl.uk:9909/BLAC", 0);
    try {
      con.connect();
      con.option("number", "20");
      ScanSet set = con.scan("@attr 1=21 \"development\"");
      System.out.println("getSize(): " + set.getSize());
      assertEquals(20, set.getSize());

    } catch (ZoomException ex) {
      fail(ex.getMessage());
    } finally {
      con.close();
    }
  }
}
