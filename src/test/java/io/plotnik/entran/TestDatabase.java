package io.plotnik.entran;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestDatabase {

    @Test
    public void testDatabase() {
        Database db = new Database();
        db.connect();  
    }

}
