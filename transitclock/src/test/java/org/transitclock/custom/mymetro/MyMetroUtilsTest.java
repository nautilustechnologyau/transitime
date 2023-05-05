package org.transitclock.custom.mymetro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MyMetroUtilsTest {

    @Test
    public void testStripAgencyId() {
        String idWithAgency = null;

        assertNull(MyMetroUtils.stripAgencyId(idWithAgency));

        idWithAgency = "";
        assertEquals(idWithAgency, MyMetroUtils.stripAgencyId(idWithAgency));

        idWithAgency = "_";
        assertEquals(idWithAgency, MyMetroUtils.stripAgencyId(idWithAgency));

        idWithAgency = "abc";
        assertEquals(idWithAgency, MyMetroUtils.stripAgencyId(idWithAgency));

        idWithAgency = "abc_";
        assertEquals(idWithAgency, MyMetroUtils.stripAgencyId(idWithAgency));

        idWithAgency = "a_bc";
        assertEquals("bc", MyMetroUtils.stripAgencyId(idWithAgency));

        idWithAgency = "a_b";
        assertEquals("b", MyMetroUtils.stripAgencyId(idWithAgency));

        idWithAgency = "a_b_c";
        assertEquals("b_c", MyMetroUtils.stripAgencyId(idWithAgency));
    }
}