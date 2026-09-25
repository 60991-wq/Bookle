package be.esi.prj.bookle.mvp.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GridTest {

    @Test
    void testGridInitialization() {
        Grid grid = new Grid();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                assertEquals(' ', grid.getLetterAt(i, j));
        assertEquals(0, grid.getCurrentRow());
    }

    @Test
    void testSetAndGetLetterAt() {
        Grid grid = new Grid();
        grid.insertWord("AAAAA");
        assertEquals('A', grid.getLetterAt(0, 0));
    }

    @Test
    void testAdvanceRow() {
        Grid grid = new Grid();
        grid.advanceRow();
        assertEquals(1, grid.getCurrentRow());
    }

    @Test
    void testIsFull() {
        Grid grid = new Grid();
        for (int i = 0; i < 5; i++) grid.advanceRow();
        assertTrue(grid.isFull());
    }

    @Test
    void testReset() {
        Grid grid = new Grid();
        grid.insertWord("AAAAA");
        grid.advanceRow();
        grid.reset();
        assertEquals(' ', grid.getLetterAt(0, 0));
        assertEquals(0, grid.getCurrentRow());
    }
}
